package com.hidrogreen.payment.gateway.platform.domain.port;

public class PaymentProviderCreationResult {
  private final String providerPaymentId;
  private final String approvalUrl;

  public PaymentProviderCreationResult(String providerPaymentId, String approvalUrl) {
    this.providerPaymentId = providerPaymentId;
    this.approvalUrl = approvalUrl;
  }

  public String getProviderPaymentId() {
    return providerPaymentId;
  }

  public String getApprovalUrl() {
    return approvalUrl;
  }
}