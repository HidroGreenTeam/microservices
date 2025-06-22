package com.hidrogreen.payment.gateway.platform.interfaces.rest.dto;

import java.math.BigDecimal;

public class CreatePaymentRequest {

  private BigDecimal amount;

  private String currency; // e.g., "USD"

  private String orderDescription;

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public String getOrderDescription() {
    return orderDescription;
  }

  public void setOrderDescription(String orderDescription) {
    this.orderDescription = orderDescription;
  }
}
