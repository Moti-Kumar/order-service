package com.codetoday.orderservice.service;

import com.codetoday.orderservice.entity.OrderEntity;
import com.codetoday.orderservice.exception.CustomOrderException;
import com.codetoday.orderservice.external.client.PaymentService;
import com.codetoday.orderservice.external.client.ProductService;
import com.codetoday.orderservice.external.request.PaymentRequest;
import com.codetoday.orderservice.external.response.PaymentResponse;
import com.codetoday.orderservice.external.response.ProductResponse;
import com.codetoday.orderservice.model.OrderResponse;
import com.codetoday.orderservice.model.OrderRequest;
import com.codetoday.orderservice.model.PaymentDetails;
import com.codetoday.orderservice.model.ProductDetails;
import com.codetoday.orderservice.repository.OrderRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Long placeOrder(OrderRequest orderRequest) {
        //Order Entity -> Save the data with Status Order Created
        //Product Service - Block Products (Reduce the Quantity)
        //Payment Service -> Payments -> Success-> COMPLETE, Else
        //CANCELLED
        log.info("Placing Order Request: {}", orderRequest);
        log.info("Calling product service: {}");

        productService.reduceQuantity(orderRequest.getProductId(), orderRequest.getQuantity());

        log.info("Creating Order -----");
        OrderEntity orderEntity = OrderEntity.builder()
                .productAmount(orderRequest.getTotalAmount())
                .orderDate(Instant.now())
                .quantity(orderRequest.getQuantity())
                .productId(orderRequest.getProductId())
                .orderStatus("CREATED")
                .build();
        orderEntity = orderRepository.save(orderEntity);

        log.info("Calling payment service for ::{}" ,orderEntity.getId()) ;
        PaymentRequest paymentRequest=PaymentRequest.builder()
                .orderId(orderEntity.getId())
                .paymentMode(orderRequest.getPaymentMode())
                .amount(orderRequest.getTotalAmount())
                .build();
        String orderStatus=null;
        try{
            paymentService.doPayment(paymentRequest);
            log.info("Payment is done Successfully");
            orderStatus="PLACED";
        } catch (Exception e) {
            log.error("Payment is failed ");
            orderStatus="PAYMENT_FAILED";
            throw new RuntimeException(e);
        }
        orderEntity.setOrderStatus(orderStatus);
        orderRepository.save(orderEntity);
        log.info("Order is placed Successfully with order id : {}", orderEntity.getId());
        return orderEntity.getId();
    }

    @Override
    public OrderResponse getOrderDetails(Long orderId) {
        log.info("Get oder details for orderID::{}",orderId);
        OrderEntity orderEntity=orderRepository.findById(orderId)
                .orElseThrow(()->new CustomOrderException("Order is not found","NOT FOUND", 404));
        log.info("Invoking product service  to fetch product details for productID ::{}",orderEntity.getProductId());
        ProductResponse productResponse=restTemplate.getForObject(
                "http://PRODUCT-SERVICE/product/"+orderEntity.getProductId(),ProductResponse.class);
        ProductDetails productDetails=ProductDetails.builder()
                .productName(productResponse.getProductName())
                .productId(productResponse.getProductId())
                .build();

        log.info("Getting payment information form the payment Service");
        PaymentResponse paymentResponse=restTemplate.getForObject(
                "http://PAYMENT-SERVICE/payment/order/"+orderEntity.getId(),PaymentResponse.class
        );
log.info("PaymentID---{}",paymentResponse.getPaymentId());
        PaymentDetails paymentDetails=PaymentDetails.builder()
                .paymentId(paymentResponse.getPaymentId())
                .paymentStatus(paymentResponse.getStatus())
                .paymentDate(paymentResponse.getPaymentDate())
                .paymentMode(paymentResponse.getPaymentMode())
                .build();

        OrderResponse orderResponse= OrderResponse.builder()
                .orderId(orderEntity.getId())
                .amount(orderEntity.getProductAmount())
                .orderStatus(orderEntity.getOrderStatus())
                .orderDate(orderEntity.getOrderDate())
                .productDetails(productDetails)
                .paymentDetails(paymentDetails)
                .build();
        return orderResponse;
    }
}
