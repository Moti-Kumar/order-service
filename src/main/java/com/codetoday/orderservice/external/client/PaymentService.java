package com.codetoday.orderservice.external.client;

import com.codetoday.orderservice.exception.CustomOrderException;
import com.codetoday.orderservice.external.request.PaymentRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@CircuitBreaker(name="external", fallbackMethod = "paymentFallback")
@FeignClient(name="PAYMENT-SERVICE/payment")
public interface PaymentService {

    @PostMapping
    public ResponseEntity<Long> doPayment(@RequestBody PaymentRequest paymentRequest);

    default void paymentFallback(Exception e){
        throw new CustomOrderException("Payment is ia not available ","UNAVAILABLE",500);
    }
}
