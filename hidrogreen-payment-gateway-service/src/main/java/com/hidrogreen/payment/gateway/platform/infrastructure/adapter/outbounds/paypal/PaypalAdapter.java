package com.hidrogreen.payment.gateway.platform.infrastructure.adapter.outbounds.paypal;

import com.hidrogreen.payment.gateway.platform.domain.exception.PaymentException;
import com.hidrogreen.payment.gateway.platform.domain.port.PaymentProviderCreationResult;
import com.hidrogreen.payment.gateway.platform.domain.port.PaymentProviderExecutionResult;
import com.hidrogreen.payment.gateway.platform.domain.port.PaymentProviderPort;
import com.paypal.api.payments.*;
        import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component // Para que Spring lo detecte como un bean y lo pueda inyectar
public class PaypalAdapter implements PaymentProviderPort {

  private static final Logger logger = LoggerFactory.getLogger(PaypalAdapter.class);
  private final APIContext apiContext;

  public PaypalAdapter(APIContext apiContext) {
    this.apiContext = apiContext;
  }

  @Override
  public PaymentProviderCreationResult createPayment(com.hidrogreen.payment.gateway.platform.domain.valueobjects.Amount domainAmount,
                                                     String description,
                                                     String cancelUrl,
                                                     String successUrl) {
    com.paypal.api.payments.Amount paypalAmount = new com.paypal.api.payments.Amount();
    paypalAmount.setCurrency(domainAmount.getCurrency());
    // PayPal espera el total como String formateado a 2 decimales
    paypalAmount.setTotal(String.format(Locale.US, "%.2f", domainAmount.getValue()));

    Transaction transaction = new Transaction();
    transaction.setDescription(description);
    transaction.setAmount(paypalAmount);

    List<Transaction> transactions = new ArrayList<>();
    transactions.add(transaction);

    Payer payer = new Payer();
    payer.setPaymentMethod("paypal"); // Método de pago fijo para este adaptador

    Payment payment = new Payment();
    payment.setIntent("sale"); // Intención fija para este adaptador
    payment.setPayer(payer);
    payment.setTransactions(transactions);

    RedirectUrls redirectUrls = new RedirectUrls();
    redirectUrls.setCancelUrl(cancelUrl);
    redirectUrls.setReturnUrl(successUrl);
    payment.setRedirectUrls(redirectUrls);

    try {
      Payment createdPayment = payment.create(apiContext);
      String approvalLink = null;
      for (Links links : createdPayment.getLinks()) {
        if ("approval_url".equals(links.getRel())) {
          approvalLink = links.getHref();
          break;
        }
      }
      if (approvalLink == null) {
        logger.error("No approval_url found in PayPal response for payment ID: {}", createdPayment.getId());
        throw new PaymentException("PayPal payment creation failed: No approval URL returned.");
      }
      // El ID del pago creado en PayPal es `createdPayment.getId()`
      return new PaymentProviderCreationResult(createdPayment.getId(), approvalLink);
    } catch (PayPalRESTException e) {
      logger.error("PayPalRESTException while creating payment: {}", e.getDetails() != null ? e.getDetails().getMessage() : e.getMessage(), e);
      throw new PaymentException("Error creating PayPal payment: " + (e.getDetails() != null ? e.getDetails().getMessage() : e.getMessage()), e);
    }
  }

//  @Override
//  public PaymentProviderCreationResult createPayment(com.hidrogreen.payment.gateway.platform.domain.valueobjects.Amount amount, String description, String cancelUrl, String successUrl) {
//    return null;
//  }

  @Override
  public PaymentProviderExecutionResult executePayment(String providerPaymentId, String payerId) {
    Payment payment = new Payment();
    payment.setId(providerPaymentId); // Este es el paymentId de PayPal

    PaymentExecution paymentExecution = new PaymentExecution();
    paymentExecution.setPayerId(payerId); // Este es el PayerID de PayPal

    try {
      Payment executedPayment = payment.execute(apiContext, paymentExecution);
      boolean success = "approved".equalsIgnoreCase(executedPayment.getState());
      if (!success) {
        logger.warn("PayPal payment execution not approved for payment ID: {}. State: {}", providerPaymentId, executedPayment.getState());
        // Podríamos querer más detalles del fallo si están disponibles
      }
      return new PaymentProviderExecutionResult(success, executedPayment.getId());
    } catch (PayPalRESTException e) {
      logger.error("PayPalRESTException while executing payment {}: {}", providerPaymentId, e.getDetails() != null ? e.getDetails().getMessage() : e.getMessage(), e);
      // Determinar si esto significa un fallo definitivo o si podría ser un error transitorio.
      // Por ahora, lo marcamos como no exitoso.
      return new PaymentProviderExecutionResult(false, providerPaymentId);
      // O podríamos lanzar una excepción para que el servicio de aplicación decida cómo manejarla:
      // throw new PaymentException("Error executing PayPal payment " + providerPaymentId + ": " + (e.getDetails() != null ? e.getDetails().getMessage() : e.getMessage()), e);
    }
  }
}