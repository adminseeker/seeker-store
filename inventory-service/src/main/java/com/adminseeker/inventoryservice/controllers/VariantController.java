package com.adminseeker.inventoryservice.controllers;


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

import com.adminseeker.inventoryservice.entities.Variant;
import com.adminseeker.inventoryservice.entities.VariantRequest;
import com.adminseeker.inventoryservice.entities.ErrorResponse;
import com.adminseeker.inventoryservice.entities.QuantityResponse;
import com.adminseeker.inventoryservice.entities.QuantityUpdate;
import com.adminseeker.inventoryservice.entities.QuantityUpdateRequest;
import com.adminseeker.inventoryservice.services.VariantService;


@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/api/v1/inventory")
public class VariantController {
    
    @Autowired
    VariantService variantService;

    @PostMapping("/{inventoryId}/variant")
    public ResponseEntity<?> save(@RequestBody VariantRequest variantrequest, @PathVariable Long inventoryId){
        try {
        return new ResponseEntity<Variant>(variantService.addVariant(inventoryId, variantrequest),HttpStatus.CREATED);
            
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{inventoryId}/variant")
    public ResponseEntity<?> getAll(@PathVariable Long inventoryId){
        try {
            return new ResponseEntity<List<Variant>>(variantService.getVariants(inventoryId),HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{inventoryId}/variant/{variantId}")
    public ResponseEntity<?> getById(@PathVariable Long inventoryId, @PathVariable Long variantId){
        try {
            Variant variant = variantService.getVariantById(inventoryId, variantId); 
            return new ResponseEntity<Variant>(variant,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/skucode/{skucode}/variant/{variantSkucode}/quantity")
    public ResponseEntity<?> getProductVariantQuantityBySkuCode(@PathVariable String skucode, @PathVariable String variantSkucode){
        try {
            QuantityResponse quantityResponse = variantService.getProductVariantQuantityBySkucode(skucode,variantSkucode); 
            return new ResponseEntity<QuantityResponse>(quantityResponse,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/variant/skucode/getquantity")
    public ResponseEntity<?> getProductVariantQuantityBySkuCodes(@RequestBody QuantityUpdateRequest quantityUpdateRequest){
        try {
            List<QuantityUpdate> updates = variantService.getVariantQuantityBySkucodes(quantityUpdateRequest);
            return new ResponseEntity<List<QuantityUpdate>>(updates,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{inventoryId}/variant/{variantId}")
    public ResponseEntity<?> updateById(@RequestBody VariantRequest variantrequest, @PathVariable Long inventoryId, @PathVariable Long variantId){
        try{
            Variant variantResult = variantService.UpdateVariantById(inventoryId, variantrequest, variantId);
            return new ResponseEntity<Variant>(variantResult,HttpStatus.OK);
        }
        catch(Exception e){
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.BAD_REQUEST);

        }
    }

    @PutMapping("/variant/skucode/quantity")
    public ResponseEntity<?> updateProductVariantQuantityBySkuCodes(@RequestBody QuantityUpdateRequest quantityUpdateRequest){
        try {
            List<QuantityUpdate> updates = variantService.UpdateVariantQuantityBySkucodes(quantityUpdateRequest);
            return new ResponseEntity<List<QuantityUpdate>>(updates,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{inventoryId}/variant/{variantId}")
    public ResponseEntity<?> DeleteById(@PathVariable Long inventoryId, @PathVariable Long variantId, @RequestBody VariantRequest variantrequest){
        try {
            Variant variant = variantService.deleteVariantById(inventoryId, variantId, variantrequest);
            return new ResponseEntity<Variant>(variant,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }
}
