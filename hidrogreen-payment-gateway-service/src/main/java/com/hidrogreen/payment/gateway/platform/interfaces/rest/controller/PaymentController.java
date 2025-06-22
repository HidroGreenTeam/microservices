package com.hidrogreen.payment.gateway.platform.interfaces.rest.controller;

import com.hidrogreen.payment.gateway.platform.application.port.in.CompletePaymentUseCase;
import com.hidrogreen.payment.gateway.platform.application.port.in.InitiatePaymentUseCase;
import com.hidrogreen.payment.gateway.platform.domain.model.Payment;
import com.hidrogreen.payment.gateway.platform.interfaces.rest.dto.CreatePaymentRequest;
import com.hidrogreen.payment.gateway.platform.interfaces.rest.dto.CreatePaymentResponse;
import com.hidrogreen.payment.gateway.platform.interfaces.rest.mapper.PaymentRestMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
        import org.springframework.web.servlet.view.RedirectView;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/payments") 
@CrossOrigin("http://localhost:8081") 
public class PaymentController {

  private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

  private final InitiatePaymentUseCase initiatePaymentUseCase;
  private final CompletePaymentUseCase completePaymentUseCase;
  private final PaymentRestMapper paymentRestMapper;

  @Value("${app.url}/api/v1/payments/success") 
  private String successUrlBase;

  @Value("${app.url}/api/v1/payments/cancel")
  private String cancelUrlBase;


  public PaymentController(InitiatePaymentUseCase initiatePaymentUseCase,
                           CompletePaymentUseCase completePaymentUseCase,
                           PaymentRestMapper paymentRestMapper) {
    this.initiatePaymentUseCase = initiatePaymentUseCase;
    this.completePaymentUseCase = completePaymentUseCase;
    this.paymentRestMapper = paymentRestMapper;
  }


  @PostMapping("/initiate")
  public ResponseEntity<CreatePaymentResponse> initiatePayment(@RequestBody CreatePaymentRequest request) {
    try {
      InitiatePaymentUseCase.InitiatePaymentCommand command = paymentRestMapper.toInitiatePaymentCommand(request);
      String successUrl = successUrlBase; 
      String cancelUrl = cancelUrlBase;

      Payment initiatedPayment = initiatePaymentUseCase.initiatePayment(command, successUrl, cancelUrl);

      CreatePaymentResponse response = paymentRestMapper.toCreatePaymentResponse(initiatedPayment);

      return ResponseEntity.status(HttpStatus.CREATED).body(response);

    } catch (Exception e) {
      logger.error("Error initiating payment: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body(new CreatePaymentResponse(null, null, "Error: " + e.getMessage()));
    }
  }

  @GetMapping("/success")
  public void paymentSuccess(@RequestParam("paymentId") String providerPaymentId, // Este es el paymentId de PayPal
                             @RequestParam("PayerID") String payerId,
                             HttpServletResponse httpServletResponse) throws IOException {
    logger.info("Payment success callback received. ProviderPaymentId: {}, PayerID: {}", providerPaymentId, payerId);
    try {
      CompletePaymentUseCase.CompletePaymentCommand command = new CompletePaymentUseCase.CompletePaymentCommand(providerPaymentId, payerId);
      Payment completedPayment = completePaymentUseCase.completePayment(command);

      // Redirigir a una página de éxito del frontend
      // El frontend puede entonces consultar el estado del pago si es necesario.
      String frontendSuccessUrl = "http://localhost:8081/payment-success?internalPaymentId=" + completedPayment.getId().getValue().toString();
      httpServletResponse.sendRedirect(frontendSuccessUrl);

    } catch (Exception e) {
      logger.error("Error processing successful payment callback: {}", e.getMessage(), e);
      // Redirigir a una página de error del frontend
      String frontendErrorUrl = "http://localhost:8081/payment-error?error=" + e.getMessage();
      httpServletResponse.sendRedirect(frontendErrorUrl);
    }
  }

  @GetMapping("/cancel")
  public void paymentCancel(@RequestParam(value = "token", required = false) String token, // PayPal a veces envía 'token' que es el paymentId
                            @RequestParam(value = "paymentId", required = false) String providerPaymentIdParam,
                            HttpServletResponse httpServletResponse) throws IOException {

    String providerPaymentId = (token != null) ? token : providerPaymentIdParam;
    logger.info("Payment cancel callback received. ProviderPaymentId/Token: {}", providerPaymentId);

    if (providerPaymentId != null && !providerPaymentId.isEmpty()) {
      try {
        // Aunque el pago se canceló en PayPal, podríamos querer marcarlo como CANCELLED en nuestro sistema.
        completePaymentUseCase.markPaymentAsCancelledByProvider(providerPaymentId);
      } catch (Exception e) {
        // Loguear el error, pero la redirección a la página de cancelación del frontend debe ocurrir igualmente.
        logger.error("Error marking payment as cancelled for providerPaymentId {}: {}", providerPaymentId, e.getMessage(), e);
      }
    } else {
      logger.warn("Payment cancel callback received without a provider payment ID (token or paymentId).");
    }

    // Redirigir a una página de cancelación del frontend
    String frontendCancelUrl = "http://localhost:8081/payment-cancelled";
    httpServletResponse.sendRedirect(frontendCancelUrl);
  }
}
