package com.adminseeker.inventoryservice.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adminseeker.inventoryservice.entities.Inventory;

public interface InventoryRepo extends JpaRepository<Inventory,Long> {
    Optional<Inventory> findBySkucode(String skucode);
    Optional<Inventory> findBySellerId(Long sellerId);
}
