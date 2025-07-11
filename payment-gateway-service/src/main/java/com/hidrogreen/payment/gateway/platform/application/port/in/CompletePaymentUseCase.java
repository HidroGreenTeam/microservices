package com.hidrogreen.payment.gateway.platform.application.port.in;

import com.hidrogreen.payment.gateway.platform.domain.model.Payment;

import java.util.Objects;

public interface CompletePaymentUseCase {

  Payment completePayment(CompletePaymentCommand command);

  void markPaymentAsCancelledByProvider(String providerPaymentId); // Nueva adición

  class CompletePaymentCommand {
    private final String providerPaymentId; 
    private final String payerId;

    public CompletePaymentCommand(String providerPaymentId, String payerId) {
      if (providerPaymentId == null || providerPaymentId.trim().isEmpty()) {
        throw new IllegalArgumentException("Provider Payment ID cannot be null or empty.");
      }
      // PayerID podría ser opcional en algunos flujos de cancelación, pero para 'success' es usualmente requerido.
      if (payerId == null || payerId.trim().isEmpty()) {
        throw new IllegalArgumentException("Payer ID cannot be null or empty.");
      }
      this.providerPaymentId = providerPaymentId;
      this.payerId = payerId;
    }

    public String getProviderPaymentId() {
      return providerPaymentId;
    }

    public String getPayerId() {
      return payerId;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      CompletePaymentCommand that = (CompletePaymentCommand) o;
      return Objects.equals(providerPaymentId, that.providerPaymentId) &&
              Objects.equals(payerId, that.payerId);
    }

    @Override
    public int hashCode() {
      return Objects.hash(providerPaymentId, payerId);
    }
  }
}
