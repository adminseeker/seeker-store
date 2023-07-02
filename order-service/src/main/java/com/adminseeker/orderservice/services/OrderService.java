package com.adminseeker.orderservice.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adminseeker.orderservice.entities.Item;
import com.adminseeker.orderservice.entities.Order;
import com.adminseeker.orderservice.entities.OrderRequest;
import com.adminseeker.orderservice.exceptions.LoginError;
import com.adminseeker.orderservice.exceptions.ResourceNotFound;
import com.adminseeker.orderservice.exceptions.ResourceUpdateError;
import com.adminseeker.orderservice.proxies.Address;
import com.adminseeker.orderservice.proxies.CartRequest;
import com.adminseeker.orderservice.proxies.CartResponse;
import com.adminseeker.orderservice.proxies.EmailRequest;
import com.adminseeker.orderservice.proxies.ItemResponse;
import com.adminseeker.orderservice.proxies.OrderItemResponse;
import com.adminseeker.orderservice.proxies.OrderResponse;
import com.adminseeker.orderservice.proxies.ProductResponse;
import com.adminseeker.orderservice.proxies.QuantityUpdate;
import com.adminseeker.orderservice.proxies.QuantityUpdateRequest;
import com.adminseeker.orderservice.proxies.User;
import com.adminseeker.orderservice.proxies.UserResponse;
import com.adminseeker.orderservice.proxies.Variant;
import com.adminseeker.orderservice.repository.OrderRepo;

@Service
@Transactional
public class OrderService {
    
    @Autowired
    OrderRepo repo;

    @Autowired
    UserServiceRequest userServiceRequest;

    @Autowired
    CartServiceRequest cartServiceRequest;

    @Autowired
    InventoryServiceRequest inventoryServiceRequest;

    @Autowired
    ProductServiceRequest productServiceRequest;

    public Order placeOrder(Map<String,String> headers,Order order){
        User user = userServiceRequest.getUserByEmail(EmailRequest.builder().email(headers.get("x-auth-user-email")).build(), headers).orElseThrow(()-> new ResourceNotFound("User Not Found!"));
        Long userId= user.getUserId();
        UserResponse userResponse = userServiceRequest.getUserById(headers,order.getUserId()).orElseThrow(()->new ResourceNotFound("User Not Found!"));
        if(!userId.equals(order.getUserId())) throw new LoginError("Unauthorised User!");
        List<Address> addressList = userResponse.getAddressList();
        Boolean isAddressPresent=false;
        for(Address address : addressList){
            if(address.getAddressId().equals(order.getAddressId())){
                isAddressPresent=true;
                break;
            }
        }
        if(!isAddressPresent) throw new ResourceNotFound("Address Not Found!");
        CartResponse cartResponse = cartServiceRequest.getUserCart(headers).orElseThrow(()->new ResourceNotFound("Cart Not Found!"));
        List<ItemResponse> cartItems = cartResponse.getItemsResponse();
        if(cartItems==null || cartItems.size()==0){
            throw new ResourceUpdateError("Cart is Empty!");
        }
        List<Item> orderItems=new ArrayList<Item>();
        Double totalPrice=0.0;
        for(ItemResponse itemResponse : cartItems){
            Item orderItem=new Item();
            orderItem.setItemId(itemResponse.getItemId());
            orderItem.setProductId(itemResponse.getProductId());
            if(itemResponse.getVariant()!=null) orderItem.setVariantId(itemResponse.getVariant().getVariantId());
            orderItem.setQuantity(itemResponse.getQuantity());
            orderItem.setPrice(itemResponse.getPrice());
            orderItem.setStatus("order_placed");
            orderItems.add(orderItem);
            totalPrice+=itemResponse.getPrice()*itemResponse.getQuantity();
        }

        List<QuantityUpdate> products = new ArrayList<QuantityUpdate>();
        List<QuantityUpdate> variants = new ArrayList<QuantityUpdate>();

        for(ItemResponse cartItem : cartItems){
            if(cartItem.getSkucode()==null){
                throw new ResourceUpdateError("Invalid Product!");
            }
            else if(cartItem.getSkucode()!=null && cartItem.getVariant()==null){
                QuantityUpdate stockUpdates = new QuantityUpdate();
                stockUpdates.setProductSkucode(cartItem.getSkucode());
                products.add(stockUpdates);
            }
            else if(cartItem.getSkucode()!=null && cartItem.getVariant()!=null) {
                QuantityUpdate stockUpdates = new QuantityUpdate();
                stockUpdates.setProductSkucode(cartItem.getSkucode());
                stockUpdates.setVariantSkucode(cartItem.getVariant().getVariantSkucode());
                variants.add(stockUpdates);
            }

        }

        QuantityUpdateRequest updateRequest = new QuantityUpdateRequest();
        updateRequest.setQuantityUpdates(products);
        products = inventoryServiceRequest.getProductQuantityBySkucodes(updateRequest).orElse(null);
        updateRequest.setQuantityUpdates(variants);
        variants = inventoryServiceRequest.getVariantQuantityBySkucodes(updateRequest).orElse(null);
        
        for(ItemResponse cartItem : cartItems){
            if(cartItem.getSkucode()!=null && cartItem.getVariant()==null){
                for(QuantityUpdate product : products){
                    if(cartItem.getSkucode().equals(product.getProductSkucode())){
                        if(cartItem.getQuantity()>product.getQuantity()){
                            throw new ResourceUpdateError("Out Of Stock "+cartItem.getSkucode());
                        }else{
                            product.setQuantity(product.getQuantity()-cartItem.getQuantity());
                        }
                    }
                }
            } else if(cartItem.getSkucode()!=null && cartItem.getVariant()!=null){
                for(QuantityUpdate variant : variants){
                    if(cartItem.getVariant().getVariantSkucode().equals(variant.getVariantSkucode())){
                        if(cartItem.getQuantity()>variant.getQuantity()){
                            throw new ResourceUpdateError("Out Of Stock "+cartItem.getSkucode());
                        }else{
                            variant.setQuantity(variant.getQuantity()-cartItem.getQuantity());
                        }
                    }
                }
            }
        }
        updateRequest.setQuantityUpdates(products);
        List<QuantityUpdate> pResp = inventoryServiceRequest.updateProductQuantityBySkucodes(headers,updateRequest).orElse(null);
        updateRequest.setQuantityUpdates(variants);
        List<QuantityUpdate> vResp = inventoryServiceRequest.updateVariantQuantityBySkucodes(headers,updateRequest).orElse(null);

        if(pResp==null && vResp==null) throw new ResourceUpdateError("inventory update error!");

        order.setItems(orderItems);
        order.setTotalPrice(totalPrice);
        order.setStatus("order_placed");
        CartRequest cartRequest = new CartRequest();
        cartRequest.setUserId(order.getUserId());
        cartServiceRequest.clearUserCart(headers).orElseThrow(()->new ResourceUpdateError("Cart Clearing Failed!"));
        return repo.save(order);
    }

    public List<OrderResponse> getOrders(Map<String,String> headers,String type, String value){
        User user = userServiceRequest.getUserByEmail(EmailRequest.builder().email(headers.get("x-auth-user-email")).build(), headers).orElseThrow(()-> new ResourceNotFound("User Not Found!"));
        List<OrderResponse> orderResponseList = new ArrayList<OrderResponse>();
        List<Order> orders = new ArrayList<Order>();
        if(type.equals("status") && StringUtils.isNotBlank(value)){
            orders=repo.findByUserId(user.getUserId()).orElse(new ArrayList<Order>());
            orders=orders.stream().filter((order)->order.getStatus().equals(value)).toList();
        }else if(type.equals("id") && StringUtils.isNotBlank(value)){
            orders=repo.findByUserId(user.getUserId()).orElse(new ArrayList<Order>());
            orders=orders.stream().filter((order)->order.getOrderId().equals(value)).toList();
        }else if(StringUtils.isBlank(type) && StringUtils.isBlank(value)){
            orders=repo.findByUserId(user.getUserId()).orElse(new ArrayList<Order>());
        }else{
            return orderResponseList;
        }
        if(orders.size()>0){
            for(Order order : orders){
                OrderResponse orderResponse = new OrderResponse();
                orderResponse.setOrderId(order.getOrderId());
                orderResponse.setStatus(order.getStatus());
                orderResponse.setTotalPrice(order.getTotalPrice());
                orderResponse.setUserId(order.getUserId());
                orderResponse.setCreatedDate(order.getCreatedDate());
                orderResponse.setModifiedDate(order.getModifiedDate());
                UserResponse userResponse = userServiceRequest.getUserById(headers,order.getUserId()).orElseThrow(()-> new ResourceNotFound("User Not Found!"));
                List<Address> addressList = userResponse.getAddressList();
                Address address = null;
                for(Address addr : addressList){
                    if(addr.getAddressId().equals(order.getAddressId())){
                        address=addr;
                        break;
                    }
                }
                orderResponse.setAddress(address);
                List<Item> orderItems = order.getItems();
                List<OrderItemResponse> orderItemResponses = new ArrayList<OrderItemResponse>();
                if(orderItems!=null){
                    for(Item orderItem : orderItems){
                        ProductResponse productResponse = productServiceRequest.getProductById(orderItem.getProductId()).orElseThrow(()-> new ResourceNotFound("Product Not Found!"));
                        OrderItemResponse orderItemResponse = new OrderItemResponse();
                        if(orderItem.getProductId()!=null && orderItem.getVariantId()==null){
                            orderItemResponse.setName(productResponse.getProduct().getName());
                            orderItemResponse.setDescription(productResponse.getProduct().getDescription());
                            orderItemResponse.setPrice(orderItem.getPrice());
                            orderItemResponse.setProductId(productResponse.getProduct().getProductId());
                            orderItemResponse.setSkucode(productResponse.getProduct().getSkucode());
                            orderItemResponse.setSellerId(productResponse.getProduct().getSellerId());
                            orderItemResponse.setQuantity(orderItem.getQuantity());
                            orderItemResponse.setStatus(orderItem.getStatus());
                            orderItemResponse.setItemId(orderItem.getItemId());
                        } else if(orderItem.getProductId()!=null && orderItem.getVariantId()!=null){
                            orderItemResponse.setName(productResponse.getProduct().getName());
                            orderItemResponse.setDescription(productResponse.getProduct().getDescription());
                            orderItemResponse.setPrice(orderItem.getPrice());
                            orderItemResponse.setProductId(productResponse.getProduct().getProductId());
                            orderItemResponse.setSkucode(productResponse.getProduct().getSkucode());
                            orderItemResponse.setSellerId(productResponse.getProduct().getSellerId());
                            orderItemResponse.setQuantity(orderItem.getQuantity());
                            orderItemResponse.setStatus(orderItem.getStatus());
                            orderItemResponse.setItemId(orderItem.getItemId());
                            Variant variant = null;
                            if(productResponse.getProduct().getVariants()==null) throw new ResourceNotFoundException("No Variants Found!");
                            for(Variant v : productResponse.getProduct().getVariants()){
                                if(v.getVariantId().equals(orderItem.getVariantId())){
                                    variant=v;
                                    break;
                                }
                            }
                            orderItemResponse.setVariant(variant);
                        }
                        orderItemResponses.add(orderItemResponse);   
                    }
                }
                orderResponse.setItems(orderItemResponses);
                orderResponseList.add(orderResponse);
            }
        }
        return orderResponseList;
    }

    public Order cancelOrder(Map<String,String> headers,OrderRequest orderRequest){
        User user = userServiceRequest.getUserByEmail(EmailRequest.builder().email(headers.get("x-auth-user-email")).build(), headers).orElseThrow(()-> new ResourceNotFound("User Not Found!"));
        String orderId = orderRequest.getOrderId();
        Long userId = user.getUserId();
        Order order = repo.findById(orderId).orElseThrow(()->new ResourceNotFound("Order Not Found!"));
        if(order.getUserId()!=userId) throw new LoginError("Unauthorised User!");
        if(order.getStatus().equals("order_cancelled")) throw new ResourceUpdateError("Order Is Already Cancelled!");
        List<Item> items = order.getItems();
        List<QuantityUpdate> products = new ArrayList<QuantityUpdate>();
        List<QuantityUpdate> variants = new ArrayList<QuantityUpdate>();
        List<ProductResponse> productResponses= new ArrayList<ProductResponse>();  
        for(Item orderItem : items){
            ProductResponse productResponse = productServiceRequest.getProductById(orderItem.getProductId()).orElse(null);
            productResponses.add(productResponse);
            if(orderItem.getProductId()!=null && orderItem.getVariantId()==null){
                QuantityUpdate product = new QuantityUpdate();
                product.setProductSkucode(productResponse.getProduct().getSkucode());
                products.add(product);
            }else if(orderItem.getProductId()!=null && orderItem.getVariantId()!=null){
                QuantityUpdate variant = new QuantityUpdate();
                if(productResponse.getProduct().getVariants()==null || productResponse.getProduct().getVariants().size()==0 ) throw new ResourceUpdateError("Variant Not Found!");
                Variant productVariant = new Variant();
                for(Variant v : productResponse.getProduct().getVariants()){
                    if(v.getVariantId().equals(orderItem.getVariantId())){
                        productVariant = v;
                        break;
                    }
                }
                variant.setProductSkucode(productResponse.getProduct().getSkucode());
                variant.setVariantSkucode(productVariant.getVariantSkucode());
                variant.setProductId(productResponse.getProduct().getProductId());
                variants.add(variant);
            }
        }

        QuantityUpdateRequest updateRequest = new QuantityUpdateRequest();
        updateRequest.setQuantityUpdates(products);
        products = inventoryServiceRequest.getProductQuantityBySkucodes(updateRequest).orElse(null);
        updateRequest.setQuantityUpdates(variants);
        variants = inventoryServiceRequest.getVariantQuantityBySkucodes(updateRequest).orElse(null);

        if((products==null && variants==null)) throw new ResourceUpdateError("Inventory Error!");

        for (ProductResponse pResp : productResponses){
            if(products!=null){
                for(QuantityUpdate product : products){
                    if(pResp.getProduct().getSkucode().equals(product.getProductSkucode())){
                        product.setProductId(pResp.getProduct().getProductId());
                    }
                }
            }
            if(variants!=null && pResp.getProduct().getVariants()!=null){
                for(Variant v : pResp.getProduct().getVariants()){
                    for(QuantityUpdate variant : variants){
                        if(variant.getVariantSkucode().equals(v.getVariantSkucode())){
                            variant.setProductId(pResp.getProduct().getProductId());
                            variant.setVariantId(v.getVariantId());
                        }
                    }
                }
            } 
            
        }


        for(Item orderItem: items){
            if(products!=null && orderItem.getProductId()!=null && orderItem.getVariantId()==null){
                for(QuantityUpdate product : products){
                    if(product.getProductId().equals(orderItem.getProductId())){
                        product.setQuantity(product.getQuantity()+orderItem.getQuantity());
                    }
                }
            }if(variants!=null && orderItem.getProductId()!=null && orderItem.getVariantId()!=null){
                for(QuantityUpdate variant : variants){
                    if(variant.getVariantId().equals(orderItem.getVariantId())){
                        variant.setQuantity(variant.getQuantity()+orderItem.getQuantity());
                    }
                }
            }
            orderItem.setStatus("order_cancelled");
        }

        updateRequest.setQuantityUpdates(products);
        products = inventoryServiceRequest.updateProductQuantityBySkucodes(headers,updateRequest).orElse(null);
        updateRequest.setQuantityUpdates(variants);
        variants = inventoryServiceRequest.updateVariantQuantityBySkucodes(headers,updateRequest).orElse(null);

        if((products==null && variants==null)) throw new ResourceUpdateError("Inventory Error!");

        order.setStatus("order_cancelled");
        order.setItems(items);
        return repo.save(order);
    }

    public Order cancelPartialOrder(Map<String,String> headers,OrderRequest orderRequest){
        User user = userServiceRequest.getUserByEmail(EmailRequest.builder().email(headers.get("x-auth-user-email")).build(), headers).orElseThrow(()-> new ResourceNotFound("User Not Found!"));
        String orderId = orderRequest.getOrderId();
        String itemId = orderRequest.getItemId();
        Long userId = user.getUserId();
        Order order = repo.findById(orderId).orElseThrow(()->new ResourceNotFound("Order Not Found!"));
        if(order.getUserId()!=userId) throw new LoginError("Unauthorised User!");
        if(order.getStatus().equals("order_cancelled")) throw new ResourceUpdateError("Order Is Already Cancelled!");

        List<Item> items = order.getItems();
        Item cancelItem = null;
        for(Item item : items){
            if(item.getItemId().equals(itemId)){
                cancelItem=item;
                if(item.getStatus().equals("order_cancelled")) throw new ResourceUpdateError("Order Item Already Cancelled!");
                item.setStatus("order_cancelled");
                break;
            }
        }
        if(cancelItem==null) throw new ResourceUpdateError("Item Not Found!");
        ProductResponse productResponse = productServiceRequest.getProductById(cancelItem.getProductId()).orElseThrow(()->new ResourceNotFound("Product Not Found!"));
        QuantityUpdateRequest quantityUpdateRequest = new QuantityUpdateRequest();
        List<QuantityUpdate> products = new ArrayList<QuantityUpdate>();
        List<QuantityUpdate> variants = new ArrayList<QuantityUpdate>();
        if(cancelItem.getVariantId()==null){
            QuantityUpdate product = new QuantityUpdate();
            product.setProductSkucode(productResponse.getProduct().getSkucode());
            products.add(product);
        }else {
            QuantityUpdate variant = new QuantityUpdate();
            for(Variant v : productResponse.getProduct().getVariants()){
                if(productResponse.getProduct().getProductId().equals(cancelItem.getProductId()) && v.getVariantId().equals(cancelItem.getVariantId())){
                    variant.setProductSkucode(productResponse.getProduct().getSkucode());
                    variant.setVariantSkucode(v.getVariantSkucode());
                    break;
                }
            }
            variants.add(variant);
        }
        if(cancelItem.getVariantId()==null){
            quantityUpdateRequest.setQuantityUpdates(products);
            products=inventoryServiceRequest.getProductQuantityBySkucodes(quantityUpdateRequest).orElseThrow(()->new ResourceUpdateError("Inventory Error!"));
            products.get(0).setQuantity(products.get(0).getQuantity()+cancelItem.getQuantity());
            quantityUpdateRequest.setQuantityUpdates(products);
            products=inventoryServiceRequest.updateProductQuantityBySkucodes(headers,quantityUpdateRequest).orElse(null);
        }else{
            quantityUpdateRequest.setQuantityUpdates(variants);
            variants=inventoryServiceRequest.getVariantQuantityBySkucodes(quantityUpdateRequest).orElseThrow(()->new ResourceUpdateError("Inventory Error!"));
            variants.get(0).setQuantity(variants.get(0).getQuantity()+cancelItem.getQuantity());
            quantityUpdateRequest.setQuantityUpdates(variants);
            variants=inventoryServiceRequest.updateVariantQuantityBySkucodes(headers,quantityUpdateRequest).orElse(null);
        }

        if(products==null && variants==null) throw new  ResourceUpdateError("Inventory Error!");
        order.setTotalPrice(order.getTotalPrice()-cancelItem.getPrice());
        order.setItems(items);
        order.setStatus("order_partial_cancelled");
        return repo.save(order);
    }
}

