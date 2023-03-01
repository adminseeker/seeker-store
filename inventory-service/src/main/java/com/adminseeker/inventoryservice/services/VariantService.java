package com.adminseeker.inventoryservice.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adminseeker.inventoryservice.entities.Variant;
import com.adminseeker.inventoryservice.entities.VariantRequest;
import com.adminseeker.inventoryservice.entities.Inventory;
import com.adminseeker.inventoryservice.entities.QuantityResponse;
import com.adminseeker.inventoryservice.entities.QuantityUpdate;
import com.adminseeker.inventoryservice.entities.QuantityUpdateRequest;
import com.adminseeker.inventoryservice.exceptions.ResourceNotFound;
import com.adminseeker.inventoryservice.exceptions.ResourceUpdateError;
import com.adminseeker.inventoryservice.proxies.ProductResponse;
import com.adminseeker.inventoryservice.repository.InventoryRepo;

import java.util.List;

import javax.transaction.Transactional;

@Service
@Transactional
public class VariantService {
    
    @Autowired
    InventoryRepo repo;

    @Autowired
    ProductServiceRequest productServiceRequest;

    public Variant addVariant(Long inventoryId,VariantRequest variantRequest){
        Variant variant = variantRequest.getVariant();
        Long userId = variantRequest.getUserId();
        Inventory inventorydb = repo.findById(inventoryId).orElseThrow(()->new ResourceNotFound("Inventory Not Found!"));
        if (!userId.equals(inventorydb.getSellerId())) throw new ResourceUpdateError("Unauthorised User!"); 
        ProductResponse productResponse = productServiceRequest.getProductBySkucode(inventorydb.getSkucode()).orElseThrow(()-> new ResourceNotFound("Product Not Found!"));
        if(productResponse.getProduct().getVariants()==null ) throw new ResourceNotFound("Product Has No Variants!");

        Boolean isVariantCorrect = false;
        for(com.adminseeker.inventoryservice.proxies.Variant v : productResponse.getProduct().getVariants()){
            if(v.getVariantSkucode().equals(variantRequest.getVariant().getVariantSkucode())){
                isVariantCorrect=true;
            }
        }

        if(!isVariantCorrect) throw new ResourceNotFound("Variant Doesn't exist!");       

        List<Variant> variants = inventorydb.getVariants();
        Boolean isSkucodePresent=false;
        for (Variant v : variants){
            if(v.getVariantSkucode().equals(variantRequest.getVariant().getVariantSkucode())){
                isSkucodePresent=true;
                break;
            }             
        }        
        if (isSkucodePresent) throw new ResourceNotFound("Variant Already Exists!");
        if(variant.getQuantity()<0) throw new ResourceUpdateError("invalid quantity!");

        variants.add(variant);
        inventorydb.setVariants(variants);
        Inventory updatedInventory = repo.save(inventorydb);

        return updatedInventory.getVariants().get(updatedInventory.getVariants().size()-1);
    }

    public List<Variant> getVariants(Long inventoryId){
        Inventory inventorydb = repo.findById(inventoryId).orElseThrow(()->new ResourceNotFound("Inventory Not Found!"));
        List<Variant> variants = inventorydb.getVariants();
        return variants;
    }

    public Variant getVariantById(Long inventoryId,Long variantId){
        Inventory inventorydb = repo.findById(inventoryId).orElseThrow(()->new ResourceNotFound("Inventory Not Found!"));
        List<Variant> variants = inventorydb.getVariants();
        Variant variant = null;
        for (Variant v : variants){
            if(v.getVariantId().equals(variantId)){
                variant=v;
                break;
            }
        }
        if(variant==null) throw new ResourceNotFound("Variant Not Found!");
        return variant;
        
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

    public List<QuantityUpdate> getVariantQuantityBySkucodes(QuantityUpdateRequest quantityUpdateRequest){
        List<QuantityUpdate> updates =  quantityUpdateRequest.getQuantityUpdates();
        for(QuantityUpdate update : updates){
            Inventory inventorydb = repo.findBySkucode(update.getProductSkucode()).orElseThrow(()->new ResourceNotFound("Inventory Not Found!"));
            if(inventorydb.getQuantity()<0) throw new ResourceUpdateError("invalid quantity!");

            List<Variant> variantsdb = inventorydb.getVariants();
            if(update.getVariantSkucode()==null) throw new ResourceUpdateError("Nothing to update!");
            // ProductResponse productResponse = productServiceRequest.getProductBySkucode(inventorydb.getSkucode()).orElseThrow(()-> new ResourceNotFound("Product Not Found!"));
            // if(productResponse.getProduct().getVariants()==null ) throw new ResourceNotFound("Product Has No Variants!");

            Boolean isPresent=false;
            Variant variant=null;
            for (Variant v : variantsdb){
                if(v.getVariantSkucode().equals(update.getVariantSkucode())){
                    Integer index = variantsdb.indexOf(v);
                    update.setQuantity(v.getQuantity());
                    variant=v;
                    isPresent=true;
                }             
            }        
            if (!isPresent) throw new ResourceNotFound("Variant Not Found!");

        }
        
        return updates;
    }

    public Variant UpdateVariantById(Long inventoryId,VariantRequest variantRequest,Long variantId){
        Long userId=variantRequest.getUserId();
        Variant variant=variantRequest.getVariant();
        Inventory inventorydb = repo.findById(inventoryId).orElseThrow(()->new ResourceNotFound("Inventory Not Found!"));
        if (!userId.equals(inventorydb.getSellerId())) throw new ResourceUpdateError("Unauthorised User!");

        List<Variant> variantsdb = inventorydb.getVariants();
        if(variant==null) throw new ResourceUpdateError("Nothing to update!");
        ProductResponse productResponse = productServiceRequest.getProductBySkucode(inventorydb.getSkucode()).orElseThrow(()-> new ResourceNotFound("Product Not Found!"));
        if(productResponse.getProduct().getVariants()==null ) throw new ResourceNotFound("Product Has No Variants!");

        Boolean isPresent=false;
        for (Variant v : variantsdb){
            if(v.getVariantId().equals(variantId)){
                Integer index = variantsdb.indexOf(v);
                variant.setVariantId(variantId);
                variantsdb.set(index, variant);
                isPresent=true;
                break;
            }             
        }        
        if (!isPresent) throw new ResourceNotFound("Variant Not Found!");

        Boolean isSkucodePresent=false;
        for (Variant v : variantsdb){
            if(!v.getVariantId().equals(variantId) && v.getVariantSkucode().equals(variantRequest.getVariant().getVariantSkucode())){
                isSkucodePresent=true;
                break;
            }             
        }        
        if (isSkucodePresent) throw new ResourceNotFound("Variant Already Exists!");
        if(variant.getQuantity()<0) throw new ResourceUpdateError("invalid quantity!");

        Boolean isVariantCorrect = false;
        for(com.adminseeker.inventoryservice.proxies.Variant v : productResponse.getProduct().getVariants()){
            if(v.getVariantSkucode().equals(variantRequest.getVariant().getVariantSkucode())){
                isVariantCorrect=true;
                break;
            }
        }

        if(!isVariantCorrect) throw new ResourceNotFound("Variant Doesn't exist!");

        inventorydb.setVariants(variantsdb);
        repo.save(inventorydb);
        return variant;
    }

    public List<QuantityUpdate> UpdateVariantQuantityBySkucodes(QuantityUpdateRequest quantityUpdateRequest){
        List<QuantityUpdate> updates =  quantityUpdateRequest.getQuantityUpdates();
        for(QuantityUpdate update : updates){
            Inventory inventorydb = repo.findBySkucode(update.getProductSkucode()).orElseThrow(()->new ResourceNotFound("Inventory Not Found!"));
            if(update.getQuantity()<0) throw new ResourceUpdateError("invalid quantity!");

            List<Variant> variantsdb = inventorydb.getVariants();
            if(update.getVariantSkucode()==null) throw new ResourceUpdateError("Nothing to update!");
            // ProductResponse productResponse = productServiceRequest.getProductBySkucode(inventorydb.getSkucode()).orElseThrow(()-> new ResourceNotFound("Product Not Found!"));
            // if(productResponse.getProduct().getVariants()==null ) throw new ResourceNotFound("Product Has No Variants!");

            Boolean isPresent=false;
            Variant variant=null;
            for (Variant v : variantsdb){
                if(v.getVariantSkucode().equals(update.getVariantSkucode())){
                    Integer index = variantsdb.indexOf(v);
                    v.setQuantity(update.getQuantity());
                    variantsdb.set(index, v);
                    variant=v;
                    isPresent=true;
                }             
            }        
            if (!isPresent) throw new ResourceNotFound("Variant Not Found!");

            inventorydb.setVariants(variantsdb);
            repo.save(inventorydb);
        }
        
        return updates;
    }


    public Variant deleteVariantById(Long inventoryId,Long variantId,VariantRequest variantRequest){
        Long userId=variantRequest.getUserId();
        Inventory inventorydb = repo.findById(inventoryId).orElseThrow(()->new ResourceNotFound("Inventory Not Found!"));
        if (!userId.equals(inventorydb.getSellerId())) throw new ResourceUpdateError("Unauthorised User!");
        List<Variant> variants = inventorydb.getVariants();
        Variant variant=null;
        Boolean isPresent = false;
        for (Variant addr : variants){
            if(addr.getVariantId().equals(variantId)){
                variant=addr;
                variants.remove(addr);
                isPresent=true;
                break;
            }
        }
        if (!isPresent) throw new ResourceNotFound("Variant Not Found!");
        inventorydb.setVariants(variants);       
        repo.save(inventorydb);
        return variant;        
    }
}
