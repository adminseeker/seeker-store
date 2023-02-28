package com.adminseeker.orderservice.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adminseeker.orderservice.entities.Order;
import com.adminseeker.orderservice.exceptions.ResourceNotFound;
import com.adminseeker.orderservice.proxies.Address;
import com.adminseeker.orderservice.proxies.UserResponse;
import com.adminseeker.orderservice.repository.OrderRepo;

@Service
@Transactional
public class OrderService {
    
    @Autowired
    OrderRepo repo;

    @Autowired
    UserServiceRequest userServiceRequest;

    // public Order placeOrder(Order order){
    //     UserResponse user = userServiceRequest.getUserById(order.getUserId()).orElseThrow(()->new ResourceNotFound("User Not Found!"));
    //     List<Address> addressList = user.getAddressList();
    //     Boolean isAddressPresent=false;
    //     for(Address address : addressList){
    //         if(address.getAddressId().equals(order.getAddressId())){
    //             isAddressPresent=true;
    //             break;
    //         }
    //     }
        
    // }
}
