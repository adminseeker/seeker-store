package com.adminseeker.orderservice.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adminseeker.orderservice.entities.Item;
import com.adminseeker.orderservice.entities.Order;
import com.adminseeker.orderservice.exceptions.ResourceNotFound;
import com.adminseeker.orderservice.exceptions.ResourceUpdateError;
import com.adminseeker.orderservice.proxies.Address;
import com.adminseeker.orderservice.proxies.CartRequest;
import com.adminseeker.orderservice.proxies.CartResponse;
import com.adminseeker.orderservice.proxies.ItemResponse;
import com.adminseeker.orderservice.proxies.OrderItemResponse;
import com.adminseeker.orderservice.proxies.OrderResponse;
import com.adminseeker.orderservice.proxies.ProductResponse;
import com.adminseeker.orderservice.proxies.QuantityUpdate;
import com.adminseeker.orderservice.proxies.QuantityUpdateRequest;
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

    public Order placeOrder(Order order){
        UserResponse user = userServiceRequest.getUserById(order.getUserId()).orElseThrow(()->new ResourceNotFound("User Not Found!"));
        List<Address> addressList = user.getAddressList();
        Boolean isAddressPresent=false;
        for(Address address : addressList){
            if(address.getAddressId().equals(order.getAddressId())){
                isAddressPresent=true;
                break;
            }
        }
        if(!isAddressPresent) throw new ResourceNotFound("Address Not Found!");
        CartResponse cartResponse = cartServiceRequest.getUserCart(order.getUserId()).orElseThrow(()->new ResourceNotFound("Cart Not Found!"));
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
        List<QuantityUpdate> pResp = inventoryServiceRequest.updateProductQuantityBySkucodes(updateRequest).orElse(null);
        updateRequest.setQuantityUpdates(variants);
        List<QuantityUpdate> vResp = inventoryServiceRequest.updateVariantQuantityBySkucodes(updateRequest).orElse(null);

        if(pResp==null && vResp==null) throw new ResourceUpdateError("inventory update error!");

        order.setItems(orderItems);
        order.setTotalPrice(totalPrice);
        order.setStatus("order_placed");
        CartRequest cartRequest = new CartRequest();
        cartRequest.setUserId(order.getUserId());
        cartServiceRequest.clearUserCart(cartRequest).orElseThrow(()->new ResourceUpdateError("Cart Clearing Failed!"));
        return repo.save(order);
    }

    public OrderResponse getOrder(String orderId){
        Order order = repo.findById(orderId).orElseThrow(()-> new ResourceNotFound("Order Not Found!"));
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setOrderId(order.getOrderId());
        orderResponse.setStatus(order.getStatus());
        orderResponse.setTotalPrice(order.getTotalPrice());
        orderResponse.setUserId(order.getUserId());
        UserResponse userResponse = userServiceRequest.getUserById(order.getUserId()).orElseThrow(()-> new ResourceNotFound("User Not Found!"));
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
        return orderResponse;
    }

}
