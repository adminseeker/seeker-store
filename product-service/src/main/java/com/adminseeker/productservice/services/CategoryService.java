package com.adminseeker.productservice.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adminseeker.productservice.entities.Category;
import com.adminseeker.productservice.exceptions.DuplicateResourceException;
import com.adminseeker.productservice.exceptions.ResourceNotFound;
import com.adminseeker.productservice.exceptions.ResourceUpdateError;
import com.adminseeker.productservice.repository.CategoryRepo;

import java.util.List;

import javax.transaction.Transactional;

@Service
@Transactional
public class CategoryService {
    
    @Autowired
    CategoryRepo repo;

    public Category addCategory(Category category){
        Category check = repo.findByCategoryCode(category.getCategoryCode()).orElse(null); 
        category.setCategoryCode(category.getCategoryCode().toUpperCase());
        if(check!=null) throw new DuplicateResourceException("Category Code Already Exists!");
        if(category.getSubCategories()!=null && !category.getSubCategories().isEmpty()){
            throw new ResourceUpdateError("Bad Request!");
        }
        return repo.save(category);
    }

    public List<Category> getAllCategories(){
        return repo.findAll();
    }

    public Category getCategoriesByCategoryCode(String categoryCode){
        Category category = repo.findByCategoryCode(categoryCode).orElseThrow(()-> new ResourceNotFound("Category Not Found!"));
        return category;
    }

    public Category getCategoriesById(Long categoryId){
        Category category = repo.findById(categoryId).orElseThrow(()-> new ResourceNotFound("Category Not Found!"));
        return category;
    }

    public Category updateCategoryById(Category category,Long categoryId) throws Exception{
        Category categorydb = repo.findById(categoryId).orElseThrow(()->new ResourceNotFound("Category Not Found!"));
        if(category.getCategoryCode()!=null){
            category.setCategoryCode(category.getCategoryCode().toUpperCase());
            Category check = repo.findByCategoryCode(category.getCategoryCode()).orElse(null);
            if(check!=null) throw new DuplicateResourceException("Category Code Already Exists!");
            categorydb.setCategoryCode(category.getCategoryCode());
        }
        if(category.getCategoryName()!=null){
            categorydb.setCategoryName(category.getCategoryName());
        }
        return repo.save(categorydb);
    }

    public Category DeleteCategoryById(Long categoryId){
        Category category = repo.findById(categoryId).orElseThrow(()-> new ResourceNotFound("Category Not Found!"));
        repo.delete(category);
        return category;        
    }

    public Category addSubCategory(Long parentId,Long subCategoryId){
        Category parentCategory = getCategoriesById(parentId);
        if(parentCategory==null) throw new ResourceNotFound("Parent Category Not Found!");    
        Category subCategory = getCategoriesById(subCategoryId); 
        if(subCategory==null) throw new ResourceNotFound("Category Not Found!");
        subCategory.setParent(parentCategory);
        List<Category> subCategories = parentCategory.getSubCategories();
        subCategories.add(subCategory);
        parentCategory.setSubCategories(subCategories);
        return repo.save(parentCategory);
    }

    public Category removeSubCategory(Long parentId,Long subCategoryId){
        Category parentCategory = getCategoriesById(parentId); 
        if(parentCategory==null) throw new ResourceNotFound("Parent Category Not Found!");
        Category subCategory = getCategoriesById(subCategoryId);
        if(subCategory==null) throw new ResourceNotFound("Sub Category Not Found!");
        List<Category> subCategories = parentCategory.getSubCategories();
        subCategories.forEach((sc)->{
            if(sc.getCategoryId().equals(subCategoryId)){
                sc.setParent(null);
                parentCategory.getSubCategories().remove(sc);
            }
        });
        parentCategory.setSubCategories(subCategories);
        return repo.save(parentCategory);
    }
}
