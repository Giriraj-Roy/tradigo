package com.roy.tradigo.model;

import com.roy.tradigo.domain.PaymentMethod;
import com.roy.tradigo.domain.PaymentOrderStatus;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class PaymentOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long amount;

    private PaymentOrderStatus paymentOrderStatus;
    private PaymentMethod paymentMethod;

    @ManyToOne
    private User user;
}
