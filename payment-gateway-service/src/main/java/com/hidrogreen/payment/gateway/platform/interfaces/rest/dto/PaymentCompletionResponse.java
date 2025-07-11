package com.hidrogreen.payment.gateway.platform.interfaces.rest.dto;

public class PaymentCompletionResponse {
  private String paymentId; // Nuestro ID interno de pago
  private String status;
  private String message;

  public PaymentCompletionResponse(String paymentId, String status, String message) {
    this.paymentId = paymentId;
    this.status = status;
    this.message = message;
  }

  public String getPaymentId() {
    return paymentId;
  }

  public void setPaymentId(String paymentId) {
    this.paymentId = paymentId;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
