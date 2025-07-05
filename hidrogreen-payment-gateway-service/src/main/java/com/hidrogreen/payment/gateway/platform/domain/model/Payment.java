package com.hidrogreen.payment.gateway.platform.domain.model;

import com.hidrogreen.payment.gateway.platform.domain.valueobjects.Amount;
import com.hidrogreen.payment.gateway.platform.domain.valueobjects.PaymentId;
import com.hidrogreen.payment.gateway.platform.domain.valueobjects.PaymentStatus;

import java.util.Objects;
import java.util.UUID;

public class Payment {

    private final PaymentId id; // Value Object para el ID
    private String orderDescription;
    private Amount amount; // Value Object para el monto y moneda
    private PaymentStatus status;
    private String paymentProviderId; // ID del pago en el proveedor externo (e.g., PayPal)
    private String approvalUrl;       // URL para que el usuario apruebe el pago

    // Constructor para un nuevo pago
    public Payment(String orderDescription, Amount amount) {
        this.id = new PaymentId(UUID.randomUUID()); // Generar un ID único interno
        this.orderDescription = Objects.requireNonNull(orderDescription, "Order description cannot be null");
        this.amount = Objects.requireNonNull(amount, "Amount cannot be null");
        this.status = PaymentStatus.PENDING_APPROVAL; // Estado inicial
    }

    public Payment(PaymentId id, String orderDescription, Amount amount, PaymentStatus status, String paymentProviderId, String approvalUrl) {
        this.id = id;
        this.orderDescription = orderDescription;
        this.amount = amount;
        this.status = status;
        this.paymentProviderId = paymentProviderId;
        this.approvalUrl = approvalUrl;
    }

    public void markAsCreatedByProvider(String paymentProviderId, String approvalUrl) {
        if (this.status != PaymentStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Payment must be in PENDING_APPROVAL state to be marked as created by provider.");
        }
        this.paymentProviderId = Objects.requireNonNull(paymentProviderId, "Payment provider ID cannot be null");
        this.approvalUrl = Objects.requireNonNull(approvalUrl, "Approval URL cannot be null");
        // El estado sigue siendo PENDING_APPROVAL hasta que el usuario apruebe
    }

    public void markAsApproved() {
        if (this.status != PaymentStatus.PENDING_APPROVAL && this.status != PaymentStatus.CREATED) {
             // Podríamos tener un estado CREATED si el proveedor lo devuelve así antes de la aprobación
            throw new IllegalStateException("Payment cannot be approved from its current state: " + this.status);
        }
        this.status = PaymentStatus.APPROVED;
        this.approvalUrl = null; // Ya no es necesaria
    }

    public void markAsFailed(String reason) {
        // Podríamos añadir un campo 'failureReason'
        this.status = PaymentStatus.FAILED;
        System.out.println("Payment failed: " + reason); // Log o manejo más sofisticado
    }

    public void markAsCancelled() {
        if (this.status != PaymentStatus.PENDING_APPROVAL && this.status != PaymentStatus.CREATED) {
            throw new IllegalStateException("Payment cannot be cancelled from its current state: " + this.status);
        }
        this.status = PaymentStatus.CANCELLED;
    }


    // Getters
    public PaymentId getId() {
        return id;
    }

    public String getOrderDescription() {
        return orderDescription;
    }

    public Amount getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getPaymentProviderId() {
        return paymentProviderId;
    }

    public String getApprovalUrl() {
        return approvalUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(id, payment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
