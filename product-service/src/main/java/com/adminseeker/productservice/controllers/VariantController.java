package com.adminseeker.productservice.controllers;


import java.util.List;
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

import com.adminseeker.productservice.entities.Variant;
import com.adminseeker.productservice.entities.ErrorResponse;
import com.adminseeker.productservice.services.VariantService;


@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/api/v1/products")
public class VariantController {
    
    @Autowired
    VariantService variantService;

    @PostMapping("/{productId}/variant")
    public ResponseEntity<?> save(@RequestHeader Map<String,String> headers,@RequestBody Variant variantrequest, @PathVariable Long productId){
        try {
            headers.remove("content-length");
            Variant variant = variantService.addVariant(productId, variantrequest, headers);
            return new ResponseEntity<Variant>(variant,HttpStatus.CREATED);
            
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/public/{productId}/variant")
    public ResponseEntity<?> getAll(@RequestHeader Map<String,String> headers,@PathVariable Long productId){
        try {
            headers.remove("content-length");
            return new ResponseEntity<List<Variant>>(variantService.getVariants(productId, headers),HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/public/{productId}/variant/{variantId}")
    public ResponseEntity<?> getById(@RequestHeader Map<String,String> headers,@PathVariable Long productId, @PathVariable Long variantId){
        try {
            headers.remove("content-length");
            Variant variant = variantService.getVariantById(productId, variantId, headers); 
            return new ResponseEntity<Variant>(variant,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{productId}/variant/{variantId}")
    public ResponseEntity<?> updateById(@RequestHeader Map<String,String> headers,@RequestBody Variant variantrequest, @PathVariable Long productId, @PathVariable Long variantId){
        try{
            headers.remove("content-length");
            Variant variantResult = variantService.UpdateVariantById(productId, variantrequest, variantId, headers);
            return new ResponseEntity<Variant>(variantResult,HttpStatus.OK);
        }
        catch(Exception e){
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.BAD_REQUEST);

        }
    }

    @DeleteMapping("/{productId}/variant/{variantId}")
    public ResponseEntity<?> DeleteById(@RequestHeader Map<String,String> headers,@PathVariable Long productId, @PathVariable Long variantId){
        try {
            headers.remove("content-length");
            Variant variant = variantService.deleteVariantById(productId, variantId, headers);
            return new ResponseEntity<Variant>(variant,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }
}
