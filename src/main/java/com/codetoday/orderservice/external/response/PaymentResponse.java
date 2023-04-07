package com.codetoday.orderservice.external.response;

import com.codetoday.orderservice.model.PaymentMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private long paymentId;
    private long amount;
    private String referenceNumber;
    private PaymentMode paymentMode;
    private String status;
    private Instant paymentDate;
}
