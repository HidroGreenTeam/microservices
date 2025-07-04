package com.hidrogreen.payment.gateway.platform.domain.port;

public class PaymentProviderExecutionResult {
  private final boolean success;
  private final String providerPaymentId; // Puede ser útil para confirmar
  // Podría incluir más detalles, como un mensaje de error si !success

  public PaymentProviderExecutionResult(boolean success, String providerPaymentId) {
    this.success = success;
    this.providerPaymentId = providerPaymentId;
  }

  public boolean isSuccess() {
    return success;
  }

  public String getProviderPaymentId() {
    return providerPaymentId;
  }
}
