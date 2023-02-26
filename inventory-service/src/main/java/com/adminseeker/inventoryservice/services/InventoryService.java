package com.adminseeker.inventoryservice.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adminseeker.inventoryservice.entities.Inventory;
import com.adminseeker.inventoryservice.entities.InventoryRequest;
import com.adminseeker.inventoryservice.entities.InventoryResponse;
import com.adminseeker.inventoryservice.entities.QuantityResponse;
import com.adminseeker.inventoryservice.exceptions.DuplicateResourceException;
import com.adminseeker.inventoryservice.exceptions.ResourceNotFound;
import com.adminseeker.inventoryservice.exceptions.ResourceUpdateError;
import com.adminseeker.inventoryservice.proxies.ProductResponse;
import com.adminseeker.inventoryservice.repository.InventoryRepo;

import java.util.List;

import javax.transaction.Transactional;

@Service
@Transactional
public class InventoryService {
    
    @Autowired
    InventoryRepo repo;

    @Autowired
    ProductServiceRequest productServiceRequest;

    public Inventory addInventory(Inventory inventory){
        inventory.setSkucode(inventory.getSkucode().toUpperCase());
        ProductResponse productresponse = productServiceRequest.getProductBySkucode(inventory.getSkucode()).orElseThrow(()-> new ResourceNotFound("Product Not Found!"));
        if(!inventory.getSellerId().equals(productresponse.getSeller().getUserId())){
            throw new ResourceNotFound("Unauthorised User!");
        }
        if(productresponse.getProduct().getVariants()==null && inventory.getVariants().size()!=0) throw new ResourceNotFound("Product Has No Variants!");
        Inventory check = repo.findBySkucode(inventory.getSkucode()).orElse(null); 
        if(check!=null) throw new DuplicateResourceException("Product Skucode Already Exists!");
        
        return repo.save(inventory);
    }

    public List<Inventory> getInventory(){
        return repo.findAll();
    }

    public InventoryResponse getInventoryById(Long id){
        Inventory inventory = repo.findById(id).orElseThrow(()-> new ResourceNotFound("Inventory Not Found!"));
        ProductResponse productResponse = productServiceRequest.getProductBySkucode(inventory.getSkucode()).orElseThrow(()-> new ResourceNotFound("Product Not Found!"));
        InventoryResponse inventoryResponse = new InventoryResponse();
        inventoryResponse.setInventory(inventory);
        inventoryResponse.setProductResponse(productResponse);
        return inventoryResponse;
    }

    public InventoryResponse getInventoryBySkucode(String skucode){
        Inventory inventory = repo.findBySkucode(skucode.toUpperCase()).orElseThrow(()-> new ResourceNotFound("Inventory Not Found!"));
        ProductResponse productResponse = productServiceRequest.getProductBySkucode(inventory.getSkucode()).orElseThrow(()-> new ResourceNotFound("Product Not Found!"));
        InventoryResponse inventoryResponse = new InventoryResponse();
        inventoryResponse.setInventory(inventory);
        inventoryResponse.setProductResponse(productResponse);
        return inventoryResponse;
    }

    public QuantityResponse getProductQuantityBySkucode(String skucode){
        Inventory inventory = repo.findBySkucode(skucode.toUpperCase()).orElseThrow(()-> new ResourceNotFound("Inventory Not Found!"));
        productServiceRequest.getProductBySkucode(inventory.getSkucode()).orElseThrow(()-> new ResourceNotFound("Product Not Found!"));
        QuantityResponse quantityResponse = new QuantityResponse();
        quantityResponse.setQuantity(inventory.getQuantity());
        return quantityResponse;
    }

    

    public Inventory updateInventoryById(Inventory inventory,Long inventoryId) throws Exception{
        Inventory inventorydb = repo.findById(inventoryId).orElseThrow(()->new ResourceNotFound("Inventory Not Found!"));
        ProductResponse productResponse = productServiceRequest.getProductBySkucode(inventorydb.getSkucode()).orElseThrow(()-> new ResourceNotFound("Product Not Found!"));
        if(!productResponse.getSeller().getUserId().equals(inventory.getSellerId())) throw new ResourceUpdateError("Unauthorised User!");
        if(inventory.getSkucode()!=null){
            inventory.setSkucode(inventory.getSkucode().toUpperCase());
            Inventory check = repo.findBySkucode(inventory.getSkucode()).orElse(null);
            if(check!=null) throw new DuplicateResourceException("Product Skucode Already Exists!");
        }
        if(inventory.getSkucode()==null && inventory.getQuantity()==null) throw new Exception("Nothing to update!");
        
        if(inventory.getSkucode()!=null){
            inventorydb.setSkucode(inventory.getSkucode());
        }

        if(inventory.getQuantity()!=null){
            inventorydb.setQuantity(inventory.getQuantity());
        }

        return repo.save(inventorydb);
    }

    public Inventory updateInventoryQuantityBySkuCode(QuantityResponse quantityResponse,String skucode) throws Exception{
        Inventory inventorydb = repo.findBySkucode(skucode).orElseThrow(()->new ResourceNotFound("Inventory Not Found!"));
        productServiceRequest.getProductBySkucode(inventorydb.getSkucode()).orElseThrow(()-> new ResourceNotFound("Product Not Found!"));

        if(quantityResponse.getQuantity()!=null){
            inventorydb.setQuantity(quantityResponse.getQuantity());
        }

        return repo.save(inventorydb);
    }

    public Inventory DeleteInventoryById(Long inventoryId,InventoryRequest inventoryRequest){

        Inventory inventory = repo.findById(inventoryId).orElseThrow(()-> new ResourceNotFound("Inventory Not Found!"));
        ProductResponse productResponse = productServiceRequest.getProductBySkucode(inventory.getSkucode()).orElseThrow(()-> new ResourceNotFound("Product Not Found!"));
        if(!productResponse.getSeller().getUserId().equals(inventoryRequest.getUserId())) throw new ResourceUpdateError("Unauthorised User!");
        repo.delete(inventory);
        return inventory;        
    }
}
