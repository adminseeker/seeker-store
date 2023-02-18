package com.adminseeker.inventoryservice.controllers;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adminseeker.inventoryservice.entities.ErrorResponse;
import com.adminseeker.inventoryservice.entities.Inventory;
import com.adminseeker.inventoryservice.entities.InventoryRequest;
import com.adminseeker.inventoryservice.entities.InventoryResponse;
import com.adminseeker.inventoryservice.entities.QuantityResponse;
import com.adminseeker.inventoryservice.services.InventoryService;


@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {
    
    @Autowired
    InventoryService inventoryService;

    @PostMapping("")
    public ResponseEntity<?> save(@RequestBody Inventory inventory){
        try {
        return new ResponseEntity<Inventory>(inventoryService.addInventory(inventory),HttpStatus.CREATED);
            
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("")
    public ResponseEntity<?> getAll(){
        return new ResponseEntity<List<Inventory>>(inventoryService.getInventory(),HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getInventoryById(@PathVariable Long id){
        try {
            InventoryResponse inventoryResponse = inventoryService.getInventoryById(id); 
            return new ResponseEntity<InventoryResponse>(inventoryResponse,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/skucode/{skucode}")
    public ResponseEntity<?> getInventoryBySkuCode(@PathVariable String skucode){
        try {
            InventoryResponse inventoryResponse = inventoryService.getInventoryBySkucode(skucode); 
            return new ResponseEntity<InventoryResponse>(inventoryResponse,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/skucode/{skucode}/quantity")
    public ResponseEntity<?> getProductQuantityBySkuCode(@PathVariable String skucode){
        try {
            QuantityResponse quantityResponse = inventoryService.getProductQuantityBySkucode(skucode); 
            return new ResponseEntity<QuantityResponse>(quantityResponse,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/skucode/{skucode}/variant/{variantSkucode}/quantity")
    public ResponseEntity<?> getProductVariantQuantityBySkuCode(@PathVariable String skucode, @PathVariable String variantSkucode){
        try {
            QuantityResponse quantityResponse = inventoryService.getProductVariantQuantityBySkucode(skucode,variantSkucode); 
            return new ResponseEntity<QuantityResponse>(quantityResponse,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateById(@RequestBody Inventory inventory, @PathVariable Long id){
        try{
            Inventory inventoryDb = inventoryService.updateInventoryById(inventory, id);
            return new ResponseEntity<Inventory>(inventoryDb,HttpStatus.OK);
        }
        catch(Exception e){
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.BAD_REQUEST);

        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> DeleteById(@PathVariable Long id, @RequestBody InventoryRequest inventoryRequest){
        try {
            Inventory inventory = inventoryService.DeleteInventoryById(id,inventoryRequest); 
            return new ResponseEntity<Inventory>(inventory,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }
}
