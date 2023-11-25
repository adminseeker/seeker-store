package com.adminseeker.productservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adminseeker.productservice.entities.Category;


public interface CategoryRepo extends JpaRepository<Category,Long> {
    Optional<Category> findByCategoryCode(String categoryCode);
}
