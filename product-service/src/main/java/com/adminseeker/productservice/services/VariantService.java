package com.adminseeker.productservice.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adminseeker.productservice.entities.Variant;
import com.adminseeker.productservice.entities.Product;
import com.adminseeker.productservice.exceptions.LoginError;
import com.adminseeker.productservice.exceptions.ResourceNotFound;
import com.adminseeker.productservice.exceptions.ResourceUpdateError;
import com.adminseeker.productservice.proxies.EmailRequest;
import com.adminseeker.productservice.proxies.User;
import com.adminseeker.productservice.repository.ProductRepo;

import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

@Service
@Transactional
public class VariantService {
    
    @Autowired
    ProductRepo repo;

    @Autowired
    UserServiceRequest userServiceRequest;

    public Variant addVariant(Long productId,Variant variant,Map<String,String> headers){
        EmailRequest emailRequest = EmailRequest.builder().email(headers.get("x-auth-user-email")).build();
        User user = userServiceRequest.getUserByEmail(emailRequest,headers).orElseThrow(()-> new ResourceNotFound("Seller Not Found!"));
        if(!user.getRole().equals("seller")) throw new ResourceUpdateError("Not a Seller!");
        Product productdb = repo.findById(productId).orElseThrow(()->new ResourceNotFound("Product Not Found!"));
        if (!user.getUserId().equals(productdb.getSellerId())) throw new LoginError("Unauthorised User!"); 
        List<Variant> variants = productdb.getVariants();
        Boolean isSkucodePresent=false;
        for (Variant v : variants){
            if(v.getVariantSkucode().equals(variant.getVariantSkucode())){
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

    public List<Variant> getVariants(Long productId,Map<String,String> headers){
        Product productdb = repo.findById(productId).orElseThrow(()->new ResourceNotFound("Product Not Found!"));
        List<Variant> variants = productdb.getVariants();
        return variants;
    }

    public Variant getVariantById(Long productId,Long variantId,Map<String,String> headers){
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

    public Variant UpdateVariantById(Long productId,Variant variant,Long variantId,Map<String,String> headers){
        EmailRequest emailRequest = EmailRequest.builder().email(headers.get("x-auth-user-email")).build();
        User user = userServiceRequest.getUserByEmail(emailRequest,headers).orElseThrow(()-> new ResourceNotFound("Seller Not Found!"));
        if(!user.getRole().equals("seller")) throw new ResourceUpdateError("Not a Seller!");
        if(!user.getEmail().equals(headers.get("x-auth-user-email"))) throw new LoginError("unauthorised user!");
        Product productdb = repo.findById(productId).orElseThrow(()->new ResourceNotFound("Product Not Found!"));
        if (!user.getUserId().equals(productdb.getSellerId())) throw new LoginError("Unauthorised User!");

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
            if(!v.getVariantId().equals(variantId) && v.getVariantSkucode().equals(variant.getVariantSkucode())){
                isSkucodePresent=true;
                break;
            }             
        }        
        if (isSkucodePresent) throw new ResourceNotFound("Variant Already Exists!");

        productdb.setVariants(variantsdb);
        repo.save(productdb);
        return variant;
    }

    public Variant deleteVariantById(Long productId,Long variantId,Map<String,String> headers){
        EmailRequest emailRequest = EmailRequest.builder().email(headers.get("x-auth-user-email")).build();
        User user = userServiceRequest.getUserByEmail(emailRequest,headers).orElseThrow(()-> new ResourceNotFound("Seller Not Found!"));        if(!user.getRole().equals("seller")) throw new ResourceUpdateError("Not a Seller!");
        Product productdb = repo.findById(productId).orElseThrow(()->new ResourceNotFound("Product Not Found!"));
        if (!user.getUserId().equals(productdb.getSellerId())) throw new LoginError("Unauthorised User!");
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
