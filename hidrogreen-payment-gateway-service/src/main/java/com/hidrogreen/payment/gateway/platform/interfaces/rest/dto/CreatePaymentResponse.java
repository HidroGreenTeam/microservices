package com.hidrogreen.payment.gateway.platform.interfaces.rest.dto;

public class CreatePaymentResponse {
  private String paymentId; // Nuestro ID interno de pago
  private String approvalUrl;
  private String status;

  public CreatePaymentResponse(String paymentId, String approvalUrl, String status) {
    this.paymentId = paymentId;
    this.approvalUrl = approvalUrl;
    this.status = status;
  }

  public String getPaymentId() {
    return paymentId;
  }

  public void setPaymentId(String paymentId) {
    this.paymentId = paymentId;
  }

  public String getApprovalUrl() {
    return approvalUrl;
  }

  public void setApprovalUrl(String approvalUrl) {
    this.approvalUrl = approvalUrl;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
