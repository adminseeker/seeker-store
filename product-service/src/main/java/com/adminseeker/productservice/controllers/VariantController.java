package com.adminseeker.productservice.controllers;


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

import com.adminseeker.productservice.entities.Variant;
import com.adminseeker.productservice.entities.VariantRequest;
import com.adminseeker.productservice.entities.ErrorResponse;
import com.adminseeker.productservice.services.VariantService;


@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/api/v1/products")
public class VariantController {
    
    @Autowired
    VariantService variantService;

    @PostMapping("/{productId}/variant")
    public ResponseEntity<?> save(@RequestBody VariantRequest variantrequest, @PathVariable Long productId){
        try {
        return new ResponseEntity<Variant>(variantService.addVariant(productId, variantrequest),HttpStatus.CREATED);
            
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{productId}/variant")
    public ResponseEntity<?> getAll(@PathVariable Long productId){
        try {
            return new ResponseEntity<List<Variant>>(variantService.getVariants(productId),HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{productId}/variant/{variantId}")
    public ResponseEntity<?> getById(@PathVariable Long productId, @PathVariable Long variantId){
        try {
            Variant variant = variantService.getVariantById(productId, variantId); 
            return new ResponseEntity<Variant>(variant,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{productId}/variant/{variantId}")
    public ResponseEntity<?> updateById(@RequestBody VariantRequest variantrequest, @PathVariable Long productId, @PathVariable Long variantId){
        try{
            Variant variantResult = variantService.UpdateVariantById(productId, variantrequest, variantId);
            return new ResponseEntity<Variant>(variantResult,HttpStatus.OK);
        }
        catch(Exception e){
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.BAD_REQUEST);

        }
    }

    @DeleteMapping("/{productId}/variant/{variantId}")
    public ResponseEntity<?> DeleteById(@PathVariable Long productId, @PathVariable Long variantId, @RequestBody VariantRequest variantrequest){
        try {
            Variant variant = variantService.deleteVariantById(productId, variantId, variantrequest);
            return new ResponseEntity<Variant>(variant,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }
}
