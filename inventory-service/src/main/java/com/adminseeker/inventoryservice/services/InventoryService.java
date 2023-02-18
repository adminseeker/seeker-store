package com.adminseeker.inventoryservice.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adminseeker.inventoryservice.entities.Inventory;
import com.adminseeker.inventoryservice.entities.InventoryRequest;
import com.adminseeker.inventoryservice.entities.InventoryResponse;
import com.adminseeker.inventoryservice.entities.QuantityResponse;
import com.adminseeker.inventoryservice.entities.Variant;
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
        List<Variant> variants = inventory.getVariants();
        String previousVal=null;
        Integer variantsCount = 1;
        if(variants!=null){
            for(Variant v: variants){
                v.setVariantId(variantsCount);
                v.setVariantSkucode(v.getVariantSkucode().toUpperCase());
                if(v.getVariantSkucode().equals(previousVal)){
                    throw new DuplicateResourceException("Duplicate Variant Skucode Entry!");
                }
                previousVal=v.getVariantSkucode();
                variantsCount++;
            }
        }
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

    public QuantityResponse getProductVariantQuantityBySkucode(String skucode, String variantSkucode){
        Inventory inventory = repo.findBySkucode(skucode.toUpperCase()).orElseThrow(()-> new ResourceNotFound("Inventory Not Found!"));
        productServiceRequest.getProductBySkucode(inventory.getSkucode()).orElseThrow(()-> new ResourceNotFound("Product Not Found!"));
        QuantityResponse quantityResponse = new QuantityResponse();
        if(inventory.getVariants().size()!=0){
            List<Variant> variants = inventory.getVariants();
            for(Variant v: variants){
                if(v.getVariantSkucode().equals(variantSkucode)){
                    quantityResponse.setQuantity(v.getQuantity());
                }
            } 
        }      
        if(quantityResponse.getQuantity()==null) throw new ResourceNotFound("Variant Not Found!");
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
        if(inventory.getSkucode()==null && inventory.getQuantity()==null && inventory.getVariants()==null) throw new Exception("Nothing to update!");
        
        if(inventory.getSkucode()!=null){
            inventorydb.setSkucode(inventory.getSkucode());
        }

        if(inventory.getQuantity()!=null){
            inventorydb.setQuantity(inventory.getQuantity());
        }
        if(productResponse.getProduct().getVariants()==null && inventory.getVariants().size()!=0) throw new ResourceNotFound("Product Has No Variants!");

        if(inventory.getVariants()!=null){
            List<Variant> variants = inventory.getVariants();
            String previousVal=null;
            Integer variantsCount = 1;

            if(variants!=null){
                for(Variant v: variants){
                    v.setVariantId(variantsCount);
                    v.setVariantSkucode(v.getVariantSkucode().toUpperCase());
                    if(v.getVariantSkucode().equals(previousVal)){
                        throw new DuplicateResourceException("Duplicate Variant Skucode Entry!");
                    }
                    previousVal=v.getVariantSkucode();
                    variantsCount++;
                }
    
            }
            inventorydb.getVariants().clear();
            inventorydb.getVariants().addAll(inventory.getVariants());
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
