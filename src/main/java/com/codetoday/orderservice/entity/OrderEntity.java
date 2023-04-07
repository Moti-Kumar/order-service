package com.codetoday.orderservice.entity;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;
@Entity
@Table(name="ORDER_DETAILS")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name="PRODUCT_ID")
    private Long productId;
    @Column(name="QUANTITY")
    private Long quantity;
    @Column(name="ORDER_DATE")
    private Instant orderDate;
    @Column(name="ORDER_STATUS")
    private String orderStatus;
    @Column(name="PRODUCT_AMOUNT")
    private Long productAmount;

}
