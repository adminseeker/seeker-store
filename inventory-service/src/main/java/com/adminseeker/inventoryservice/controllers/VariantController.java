package com.adminseeker.inventoryservice.controllers;


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

import com.adminseeker.inventoryservice.entities.Variant;
import com.adminseeker.inventoryservice.entities.VariantRequest;
import com.adminseeker.inventoryservice.entities.ErrorResponse;
import com.adminseeker.inventoryservice.entities.QuantityResponse;
import com.adminseeker.inventoryservice.entities.VariantQuantity;
import com.adminseeker.inventoryservice.entities.VariantQuantityRequest;
import com.adminseeker.inventoryservice.services.VariantService;


@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/api/v1/inventory")
public class VariantController {
    
    @Autowired
    VariantService variantService;

    @PostMapping("/{inventoryId}/variant")
    public ResponseEntity<?> save(@RequestHeader Map<String,String> headers,@RequestBody VariantRequest variantrequest, @PathVariable Long inventoryId){
        try {
            return new ResponseEntity<Variant>(variantService.addVariant(headers,inventoryId, variantrequest),HttpStatus.CREATED);
            
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/public/{inventoryId}/variant")
    public ResponseEntity<?> getAll(@RequestHeader Map<String,String> headers,@PathVariable Long inventoryId){
        try {
            return new ResponseEntity<List<Variant>>(variantService.getVariants(inventoryId),HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/public/{inventoryId}/variant/{variantId}")
    public ResponseEntity<?> getById(@RequestHeader Map<String,String> headers,@PathVariable Long inventoryId, @PathVariable Long variantId){
        try {
            Variant variant = variantService.getVariantById(inventoryId, variantId); 
            return new ResponseEntity<Variant>(variant,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/public/skucode/{skucode}/variant/{variantSkucode}/quantity")
    public ResponseEntity<?> getProductVariantQuantityBySkuCode(@RequestHeader Map<String,String> headers,@PathVariable String skucode, @PathVariable String variantSkucode){
        try {
            QuantityResponse quantityResponse = variantService.getProductVariantQuantityBySkucode(skucode,variantSkucode); 
            return new ResponseEntity<QuantityResponse>(quantityResponse,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/public/variant/skucode/getquantity")
    public ResponseEntity<?> getProductVariantQuantityBySkuCodes(@RequestHeader Map<String,String> headers,@RequestBody VariantQuantityRequest quantityUpdateRequest){
        try {
            List<VariantQuantity> updates = variantService.getVariantQuantityBySkucodes(quantityUpdateRequest);
            return new ResponseEntity<List<VariantQuantity>>(updates,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{inventoryId}/variant/{variantId}")
    public ResponseEntity<?> updateById(@RequestHeader Map<String,String> headers,@RequestBody VariantRequest variantrequest, @PathVariable Long inventoryId, @PathVariable Long variantId){
        try{
            Variant variantResult = variantService.UpdateVariantById(headers,inventoryId, variantrequest, variantId);
            return new ResponseEntity<Variant>(variantResult,HttpStatus.OK);
        }
        catch(Exception e){
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.BAD_REQUEST);

        }
    }

    @PutMapping("/inapi/variant/skucode/quantity")
    public ResponseEntity<?> updateProductVariantQuantityBySkuCodes(@RequestHeader Map<String,String> headers,@RequestBody VariantQuantityRequest quantityUpdateRequest){
        try {
            List<VariantQuantity> updates = variantService.UpdateVariantQuantityBySkucodes(quantityUpdateRequest);
            return new ResponseEntity<List<VariantQuantity>>(updates,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{inventoryId}/variant/{variantId}")
    public ResponseEntity<?> DeleteById(@RequestHeader Map<String,String> headers,@PathVariable Long inventoryId, @PathVariable Long variantId){
        try {
            Variant variant = variantService.deleteVariantById(headers,inventoryId, variantId);
            return new ResponseEntity<Variant>(variant,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }
}
