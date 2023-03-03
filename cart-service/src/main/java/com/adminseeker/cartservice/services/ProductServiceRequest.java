package com.adminseeker.cartservice.services;

import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.adminseeker.cartservice.proxies.ProductResponse;

@FeignClient(value="product-feign-client",url="http://${PRODUCT_SERVICE_URL:localhost:8082}/api/v1/products")
public interface ProductServiceRequest {

    @GetMapping("/skucode/{skucode}")
    Optional<ProductResponse> getProductBySkucode(@PathVariable("skucode") String skucode);

    @GetMapping("/{productId}")
    Optional<ProductResponse> getProductById(@PathVariable("productId") Long productId);
}