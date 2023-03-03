package com.adminseeker.orderservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adminseeker.orderservice.entities.ErrorResponse;
import com.adminseeker.orderservice.entities.Order;
import com.adminseeker.orderservice.entities.OrderRequest;
import com.adminseeker.orderservice.proxies.OrderResponse;
import com.adminseeker.orderservice.services.OrderService;
import java.util.List;


@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    
    @Autowired
    OrderService orderService;

    @PostMapping("")
    public ResponseEntity<?> placeOrder(@RequestBody Order order){
        try {
        return new ResponseEntity<Order>(orderService.placeOrder(order),HttpStatus.CREATED);
            
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/cancel")
    public ResponseEntity<?> cancelOrder(@RequestBody OrderRequest orderRequest){
        try {
        return new ResponseEntity<Order>(orderService.cancelOrder(orderRequest),HttpStatus.CREATED);
            
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/partialcancel")
    public ResponseEntity<?> partialCancelOrder(@RequestBody OrderRequest orderRequest){
        try {
        return new ResponseEntity<Order>(orderService.cancelPartialOrder(orderRequest),HttpStatus.CREATED);
            
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable String id){
        try {
            OrderResponse orderResponse = orderService.getOrder(id); 
            return new ResponseEntity<OrderResponse>(orderResponse,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/order")
    public ResponseEntity<?> getOrderByStatus(@RequestParam String status){
        try {
            List<Order> orders= orderService.getOrdersByStatus(status); 
            return new ResponseEntity<List<Order>>(orders,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }

   
}
