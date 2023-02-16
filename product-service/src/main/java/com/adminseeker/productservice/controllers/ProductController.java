package com.adminseeker.productservice.controllers;


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

import com.adminseeker.productservice.entities.ErrorResponse;
import com.adminseeker.productservice.entities.Product;
import com.adminseeker.productservice.entities.ProductRequest;
import com.adminseeker.productservice.entities.ProductResponse;
import com.adminseeker.productservice.services.ProductService;


@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    
    @Autowired
    ProductService productService;

    @PostMapping("")
    public ResponseEntity<?> save(@RequestBody Product product){
        try {
        return new ResponseEntity<Product>(productService.addProduct(product),HttpStatus.CREATED);
            
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("")
    public ResponseEntity<?> getAll(){
        return new ResponseEntity<List<Product>>(productService.getProducts(),HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id){
        try {
            ProductResponse productResponse = productService.getProductById(id); 
            return new ResponseEntity<ProductResponse>(productResponse,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/skucode/{skucode}")
    public ResponseEntity<?> getProductBySkuCode(@PathVariable String skucode){
        try {
            ProductResponse productResponse = productService.getProductBySkucode(skucode); 
            return new ResponseEntity<ProductResponse>(productResponse,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateById(@RequestBody Product product, @PathVariable Long id){
        try{
            Product productDb = productService.updateProductById(product, id);
            return new ResponseEntity<Product>(productDb,HttpStatus.OK);
        }
        catch(Exception e){
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.BAD_REQUEST);

        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> DeleteById(@PathVariable Long id, @RequestBody ProductRequest productRequest){
        try {
            Product product = productService.DeleteProductById(id,productRequest); 
            return new ResponseEntity<Product>(product,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }
}
