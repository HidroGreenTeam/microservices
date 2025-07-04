package com.hidrogreen.payment.gateway.platform.domain.valueobjects;

import java.math.BigDecimal;
import java.util.Objects;

public class Amount {
  private final BigDecimal value;
  private final String currency;

  public Amount(BigDecimal value, String currency) {
    if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Amount value must be positive.");
    }
    if (currency == null || currency.trim().isEmpty()) {
      throw new IllegalArgumentException("Currency cannot be null or empty.");
    }
    this.value = value;
    this.currency = currency.toUpperCase();
  }

  public BigDecimal getValue() {
    return value;
  }

  public String getCurrency() {
    return currency;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Amount amount = (Amount) o;
    return Objects.equals(value, amount.value) && Objects.equals(currency, amount.currency);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, currency);
  }
}
