package com.adminseeker.orderservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adminseeker.orderservice.entities.ErrorResponse;
import com.adminseeker.orderservice.entities.Order;
import com.adminseeker.orderservice.entities.OrderRequest;
import com.adminseeker.orderservice.proxies.OrderResponse;
import com.adminseeker.orderservice.services.OrderService;
import java.util.List;
import java.util.Map;


@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    
    @Autowired
    OrderService orderService;

    @PostMapping("")
    public ResponseEntity<?> placeOrder(@RequestHeader Map<String,String> headers,@RequestBody Order order){
        try {
            headers.remove("content-length");
            return new ResponseEntity<Order>(orderService.placeOrder(headers,order),HttpStatus.CREATED);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/cancel")
    public ResponseEntity<?> cancelOrder(@RequestHeader Map<String,String> headers,@RequestBody OrderRequest orderRequest){
        try {
            headers.remove("content-length");
            return new ResponseEntity<Order>(orderService.cancelOrder(headers,orderRequest),HttpStatus.CREATED);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/partialcancel")
    public ResponseEntity<?> partialCancelOrder(@RequestHeader Map<String,String> headers,@RequestBody OrderRequest orderRequest){
        try {
            headers.remove("content-length");
            return new ResponseEntity<Order>(orderService.cancelPartialOrder(headers,orderRequest),HttpStatus.CREATED);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("")
    public ResponseEntity<?> getOrders(@RequestHeader Map<String,String> headers,@RequestParam(required = false,defaultValue = "") String type,@RequestParam(required = false,defaultValue = "") String value){
        try {
            headers.remove("content-length");
            List<OrderResponse> orderResponse = orderService.getOrders(headers,type,value); 
            return new ResponseEntity<List<OrderResponse>>(orderResponse,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }
}
