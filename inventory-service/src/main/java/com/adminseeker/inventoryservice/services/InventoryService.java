package com.adminseeker.inventoryservice.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adminseeker.inventoryservice.entities.Inventory;
import com.adminseeker.inventoryservice.entities.InventoryResponse;
import com.adminseeker.inventoryservice.entities.ProductQuantity;
import com.adminseeker.inventoryservice.entities.ProductQuantityRequest;
import com.adminseeker.inventoryservice.entities.QuantityResponse;
import com.adminseeker.inventoryservice.exceptions.DuplicateResourceException;
import com.adminseeker.inventoryservice.exceptions.LoginError;
import com.adminseeker.inventoryservice.exceptions.ResourceNotFound;
import com.adminseeker.inventoryservice.exceptions.ResourceUpdateError;
import com.adminseeker.inventoryservice.proxies.EmailRequest;
import com.adminseeker.inventoryservice.proxies.ProductResponse;
import com.adminseeker.inventoryservice.proxies.User;
import com.adminseeker.inventoryservice.repository.InventoryRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

@Service
@Transactional
public class InventoryService {
    
    @Autowired
    InventoryRepo repo;

    @Autowired
    ProductServiceRequest productServiceRequest;

    @Autowired
    UserServiceRequest userServiceRequest;

    public Inventory addInventory(Inventory inventory,Map<String,String> headers){
        User user = userServiceRequest.getUserByEmail(EmailRequest.builder().email(headers.get("x-auth-user-email")).build(), headers).orElseThrow(()-> new ResourceNotFound("User Not Found!"));
        inventory.setSkucode(inventory.getSkucode().toUpperCase());
        ProductResponse productresponse = productServiceRequest.getProductBySkucode(inventory.getSkucode()).orElseThrow(()-> new ResourceNotFound("Product Not Found!"));
        if(!user.getUserId().equals(inventory.getSellerId())) throw new LoginError("unauthorised user!");
        if(!user.getUserId().equals(productresponse.getSeller().getUserId())) throw new LoginError("unauthorised user!");
        if(!user.getRole().equals("seller")) throw new ResourceUpdateError("Not a Seller!");
        if(productresponse.getProduct().getVariants()==null && inventory.getVariants().size()!=0) throw new ResourceNotFound("Product Has No Variants!");
        Inventory check = repo.findBySkucode(inventory.getSkucode()).orElse(null); 
        if(check!=null) throw new DuplicateResourceException("Product Skucode Already Exists!");
        if(inventory.getQuantity()<0) throw new ResourceUpdateError("invalid quantity!");
        return repo.save(inventory);
    }

    public List<Inventory> getInventory(Map<String,String> headers){
        return repo.findAll();
    }

    public InventoryResponse getInventoryById(Long id,Map<String,String> headers){
        Inventory inventory = repo.findById(id).orElseThrow(()-> new ResourceNotFound("Inventory Not Found!"));
        ProductResponse productResponse = productServiceRequest.getProductBySkucode(inventory.getSkucode()).orElseThrow(()-> new ResourceNotFound("Product Not Found!"));
        InventoryResponse inventoryResponse = new InventoryResponse();
        inventoryResponse.setInventory(inventory);
        inventoryResponse.setProductResponse(productResponse);
        return inventoryResponse;
    }

    public InventoryResponse getInventoryBySkucode(String skucode,Map<String,String> headers){
        Inventory inventory = repo.findBySkucode(skucode.toUpperCase()).orElseThrow(()-> new ResourceNotFound("Inventory Not Found!"));
        ProductResponse productResponse = productServiceRequest.getProductBySkucode(inventory.getSkucode()).orElseThrow(()-> new ResourceNotFound("Product Not Found!"));
        InventoryResponse inventoryResponse = new InventoryResponse();
        inventoryResponse.setInventory(inventory);
        inventoryResponse.setProductResponse(productResponse);
        return inventoryResponse;
    }

    public QuantityResponse getProductQuantityBySkucode(String skucode,Map<String,String> headers){
        Inventory inventory = repo.findBySkucode(skucode.toUpperCase()).orElseThrow(()-> new ResourceNotFound("Inventory Not Found!"));
        productServiceRequest.getProductBySkucode(inventory.getSkucode()).orElseThrow(()-> new ResourceNotFound("Product Not Found!"));
        QuantityResponse quantityResponse = new QuantityResponse();
        quantityResponse.setQuantity(inventory.getQuantity());
        return quantityResponse;
    }


    public List<ProductQuantity>  getInventoryQuantityBySkuCodes(ProductQuantityRequest productQuantityRequest,Map<String,String> headers) throws Exception{
        List<ProductQuantity> updates = productQuantityRequest.getQuantityUpdates();
        if(updates==null || updates.size()==0) return new ArrayList<ProductQuantity>();
        for (ProductQuantity update : updates){
            Inventory inventorydb = repo.findBySkucode(update.getProductSkucode()).orElseThrow(()->new ResourceNotFound("Inventory Not Found!"));
            if(inventorydb.getQuantity()!=null){
                update.setQuantity(inventorydb.getQuantity());
            }
            if(inventorydb.getQuantity()<0) throw new ResourceUpdateError("invalid quantity!");
        }
        return updates;
    }
    

    public Inventory updateInventoryById(Inventory inventory,Long inventoryId,Map<String,String> headers) throws Exception{
        User user = userServiceRequest.getUserByEmail(EmailRequest.builder().email(headers.get("x-auth-user-email")).build(), headers).orElseThrow(()-> new ResourceNotFound("User Not Found!"));
        Inventory inventorydb = repo.findById(inventoryId).orElseThrow(()->new ResourceNotFound("Inventory Not Found!"));
        ProductResponse productResponse = productServiceRequest.getProductBySkucode(inventorydb.getSkucode()).orElseThrow(()-> new ResourceNotFound("Product Not Found!"));
        if(!user.getUserId().equals(productResponse.getSeller().getUserId())) throw new LoginError("unauthorised user!");
        if(inventory.getSkucode()!=null && !inventorydb.getSkucode().equals(inventory.getSkucode())){
            inventory.setSkucode(inventory.getSkucode().toUpperCase());
            Inventory check = repo.findBySkucode(inventory.getSkucode()).orElse(null);
            if(check!=null) throw new DuplicateResourceException("Product Skucode Already Exists!");
            productServiceRequest.getProductBySkucode(inventory.getSkucode()).orElseThrow(()-> new ResourceNotFound("Product Not Found!"));
        }
        if(inventory.getSkucode()==null && inventory.getQuantity()==null) throw new Exception("Nothing to update!");
        
        if(inventory.getSkucode()!=null){
            inventorydb.setSkucode(inventory.getSkucode());
        }

        if(inventory.getQuantity()!=null){
            inventorydb.setQuantity(inventory.getQuantity());
        }
        if(inventory.getQuantity()<0) throw new ResourceUpdateError("invalid quantity!");


        return repo.save(inventorydb);
    }

    public List<ProductQuantity>  updateInventoryQuantityBySkuCodes(ProductQuantityRequest productQuantityRequest,Map<String,String> headers) throws Exception{
        List<ProductQuantity> updates = productQuantityRequest.getQuantityUpdates();
        if(updates==null || updates.size()==0) return new ArrayList<ProductQuantity>();
        for (ProductQuantity update : updates){
            Inventory inventorydb = repo.findBySkucode(update.getProductSkucode()).orElseThrow(()->new ResourceNotFound("Inventory Not Found!"));

            if(update.getQuantity()!=null){
                inventorydb.setQuantity(update.getQuantity());
            }
            if(inventorydb.getQuantity()<0) throw new ResourceUpdateError("invalid quantity!");
            repo.save(inventorydb);
        }
        return updates;
    }

    public Inventory DeleteInventoryById(Long inventoryId,Map<String,String> headers){
        User user = userServiceRequest.getUserByEmail(EmailRequest.builder().email(headers.get("x-auth-user-email")).build(), headers).orElseThrow(()-> new ResourceNotFound("User Not Found!"));
        Inventory inventory = repo.findById(inventoryId).orElseThrow(()-> new ResourceNotFound("Inventory Not Found!"));
        ProductResponse productResponse = productServiceRequest.getProductBySkucode(inventory.getSkucode()).orElseThrow(()-> new ResourceNotFound("Product Not Found!"));
        if(!user.getUserId().equals(productResponse.getSeller().getUserId())) throw new LoginError("unauthorised user!");
        repo.delete(inventory);
        return inventory;        
    }
}
