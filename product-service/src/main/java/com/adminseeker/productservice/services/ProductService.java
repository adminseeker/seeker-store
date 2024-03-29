package com.adminseeker.productservice.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adminseeker.productservice.entities.Category;
import com.adminseeker.productservice.entities.Product;
import com.adminseeker.productservice.entities.ProductRequest;
import com.adminseeker.productservice.entities.ProductResponse;
import com.adminseeker.productservice.exceptions.DuplicateResourceException;
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
public class ProductService {
    
    @Autowired
    ProductRepo repo;

    @Autowired
    CategoryService categoryService;

    @Autowired
    UserServiceRequest userServiceRequest;

    public Product addProduct(ProductRequest productRequest,Map<String,String> headers){
        EmailRequest emailRequest = EmailRequest.builder().email(headers.get("x-auth-user-email")).build();
        User user = userServiceRequest.getUserByEmail(emailRequest,headers).orElseThrow(()-> new ResourceNotFound("Seller Not Found!"));        
        Product check = repo.findBySkucode(productRequest.getSkucode().toUpperCase()).orElse(null); 
        if (!user.getUserId().equals(productRequest.getSellerId())) throw new LoginError("Unauthorised User!");
        if(!user.getRole().equals("seller")) throw new ResourceUpdateError("Not a Seller!");
        if(check!=null) throw new DuplicateResourceException("Product Skucode Already Exists!");
        Category category = categoryService.getCategoriesById(productRequest.getCategoryId());
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setSellerId(productRequest.getSellerId());
        product.setImagePath(productRequest.getImagePath());
        product.setSkucode(productRequest.getSkucode().toUpperCase());
        product.setCategory(category);
        return repo.save(product);
    }

    public List<Product> getAllProducts(Map<String,String> headers){
        return repo.findAll();
    }

    public List<Product> getProductsBySeller(Long SellerId,Map<String,String> headers){
        List<Product> products = repo.findBySellerId(SellerId).orElseThrow(()-> new ResourceNotFound("Products Not Found!"));
        return products;
    }

    public List<Product> getProductsByCategoryCode(String categoryCode,Map<String,String> headers){
        List<Product> products = repo.findAllByCategoryCategoryCodeLike(categoryCode).orElseThrow(()-> new ResourceNotFound("Products Not Found!"));
        return products;
    }

    public ProductResponse getProductById(Long id,Map<String,String> headers){
        Product product = repo.findById(id).orElseThrow(()-> new ResourceNotFound("Product Not Found!"));
        User seller = userServiceRequest.getUserByIdPublic(product.getSellerId(),headers).orElseThrow(()-> new ResourceNotFound("Seller Not Found!"));
        ProductResponse productResponse = new ProductResponse();
        productResponse.setProduct(product);
        productResponse.setSeller(seller);
        return productResponse;
    }

    public ProductResponse getProductBySkucode(String skucode,Map<String,String> headers){
        Product product = repo.findBySkucode(skucode.toUpperCase()).orElseThrow(()-> new ResourceNotFound("Product Not Found!"));
        User seller = userServiceRequest.getUserByIdPublic(product.getSellerId(),headers).orElseThrow(()-> new ResourceNotFound("Seller Not Found!"));
        ProductResponse productResponse = new ProductResponse();
        productResponse.setProduct(product);
        productResponse.setSeller(seller);
        return productResponse;
    }

    public Product updateProductById(ProductRequest productRequest,Long productId,Map<String,String> headers) throws Exception{
        EmailRequest emailRequest = EmailRequest.builder().email(headers.get("x-auth-user-email")).build();
        User user = userServiceRequest.getUserByEmail(emailRequest,headers).orElseThrow(()-> new ResourceNotFound("Seller Not Found!"));
        if(!user.getRole().equals("seller")) throw new ResourceUpdateError("Not a Seller!");
        Product productdb = repo.findById(productId).orElseThrow(()->new ResourceNotFound("Product Not Found!"));
        if (!user.getUserId().equals(productdb.getSellerId())) throw new LoginError("Unauthorised User!");
      
        if(productRequest.getSkucode()!=null){
            productRequest.setSkucode(productRequest.getSkucode().toUpperCase());
            Product check = repo.findBySkucode(productRequest.getSkucode()).orElse(null);
            if(check!=null) throw new DuplicateResourceException("Product Skucode Already Exists!");
            productdb.setSkucode(productRequest.getSkucode());
        }
        if(productRequest.getSkucode()==null && productRequest.getName()==null && productRequest.getDescription()==null && productRequest.getPrice()==null && productRequest.getCategoryId()==null) throw new Exception("Nothing to update!");
        
        if(productRequest.getName()!=null){
            productdb.setName(productRequest.getName());
        }

        if(productRequest.getDescription()!=null){
            productdb.setDescription(productRequest.getDescription());
        }

        if(productRequest.getPrice()!=null){
            productdb.setPrice(productRequest.getPrice());
        }

        if(productRequest.getCategoryId()!=null){
            Category category = categoryService.getCategoriesById(productRequest.getCategoryId());
            productdb.setCategory(category);
        }
        return repo.save(productdb);
    }

    

    public Product DeleteProductById(Long productId,Map<String,String> headers){
        EmailRequest emailRequest = EmailRequest.builder().email(headers.get("x-auth-user-email")).build();
        User user = userServiceRequest.getUserByEmail(emailRequest,headers).orElseThrow(()-> new ResourceNotFound("Seller Not Found!"));
        if(!user.getRole().equals("seller")) throw new ResourceUpdateError("Not a Seller!");
        Product product = repo.findById(productId).orElseThrow(()-> new ResourceNotFound("Product Not Found!"));
        if (!user.getUserId().equals(product.getSellerId())) throw new LoginError("Unauthorised User!");
        product.setCategory(null);
        repo.delete(product);
        return product;        
    }
}
