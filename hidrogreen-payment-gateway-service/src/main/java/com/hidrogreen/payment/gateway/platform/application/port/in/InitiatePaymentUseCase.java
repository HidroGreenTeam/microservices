package com.hidrogreen.payment.gateway.platform.application.port.in;

import com.hidrogreen.payment.gateway.platform.domain.model.Payment;

import java.math.BigDecimal;
import java.util.Objects;

public interface InitiatePaymentUseCase {

  Payment initiatePayment(InitiatePaymentCommand command, String successUrl, String cancelUrl);

  class InitiatePaymentCommand {
    private final BigDecimal amountValue;
    private final String currency;
    private final String orderDescription;

    public InitiatePaymentCommand(BigDecimal amountValue, String currency, String orderDescription) {
      if (amountValue == null || amountValue.compareTo(BigDecimal.ZERO) <= 0) {
        throw new IllegalArgumentException("Amount value must be positive.");
      }
      if (currency == null || currency.trim().isEmpty()) {
        throw new IllegalArgumentException("Currency cannot be null or empty.");
      }
      if (orderDescription == null || orderDescription.trim().isEmpty()) {
        throw new IllegalArgumentException("Order description cannot be null or empty.");
      }

      this.amountValue = amountValue;
      this.currency = currency;
      this.orderDescription = orderDescription;
    }

    public BigDecimal getAmountValue() {
      return amountValue;
    }

    public String getCurrency() {
      return currency;
    }

    public String getOrderDescription() {
      return orderDescription;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      InitiatePaymentCommand command = (InitiatePaymentCommand) o;
      return Objects.equals(amountValue, command.amountValue) &&
              Objects.equals(currency, command.currency) &&
              Objects.equals(orderDescription, command.orderDescription);
    }

    @Override
    public int hashCode() {
      return Objects.hash(amountValue, currency, orderDescription);
    }
  }
}
