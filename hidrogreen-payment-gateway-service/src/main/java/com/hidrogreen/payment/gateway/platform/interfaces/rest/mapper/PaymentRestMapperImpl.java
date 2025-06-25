package com.hidrogreen.payment.gateway.platform.interfaces.rest.mapper;

import com.hidrogreen.payment.gateway.platform.application.port.in.InitiatePaymentUseCase.InitiatePaymentCommand;
import com.hidrogreen.payment.gateway.platform.domain.model.Payment;
import com.hidrogreen.payment.gateway.platform.interfaces.rest.dto.CreatePaymentRequest;
import com.hidrogreen.payment.gateway.platform.interfaces.rest.dto.CreatePaymentResponse;
import com.hidrogreen.payment.gateway.platform.interfaces.rest.dto.PaymentCompletionResponse;
import org.springframework.stereotype.Component; 

@Component 
public class PaymentRestMapperImpl implements PaymentRestMapper {

  @Override
  public InitiatePaymentCommand toInitiatePaymentCommand(CreatePaymentRequest request) {
    if (request == null) {
      return null;
    }
    return new InitiatePaymentCommand(
            request.getAmount(), // Esto es amountValue en el Command
            request.getCurrency(),
            request.getOrderDescription()
    );
  }

  @Override
  public CreatePaymentResponse toCreatePaymentResponse(Payment payment) {
    if (payment == null) {
      return null;
    }
    return new CreatePaymentResponse(
            payment.getId().getValue().toString(),
            payment.getApprovalUrl(),
            payment.getStatus().toString()
    );
  }

  @Override
  public PaymentCompletionResponse toPaymentCompletionResponse(Payment payment, String message) {
    // El método default en la interfaz ya funciona, pero si quieres sobreescribirlo o
    // si quitas 'default' de la interfaz, necesitas implementarlo aquí.
    // Por ahora, asumimos que el default de la interfaz es suficiente si no se sobreescribe.
    // Si la interfaz NO tuviera el método default:
    if (payment == null) {
      // Podrías devolver null o un objeto de respuesta de error genérico
      return new PaymentCompletionResponse(null, "ERROR", message != null ? message : "Payment object was null");
    }
    return new PaymentCompletionResponse(
            payment.getId().getValue().toString(),
            payment.getStatus().toString(),
            message
    );
  }
}
