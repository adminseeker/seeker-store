package com.adminseeker.orderservice.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adminseeker.orderservice.entities.Item;
import com.adminseeker.orderservice.entities.Order;
import com.adminseeker.orderservice.exceptions.ResourceNotFound;
import com.adminseeker.orderservice.exceptions.ResourceUpdateError;
import com.adminseeker.orderservice.proxies.Address;
import com.adminseeker.orderservice.proxies.CartResponse;
import com.adminseeker.orderservice.proxies.ItemResponse;
import com.adminseeker.orderservice.proxies.UserResponse;
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
            totalPrice+=itemResponse.getPrice();
        }
        order.setItems(orderItems);
        order.setTotalPrice(totalPrice);
        order.setStatus("order_placed");
        return repo.save(order);
    }

}
