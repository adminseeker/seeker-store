package com.adminseeker.productservice.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adminseeker.productservice.entities.Product;
import com.adminseeker.productservice.entities.ProductRequest;
import com.adminseeker.productservice.entities.ProductResponse;
import com.adminseeker.productservice.entities.Variant;
import com.adminseeker.productservice.exceptions.DuplicateResourceException;
import com.adminseeker.productservice.exceptions.ResourceNotFound;
import com.adminseeker.productservice.exceptions.ResourceUpdateError;
import com.adminseeker.productservice.proxies.User;
import com.adminseeker.productservice.repository.ProductRepo;

import java.util.List;

import javax.transaction.Transactional;

@Service
@Transactional
public class ProductService {
    
    @Autowired
    ProductRepo repo;

    @Autowired
    UserServiceRequest userServiceRequest;

    public Product addProduct(Product product){
        User user = userServiceRequest.getUserById(product.getSellerId()).orElseThrow(()-> new ResourceNotFound("Seller Not Found!"));
        product.setSkucode(product.getSkucode().toUpperCase());
        Product check = repo.findBySkucode(product.getSkucode()).orElse(null); 
        if(!user.getRole().equals("seller")) throw new ResourceUpdateError("Not a Seller!");
        if(check!=null) throw new DuplicateResourceException("Product Skucode Already Exists!");
        List<Variant> variants = product.getVariants();
        String previousVal=null;
        if(variants!=null){
            for(Variant v: variants){
                v.setVariantSkucode(v.getVariantSkucode().toUpperCase());
                if(v.getVariantSkucode().equals(previousVal)){
                    throw new DuplicateResourceException("Duplicate Variant Skucode Entry!");
                }
                previousVal=v.getVariantSkucode();
            }
        }
        return repo.save(product);
    }

    public List<Product> getProducts(){
        return repo.findAll();
    }

    public ProductResponse getProductById(Long id){
        Product product = repo.findById(id).orElseThrow(()-> new ResourceNotFound("Product Not Found!"));
        User seller = userServiceRequest.getUserById(product.getSellerId()).orElseThrow(()-> new ResourceNotFound("Seller Not Found!"));
        ProductResponse productResponse = new ProductResponse();
        productResponse.setProduct(product);
        productResponse.setSeller(seller);
        return productResponse;
    }

    public ProductResponse getProductBySkucode(String skucode){
        Product product = repo.findBySkucode(skucode.toUpperCase()).orElseThrow(()-> new ResourceNotFound("Product Not Found!"));
        User seller = userServiceRequest.getUserById(product.getSellerId()).orElseThrow(()-> new ResourceNotFound("Seller Not Found!"));
        ProductResponse productResponse = new ProductResponse();
        productResponse.setProduct(product);
        productResponse.setSeller(seller);
        return productResponse;
    }

    public Product updateProductById(Product product,Long productId) throws Exception{
        User user = userServiceRequest.getUserById(product.getSellerId()).orElseThrow(()-> new ResourceNotFound("Seller Not Found!"));
        if(!user.getRole().equals("seller")) throw new ResourceUpdateError("Not a Seller!");
        Product productdb = repo.findById(productId).orElseThrow(()->new ResourceNotFound("Product Not Found!"));
        if (!product.getSellerId().equals(productdb.getSellerId())) throw new ResourceUpdateError("Unauthorised User!");
        if(product.getSkucode()!=null){
            product.setSkucode(product.getSkucode().toUpperCase());
            Product check = repo.findBySkucode(product.getSkucode()).orElse(null);
            if(check!=null) throw new DuplicateResourceException("Product Skucode Already Exists!");
        }
        if(product.getName()==null && product.getDescription()==null && product.getPrice()==null && product.getVariants()==null) throw new Exception("Nothing to update!");
        
        if(product.getName()!=null){
            productdb.setName(product.getName());
        }

        if(product.getDescription()!=null){
            productdb.setDescription(product.getDescription());
        }

        if(product.getPrice()!=null){
            productdb.setPrice(product.getPrice());
        }

        if(product.getVariants()!=null){
            List<Variant> variants = product.getVariants();
            String previousVal=null;

            if(variants!=null){
                for(Variant v: variants){
                    v.setVariantSkucode(v.getVariantSkucode().toUpperCase());
                    if(v.getVariantSkucode().equals(previousVal)){
                        throw new DuplicateResourceException("Duplicate Variant Skucode Entry!");
                    }
                    previousVal=v.getVariantSkucode();
                }
    
            }
            productdb.getVariants().clear();
            productdb.getVariants().addAll(product.getVariants());
        }

        return repo.save(productdb);
    }

    public Product DeleteProductById(Long productId,ProductRequest productRequest){
        User user = userServiceRequest.getUserById(productRequest.getUserId()).orElseThrow(()-> new ResourceNotFound("Seller Not Found!"));
        if(!user.getRole().equals("seller")) throw new ResourceUpdateError("Not a Seller!");

        Product product = repo.findById(productId).orElseThrow(()-> new ResourceNotFound("Product Not Found!"));
        if (!productRequest.getUserId().equals(product.getSellerId())) throw new ResourceUpdateError("Unauthorised User!");
        repo.delete(product);
        return product;        
    }
}
