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

import com.adminseeker.productservice.entities.Category;
import com.adminseeker.productservice.entities.ErrorResponse;
import com.adminseeker.productservice.entities.Product;
import com.adminseeker.productservice.entities.ProductResponse;
import com.adminseeker.productservice.services.CategoryService;
import com.adminseeker.productservice.services.ProductService;


@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/api/v1/products")
public class CategoryController {
    
    @Autowired
    CategoryService categoryService;

    @PostMapping("/inapi/categories")
    public ResponseEntity<?> saveCategory(@RequestHeader Map<String,String> headers,@RequestBody Category category ){
        try {
            Category newCategory= categoryService.addCategory(category);
            return new ResponseEntity<Category>(newCategory,HttpStatus.CREATED);
            
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/inapi/categories/{parentId}/subcategories")
    public ResponseEntity<?> saveSubCategory(@RequestHeader Map<String,String> headers,@RequestBody Category subCategory, @PathVariable Long parentId ){
        try {
            Category category= categoryService.addSubCategory(parentId,subCategory);
            return new ResponseEntity<Category>(category,HttpStatus.CREATED);
            
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/public/categories")
    public ResponseEntity<?> getAllCategories(@RequestHeader Map<String,String> headers){
        List<Category> categories = categoryService.getAllCategories();
        return new ResponseEntity<List<Category>>(categories,HttpStatus.OK);
    }

    @GetMapping("/public/categories/{id}")
    public ResponseEntity<?> getCategoryById(@RequestHeader Map<String,String> headers,@PathVariable Long id){
        try {
            Category category = categoryService.getCategoriesById(id); 
            return new ResponseEntity<Category>(category,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/public/categories/code/{categoryCode}")
    public ResponseEntity<?> getCategoryByCode(@RequestHeader Map<String,String> headers,@PathVariable String categoryCode){
        try {
            Category category = categoryService.getCategoriesByCategoryCode(categoryCode);
            return new ResponseEntity<Category>(category,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/inapi/categories/{id}")
    public ResponseEntity<?> updateById(@RequestHeader Map<String,String> headers,@RequestBody Category category, @PathVariable Long id){
        try{
            Category categoryDb = categoryService.updateCategoryById(category, id);
            return new ResponseEntity<Category>(categoryDb,HttpStatus.OK);
        }
        catch(Exception e){
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.BAD_REQUEST);

        }
    }

    @DeleteMapping("/inapi/categories/{id}")
    public ResponseEntity<?> DeleteById(@RequestHeader Map<String,String> headers,@PathVariable Long id){
        try {
            Category category = categoryService.DeleteCategoryById(id);
            return new ResponseEntity<Category>(category,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/inapi/categories/{parentId}/subcategories/{id}")
    public ResponseEntity<?> DeleteById(@RequestHeader Map<String,String> headers,@PathVariable Long parentId ,@PathVariable Long id){
        try {
            Category category = categoryService.removeSubCategory(parentId,id);
            return new ResponseEntity<Category>(category,HttpStatus.OK);
        } catch (Exception e) {
            ErrorResponse err = ErrorResponse.builder().msg(e.getMessage()).build();
            return new ResponseEntity<ErrorResponse>(err,HttpStatus.NOT_FOUND);
        }
    }
}
