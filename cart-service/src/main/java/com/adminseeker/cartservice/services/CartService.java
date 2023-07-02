package com.adminseeker.cartservice.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adminseeker.cartservice.entities.Cart;
import com.adminseeker.cartservice.entities.CartRequest;
import com.adminseeker.cartservice.entities.CartResponse;
import com.adminseeker.cartservice.entities.Item;
import com.adminseeker.cartservice.entities.ItemResponse;
import com.adminseeker.cartservice.exceptions.LoginError;
import com.adminseeker.cartservice.exceptions.ResourceNotFound;
import com.adminseeker.cartservice.exceptions.ResourceUpdateError;
import com.adminseeker.cartservice.proxies.EmailRequest;
import com.adminseeker.cartservice.proxies.ProductResponse;
import com.adminseeker.cartservice.proxies.QuantityResponse;
import com.adminseeker.cartservice.proxies.User;
import com.adminseeker.cartservice.proxies.Variant;
import com.adminseeker.cartservice.repository.CartRepo;

@Service
@Transactional
public class CartService {
    
    @Autowired
    CartRepo repo;

    @Autowired
    ProductServiceRequest productServiceRequest;

    @Autowired
    InventoryServiceRequest inventoryServiceRequest;

    @Autowired
    UserServiceRequest userServiceRequest;

    public Cart addItems(Map<String,String> headers,CartRequest cartrequest){
        User user = userServiceRequest.getUserByEmail(EmailRequest.builder().email(headers.get("x-auth-user-email")).build(), headers).orElseThrow(()-> new ResourceNotFound("User Not Found!"));
        Long userId = cartrequest.getUserId();
        if(!userId.equals(user.getUserId()))  throw new LoginError("Unauthorised User!");
        Item item = cartrequest.getItem();
        if(item==null) throw new ResourceUpdateError("Nothing to Update!");
        Cart cartdb = repo.findByUserId(userId).orElse(null);
        if(cartdb==null) {
            cartdb = new Cart();
            cartdb.setUserId(userId);
        }
        if(userId!=cartdb.getUserId()) throw new LoginError("Unauthorised User!");
        ProductResponse productResponse = productServiceRequest.getProductById(item.getProductId()).orElseThrow(()->new ResourceNotFound("Product Not Found!"));
        Variant variantResponse = null;
        Boolean isVariantPresent=false;
        if(productResponse.getProduct().getVariants()!=null){
            for(Variant v : productResponse.getProduct().getVariants()){
                if(v.getVariantId()==item.getVariantId()){
                    variantResponse=v;
                    isVariantPresent=true;
                    break;
                }
            }
        }
        if((productResponse.getProduct().getVariants()!=null && productResponse.getProduct().getVariants().size()!=0) && (item.getVariantId()==null || !isVariantPresent)) throw new ResourceUpdateError("Incorrect Variant!");
        if (item.getVariantId()!=null && !isVariantPresent) throw new ResourceNotFound("Variant Not Found!");
        QuantityResponse quantityResponse = null;
        if(variantResponse!=null){
            quantityResponse=inventoryServiceRequest.getVariantQuantityBySkucode(productResponse.getProduct().getSkucode(), variantResponse.getVariantSkucode()).orElseThrow(()->new ResourceNotFound("Variant Not Found"));
        }else{
            quantityResponse=inventoryServiceRequest.getProductQuantityBySkucode(productResponse.getProduct().getSkucode()).orElseThrow(()->new ResourceNotFound("Product Not Found!"));

        }
        if(item.getQuantity()<1) throw new ResourceUpdateError("Minimum 1 quantity should be selected!");
        if(item.getQuantity()>quantityResponse.getQuantity()) throw new ResourceUpdateError("Out of Stock!");
        List<Item> items=null;
        if(cartdb.getItems()==null){
            items=new ArrayList<Item>();
        }else{
            items = cartdb.getItems();
        }
        item.setItemId(ObjectId.get().toString());

        Boolean isItemPresent=false;
        for(Item i : items){
            if(i.getProductId().equals(item.getProductId()) && isVariantPresent && i.getVariantId().equals(item.getVariantId())){
                isItemPresent=true;
                break;
            } else if (!isVariantPresent && i.getProductId().equals(item.getProductId())){
                isItemPresent=true;
                break;
            }
        }
        if(isItemPresent) throw new ResourceUpdateError("Item Already Exists In The Cart!");
        items.add(item);
        cartdb.setItems(items);
        return repo.save(cartdb);
    }


    public Cart updateItemById(Map<String,String> headers, CartRequest cartrequest,String itemId){
        User user = userServiceRequest.getUserByEmail(EmailRequest.builder().email(headers.get("x-auth-user-email")).build(), headers).orElseThrow(()-> new ResourceNotFound("User Not Found!"));
        Long userId = cartrequest.getUserId();
        if(!userId.equals(user.getUserId()))  throw new LoginError("Unauthorised User!");
        Item item = cartrequest.getItem();
        Cart cartdb = repo.findByUserId(userId).orElseThrow(()->new ResourceNotFound("Cart Not Found!"));
        if(userId!=cartdb.getUserId()) throw new LoginError("Unauthorised User!");
        ProductResponse productResponse = productServiceRequest.getProductById(item.getProductId()).orElseThrow(()->new ResourceNotFound("Product Not Found!"));
        Variant variantResponse = null;
        Boolean isVariantPresent=false;
        if(productResponse.getProduct().getVariants()!=null){
            for(Variant v : productResponse.getProduct().getVariants()){
                if(v.getVariantId()==item.getVariantId()){
                    variantResponse=v;
                    isVariantPresent=true;
                    break;
                }
            }
        }
        if((productResponse.getProduct().getVariants()!=null && productResponse.getProduct().getVariants().size()!=0) && (item.getVariantId()==null || !isVariantPresent)) throw new ResourceUpdateError("Incorrect Variant!");
        if (item.getVariantId()!=null && !isVariantPresent) throw new ResourceNotFound("Variant Not Found!");
        QuantityResponse quantityResponse = null;
        if(variantResponse!=null){
            quantityResponse=inventoryServiceRequest.getVariantQuantityBySkucode(productResponse.getProduct().getSkucode(), variantResponse.getVariantSkucode()).orElseThrow(()->new ResourceNotFound("Variant Not Found"));
        }else{
            quantityResponse=inventoryServiceRequest.getProductQuantityBySkucode(productResponse.getProduct().getSkucode()).orElseThrow(()->new ResourceNotFound("Product Not Found!"));

        }
        if(item.getQuantity()>quantityResponse.getQuantity()) throw new ResourceUpdateError("Out of Stock!");
        if(item.getQuantity()<1) throw new ResourceUpdateError("Minimum 1 quantity should be selected!");
        List<Item> items = cartdb.getItems();
        
        Boolean isItemPresent=false;
        for(Item i : items){
            if(i.getProductId().equals(item.getProductId()) && isVariantPresent && i.getVariantId().equals(item.getVariantId()) && !i.getItemId().equals(itemId)){
                isItemPresent=true;
                break;
            } else if (!isVariantPresent && i.getProductId().equals(item.getProductId()) && !i.getItemId().equals(itemId)){
                isItemPresent=true;
                break;
            }
        }
        if(isItemPresent) throw new ResourceUpdateError("Item Already Exists In The Cart!");
        
        Boolean isPresent=false;
        for(Item i : items){
            if(i.getItemId().equals(itemId)){
                Integer index = items.indexOf(i);
                item.setItemId(itemId);
                items.set(index, item);
                isPresent=true;
                break;
            }
        }
        if (!isPresent) throw new ResourceNotFound("Item Not Found!");
        cartdb.setItems(items);
        return repo.save(cartdb);
    }

    public Cart deleteItemById(Map<String,String> headers,String itemId){
        User user = userServiceRequest.getUserByEmail(EmailRequest.builder().email(headers.get("x-auth-user-email")).build(), headers).orElseThrow(()-> new ResourceNotFound("User Not Found!"));
        Long userId = user.getUserId();
        Cart cartdb = repo.findByUserId(userId).orElseThrow(()->new ResourceNotFound("Cart Not Found!"));
        if(userId!=cartdb.getUserId()) throw new LoginError("Unauthorised User!");
        Item item=null;
        List<Item> items=cartdb.getItems();
        Boolean isPresent=false;
        for(Item i : items){
            if(i.getItemId().equals(itemId)){
                item=i;
                items.remove(item);
                isPresent=true;
                break;
            }
        }
        if (!isPresent) throw new ResourceNotFound("Item Not Found!");
        cartdb.setItems(items);
        return repo.save(cartdb);
    }


    public CartResponse getCart(Map<String,String> headers){
        User user = userServiceRequest.getUserByEmail(EmailRequest.builder().email(headers.get("x-auth-user-email")).build(), headers).orElseThrow(()-> new ResourceNotFound("User Not Found!"));
        Long userId = user.getUserId();
        Cart cartdb = repo.findByUserId(userId).orElse(null);
        CartResponse cartResponse = new CartResponse();
        cartResponse.setUserId(userId);
        if(cartdb==null) return cartResponse;
        if(!user.getUserId().equals(cartdb.getUserId())) throw new LoginError("Unauthorised User!");
        cartResponse.setCartId(cartdb.getCartId());
        Double totalPrice=0.0;
        List<ItemResponse> itemsResponse = new ArrayList<ItemResponse>();
        if(cartdb.getItems()!=null && cartdb.getItems().size()!=0){
            for(Item item : cartdb.getItems()){
                Long productId = item.getProductId();
                Long variantId = item.getVariantId();
                ProductResponse productResponse = productServiceRequest.getProductById(productId).orElse(null);
                if(productResponse!=null){
                    ItemResponse itemResponse = new ItemResponse();
                    itemResponse.setName(productResponse.getProduct().getName());
                    itemResponse.setDescription(productResponse.getProduct().getDescription());
                    itemResponse.setPrice(productResponse.getProduct().getPrice());
                    itemResponse.setProductId(productResponse.getProduct().getProductId());
                    itemResponse.setSkucode(productResponse.getProduct().getSkucode());
                    itemResponse.setSellerId(productResponse.getProduct().getSellerId());
                    if(item.getVariantId()!=null){
                        com.adminseeker.cartservice.entities.Variant variantEntity = new com.adminseeker.cartservice.entities.Variant();
                        if(productResponse.getProduct().getVariants()!=null && productResponse.getProduct().getVariants().size()!=0)
                        for(Variant v: productResponse.getProduct().getVariants()){
                            if(v.getVariantId().equals(variantId)){
                                variantEntity.setColor(v.getColor());
                                variantEntity.setPrice(v.getPrice());
                                variantEntity.setSize(v.getSize());
                                variantEntity.setType(v.getType());
                                variantEntity.setVariantId(v.getVariantId());
                                variantEntity.setVariantSkucode(v.getVariantSkucode());
                                itemResponse.setPrice(v.getPrice());
                                break;
                            }
                        }
                        itemResponse.setVariant(variantEntity);
                        itemResponse.setQuantity(item.getQuantity());
                        itemResponse.setItemId(item.getItemId());
                        totalPrice+=(itemResponse.getVariant().getPrice()*item.getQuantity());
                    }else{
                        totalPrice+=(itemResponse.getPrice()*item.getQuantity());
                        itemResponse.setQuantity(item.getQuantity());
                        itemResponse.setItemId(item.getItemId());
                    }
                    itemsResponse.add(itemResponse);
                }
            }
            cartResponse.setItemsResponse(itemsResponse);
        }
        cartResponse.setTotalPrice(totalPrice);
        return cartResponse;
    }

    public Cart clearCart(Map<String,String> headers){
        User user = userServiceRequest.getUserByEmail(EmailRequest.builder().email(headers.get("x-auth-user-email")).build(), headers).orElseThrow(()-> new ResourceNotFound("User Not Found!"));
        Long userId = user.getUserId();
        Cart cartdb = repo.findByUserId(userId).orElseThrow(()->new ResourceNotFound("Cart Not Found"));
        cartdb.getItems().clear();
        repo.save(cartdb);
        return cartdb;
    }
}
