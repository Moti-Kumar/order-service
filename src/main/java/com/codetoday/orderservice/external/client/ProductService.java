package com.codetoday.orderservice.external.client;

import com.codetoday.orderservice.exception.CustomOrderException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
@CircuitBreaker(name="external", fallbackMethod = "productFallback")
@FeignClient(name="PRODUCT-SERVICE/product")
public interface ProductService {
    @PutMapping("/reduceQuantity/{id}")
    public ResponseEntity<Void> reduceQuantity(
            @PathVariable("id") long productId,
            @RequestParam long quantity
    );

    default void productFallback(Exception e){
        throw new CustomOrderException("Product is ia not available ","UNAVAILABLE",500);
    }
}
