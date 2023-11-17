package com.adminseeker.productservice.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adminseeker.productservice.entities.Product;

public interface ProductRepo extends JpaRepository<Product,Long> {
    Optional<Product> findBySkucode(String skucode);
    Optional<List<Product>> findBySellerId(Long sellerId);
    Optional<List<Product>> findByCategoryCodes(String categoryCode);
}
