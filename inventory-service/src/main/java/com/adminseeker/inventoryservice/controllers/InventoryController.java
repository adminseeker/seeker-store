package com.adminseeker.inventoryservice.controllers;


import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adminseeker.inventoryservice.entities.ErrorResponse;
import com.adminseeker.inventoryservice.entities.Inventory;
import com.adminseeker.inventoryservice.entities.InventoryResponse;
import com.adminseeker.inventoryservice.entities.ProductQuantity;
import com.adminseeker.inventoryservice.entities.ProductQuantityRequest;
import com.adminseeker.inventoryservice.entities.QuantityResponse;
import com.adminseeker.inventoryservice.services.InventoryService;


@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {

    @Autowired
    InventoryService inventoryService;

    @PostMapping("")
    public ResponseEntity<?> save(@RequestHeader Map<String,String> headers,@RequestBody Inventory inventory){
        try {
            Inventory newInventory = inventoryService.addInventory(inventory,headers);
            return new ResponseEntity<Inventory>(newInventory,HttpStatus.CREATED);
            
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/inapi")
    public ResponseEntity<?> getAll(@RequestHeader Map<String,String> headers){
        return new ResponseEntity<List<Inventory>>(inventoryService.getInventory(headers),HttpStatus.OK);
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<?> getInventoryById(@RequestHeader Map<String,String> headers,@PathVariable Long id){
        try {
            InventoryResponse inventoryResponse = inventoryService.getInventoryById(id,headers);
            return new ResponseEntity<InventoryResponse>(inventoryResponse,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/public/skucode/{skucode}")
    public ResponseEntity<?> getInventoryBySkuCode(@RequestHeader Map<String,String> headers,@PathVariable String skucode){
        try {
            InventoryResponse inventoryResponse = inventoryService.getInventoryBySkucode(skucode,headers); 
            return new ResponseEntity<InventoryResponse>(inventoryResponse,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/public/skucode/{skucode}/quantity")
    public ResponseEntity<?> getProductQuantityBySkuCode(@RequestHeader Map<String,String> headers,@PathVariable String skucode){
        try {
            QuantityResponse quantityResponse = inventoryService.getProductQuantityBySkucode(skucode,headers); 
            return new ResponseEntity<QuantityResponse>(quantityResponse,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/public/skucode/getquantity")
    public ResponseEntity<?> getProductQuantityBySkuCodes(@RequestHeader Map<String,String> headers,@RequestBody ProductQuantityRequest productQuantityRequest){
        try {
            List<ProductQuantity> updates = inventoryService.getInventoryQuantityBySkuCodes(productQuantityRequest,headers); 
            return new ResponseEntity<List<ProductQuantity>>(updates,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }
    

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateById(@RequestHeader Map<String,String> headers,@RequestBody Inventory inventory, @PathVariable Long id){
        try{
            Inventory inventoryDb = inventoryService.updateInventoryById(inventory, id,headers);
            return new ResponseEntity<Inventory>(inventoryDb,HttpStatus.OK);
        }
        catch(Exception e){
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.BAD_REQUEST);

        }
    }

    @PutMapping("/inapi/skucode/quantity")
    public ResponseEntity<?> updateProductQuantityBySkuCodes(@RequestHeader Map<String,String> headers,@RequestBody ProductQuantityRequest productQuantityRequest){
        try {
            List<ProductQuantity> updates = inventoryService.updateInventoryQuantityBySkuCodes(productQuantityRequest,headers);
            return new ResponseEntity<List<ProductQuantity>>(updates,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> DeleteById(@RequestHeader Map<String,String> headers,@PathVariable Long id){
        try {
            Inventory inventory = inventoryService.DeleteInventoryById(id,headers); 
            return new ResponseEntity<Inventory>(inventory,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }
}
