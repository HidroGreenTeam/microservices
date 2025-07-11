package com.hidrogreen.payment.gateway.platform.domain.port;

import com.hidrogreen.payment.gateway.platform.domain.valueobjects.Amount;

public interface PaymentProviderPort {
  PaymentProviderCreationResult createPayment(
          Amount amount,
          String description,
          String cancelUrl,
          String successUrl
  );

  PaymentProviderExecutionResult executePayment(String providerPaymentId, String payerId);
}