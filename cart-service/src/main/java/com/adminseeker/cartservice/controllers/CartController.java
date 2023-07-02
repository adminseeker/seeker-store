package com.adminseeker.cartservice.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adminseeker.cartservice.entities.Cart;
import com.adminseeker.cartservice.entities.CartRequest;
import com.adminseeker.cartservice.entities.CartResponse;
import com.adminseeker.cartservice.entities.ErrorResponse;
import com.adminseeker.cartservice.services.CartService;



@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/api/v1/cart")
public class CartController {
    
    @Autowired
    CartService cartService;

    @PostMapping("")
    public ResponseEntity<?> save(@RequestHeader Map<String,String> headers, @RequestBody CartRequest cartrequest){
        try {
            headers.remove("content-length");
            return new ResponseEntity<Cart>(cartService.addItems(headers,cartrequest),HttpStatus.CREATED);
            
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("")
    public ResponseEntity<?> getAll(@RequestHeader Map<String,String> headers){
        try {
            headers.remove("content-length");
            return new ResponseEntity<CartResponse>(cartService.getCart(headers),HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/item/{itemId}")
    public ResponseEntity<?> updateById(@RequestHeader Map<String,String> headers,@RequestBody CartRequest cartrequest, @PathVariable String itemId){
        try{
            headers.remove("content-length");
            Cart cartResult = cartService.updateItemById(headers,cartrequest,itemId);
            return new ResponseEntity<Cart>(cartResult,HttpStatus.OK);
        }
        catch(Exception e){
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.BAD_REQUEST);

        }
    }

    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<?> DeleteItemById(@RequestHeader Map<String,String> headers,@PathVariable String itemId){
        try {
            headers.remove("content-length");
            Cart cart = cartService.deleteItemById(headers,itemId);
            return new ResponseEntity<Cart>(cart,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("")
    public ResponseEntity<?> clearCart(@RequestHeader Map<String,String> headers){
        try {
            headers.remove("content-length");
            Cart cart = cartService.clearCart(headers);
            return new ResponseEntity<Cart>(cart,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }
}
