package com.adminseeker.productservice.controllers;


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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
    public ResponseEntity<?> save(@RequestHeader Map<String,String> headers,@RequestBody ProductRequest productRequest ){
        try {
            Product newProduct = productService.addProduct(productRequest,headers);
            return new ResponseEntity<Product>(newProduct,HttpStatus.CREATED);
            
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/public")
    public ResponseEntity<?> getAllProducts(@RequestHeader Map<String,String> headers){
        List<Product> products = productService.getAllProducts(headers);
        return new ResponseEntity<List<Product>>(products,HttpStatus.OK);
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<?> getProductById(@RequestHeader Map<String,String> headers,@PathVariable Long id){
        try {
            ProductResponse productResponse = productService.getProductById(id,headers); 
            return new ResponseEntity<ProductResponse>(productResponse,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/public/seller/{sellerId}")
    public ResponseEntity<?> getProductBySeller(@RequestHeader Map<String,String> headers,@PathVariable Long sellerId){
        try {
            List<Product> products = productService.getProductsBySeller(sellerId,headers);
            return new ResponseEntity<List<Product>>(products,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/public/category/{categoryCode}")
    public ResponseEntity<?> getProductByCategory(@RequestHeader Map<String,String> headers,@PathVariable String categoryCode){
        try {
            List<Product> products = productService.getProductsByCategoryCode(categoryCode,headers);
            return new ResponseEntity<List<Product>>(products,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/public/skucode/{skucode}")
    public ResponseEntity<?> getProductBySkuCode(@RequestHeader Map<String,String> headers,@PathVariable String skucode){
        try {
            ProductResponse productResponse = productService.getProductBySkucode(skucode,headers);
            return new ResponseEntity<ProductResponse>(productResponse,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateById(@RequestHeader Map<String,String> headers,@RequestBody ProductRequest productRequest, @PathVariable Long id){
        try{
            Product productDb = productService.updateProductById(productRequest, id,headers);
            return new ResponseEntity<Product>(productDb,HttpStatus.OK);
        }
        catch(Exception e){
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.BAD_REQUEST);

        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> DeleteById(@RequestHeader Map<String,String> headers,@PathVariable Long id){
        try {
            Product product = productService.DeleteProductById(id,headers);
            return new ResponseEntity<Product>(product,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }
}
