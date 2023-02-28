package com.adminseeker.productservice.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adminseeker.productservice.entities.Variant;
import com.adminseeker.productservice.entities.VariantRequest;
import com.adminseeker.productservice.entities.Product;
import com.adminseeker.productservice.exceptions.ResourceNotFound;
import com.adminseeker.productservice.exceptions.ResourceUpdateError;
import com.adminseeker.productservice.proxies.User;
import com.adminseeker.productservice.repository.ProductRepo;

import java.util.List;

import javax.transaction.Transactional;

@Service
@Transactional
public class VariantService {
    
    @Autowired
    ProductRepo repo;

    @Autowired
    UserServiceRequest userServiceRequest;

    public Variant addVariant(Long productId,VariantRequest variantRequest){
        Variant variant = variantRequest.getVariant();
        Long userId = variantRequest.getUserId();
        User user = userServiceRequest.getUserById(userId).orElseThrow(()-> new ResourceNotFound("Seller Not Found!"));
        if(!user.getRole().equals("seller")) throw new ResourceUpdateError("Not a Seller!");
        Product productdb = repo.findById(productId).orElseThrow(()->new ResourceNotFound("Product Not Found!"));
        if (!userId.equals(productdb.getSellerId())) throw new ResourceUpdateError("Unauthorised User!"); 
        List<Variant> variants = productdb.getVariants();
        Boolean isSkucodePresent=false;
        for (Variant v : variants){
            if(v.getVariantSkucode().equals(variantRequest.getVariant().getVariantSkucode())){
                isSkucodePresent=true;
                break;
            }             
        }        
        if (isSkucodePresent) throw new ResourceNotFound("Variant Already Exists!");
        variants.add(variant);
        productdb.setVariants(variants);
        Product updatedProduct = repo.save(productdb);

        return updatedProduct.getVariants().get(updatedProduct.getVariants().size()-1);
    }

    public List<Variant> getVariants(Long productId){
        Product productdb = repo.findById(productId).orElseThrow(()->new ResourceNotFound("Product Not Found!"));
        List<Variant> variants = productdb.getVariants();
        return variants;
    }

    public Variant getVariantById(Long productId,Long variantId){
        Product productdb = repo.findById(productId).orElseThrow(()->new ResourceNotFound("Product Not Found!"));
        List<Variant> variants = productdb.getVariants();
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

    public Variant UpdateVariantById(Long productId,VariantRequest variantRequest,Long variantId){
        Long userId=variantRequest.getUserId();
        Variant variant=variantRequest.getVariant();
        User user = userServiceRequest.getUserById(userId).orElseThrow(()-> new ResourceNotFound("Seller Not Found!"));
        if(!user.getRole().equals("seller")) throw new ResourceUpdateError("Not a Seller!");
        Product productdb = repo.findById(productId).orElseThrow(()->new ResourceNotFound("Product Not Found!"));
        if (!userId.equals(productdb.getSellerId())) throw new ResourceUpdateError("Unauthorised User!");

        List<Variant> variantsdb = productdb.getVariants();
        if(variant==null) throw new ResourceUpdateError("Nothing to update!");


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

        productdb.setVariants(variantsdb);
        repo.save(productdb);
        return variant;
    }

    public Variant deleteVariantById(Long productId,Long variantId,VariantRequest variantRequest){
        Long userId=variantRequest.getUserId();
        User user = userServiceRequest.getUserById(userId).orElseThrow(()-> new ResourceNotFound("Seller Not Found!"));
        if(!user.getRole().equals("seller")) throw new ResourceUpdateError("Not a Seller!");
        Product productdb = repo.findById(productId).orElseThrow(()->new ResourceNotFound("Product Not Found!"));
        if (!userId.equals(productdb.getSellerId())) throw new ResourceUpdateError("Unauthorised User!");
        List<Variant> variants = productdb.getVariants();
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
        productdb.setVariants(variants);       
        repo.save(productdb);
        return variant;        
    }
}
