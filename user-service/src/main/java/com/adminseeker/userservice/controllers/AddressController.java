package com.adminseeker.userservice.controllers;


import java.util.List;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adminseeker.userservice.entities.Address;
import com.adminseeker.userservice.entities.ErrorResponse;
import com.adminseeker.userservice.services.AddressService;


@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/api/v1/users")
public class AddressController {
    
    @Autowired
    AddressService addressService;

    @PostMapping("/{userId}/address")
    public ResponseEntity<?> save(@RequestBody Address address, @PathVariable Long userId){
        try {
        return new ResponseEntity<Address>(addressService.addAddress(userId, address),HttpStatus.CREATED);
            
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{userId}/address")
    public ResponseEntity<?> getAll(@PathVariable Long userId){
        try {
            return new ResponseEntity<List<Address>>(addressService.getAddressList(userId),HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{userId}/address/{addressId}")
    public ResponseEntity<?> getById(@PathVariable Long userId, @PathVariable Long addressId){
        try {
            Address address = addressService.getAddressById(userId, addressId); 
            return new ResponseEntity<Address>(address,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{userId}/address/{addressId}")
    public ResponseEntity<?> updateById(@RequestBody Address address, @PathVariable Long userId, @PathVariable Long addressId){
        try{
            Address addressResult = addressService.updateAddressById(userId, address, addressId);
            return new ResponseEntity<Address>(addressResult,HttpStatus.OK);
        }
        catch(Exception e){
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.BAD_REQUEST);

        }
    }

    @DeleteMapping("/{userId}/address/{addressId}")
    public ResponseEntity<?> DeleteById(@PathVariable Long userId, @PathVariable Long addressId){
        try {
            Address address = addressService.deleteAddressById(userId, addressId);
            return new ResponseEntity<Address>(address,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }
}
