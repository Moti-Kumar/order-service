package com.codetoday.orderservice.service;

import com.codetoday.orderservice.model.OrderResponse;
import com.codetoday.orderservice.model.OrderRequest;

public interface OrderService {
    Long placeOrder(OrderRequest orderRequest);

    OrderResponse getOrderDetails(Long orderId);
}
