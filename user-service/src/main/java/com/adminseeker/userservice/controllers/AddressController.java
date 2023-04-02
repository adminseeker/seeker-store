package com.adminseeker.userservice.controllers;


import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.adminseeker.userservice.entities.Address;
import com.adminseeker.userservice.entities.ErrorResponse;
import com.adminseeker.userservice.services.AddressService;


@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/api/v1/users")
public class AddressController {
    
    private static Logger logger = LoggerFactory.getLogger(AddressController.class);

    @Autowired
    AddressService addressService;

    @PostMapping("/{userId}/address")
    public ResponseEntity<?> save(@RequestBody Address address, @PathVariable Long userId,@RequestHeader Map<String,String> headers){
        try {
            Address newAddress = addressService.addAddress(userId, address,headers);
            logger.debug("New Address Added!, User Address: "+address.toString()+" correlation-id: "+headers.get("correlation-id"));
            return new ResponseEntity<Address>(newAddress,HttpStatus.CREATED);
            
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            logger.debug("Error Adding Address User Email: "+address.toString()+" "+e.getMessage()+" correlation-id: "+headers.get("correlation-id"));
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{userId}/address")
    public ResponseEntity<?> getAll(@PathVariable Long userId,@RequestHeader Map<String,String> headers){
        try {
            List<Address> addressList = addressService.getAddressList(userId,headers);
            logger.debug("Address List Requested!, "+addressList.toString() +" correlation-id: "+headers.get("correlation-id"));
            return new ResponseEntity<List<Address>>(addressList,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            logger.debug("Error Requesting Address List "+e.getMessage()+" correlation-id: "+headers.get("correlation-id"));
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{userId}/address/{addressId}")
    public ResponseEntity<?> getById(@PathVariable Long userId, @PathVariable Long addressId,@RequestHeader Map<String,String> headers){
        try {
            Address address = addressService.getAddressById(userId, addressId,headers);
            logger.debug("Address Requested!, "+address.toString() +" correlation-id: "+headers.get("correlation-id"));
            return new ResponseEntity<Address>(address,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            logger.debug("Error Requesting Address "+e.getMessage()+" correlation-id: "+headers.get("correlation-id"));
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{userId}/address/{addressId}")
    public ResponseEntity<?> updateById(@RequestBody Address address, @PathVariable Long userId, @PathVariable Long addressId,@RequestHeader Map<String,String> headers){
        try{
            Address addressResult = addressService.updateAddressById(userId, address, addressId,headers);
            logger.debug("Address Update Requested!, "+address.toString() +" correlation-id: "+headers.get("correlation-id"));
            return new ResponseEntity<Address>(addressResult,HttpStatus.OK);
        }
        catch(Exception e){
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            logger.debug("Error Update Address "+e.getMessage()+" correlation-id: "+headers.get("correlation-id"));
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.BAD_REQUEST);

        }
    }

    @DeleteMapping("/{userId}/address/{addressId}")
    public ResponseEntity<?> DeleteById(@PathVariable Long userId, @PathVariable Long addressId,@RequestHeader Map<String,String> headers){
        try {
            Address address = addressService.deleteAddressById(userId, addressId,headers);
            logger.debug("Address Delete Requested!, "+address.toString() +" correlation-id: "+headers.get("correlation-id"));
            return new ResponseEntity<Address>(address,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            logger.debug("Error Delete Address "+e.getMessage()+" correlation-id: "+headers.get("correlation-id"));
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }
}
