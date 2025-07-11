package com.hidrogreen.payment.gateway.platform.interfaces.rest.mapper;

import com.hidrogreen.payment.gateway.platform.application.port.in.InitiatePaymentUseCase;
import com.hidrogreen.payment.gateway.platform.domain.model.Payment;
import com.hidrogreen.payment.gateway.platform.interfaces.rest.dto.CreatePaymentRequest;
import com.hidrogreen.payment.gateway.platform.interfaces.rest.dto.CreatePaymentResponse;
import com.hidrogreen.payment.gateway.platform.interfaces.rest.dto.PaymentCompletionResponse;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

public interface PaymentRestMapper {

  PaymentRestMapper INSTANCE = Mappers.getMapper(PaymentRestMapper.class);

  @Mapping(target = "amountValue", source = "amount")
    // currency y orderDescription se mapean automáticamente por nombre
  InitiatePaymentUseCase.InitiatePaymentCommand toInitiatePaymentCommand(CreatePaymentRequest request);

  @Mapping(source = "id.value", target = "paymentId") // Mapea Payment.PaymentId.value a paymentId
  @Mapping(source = "status", target = "status") // Mapea Payment.PaymentStatus (enum) a String
  CreatePaymentResponse toCreatePaymentResponse(Payment payment);

  // Para el caso de completar pago, la respuesta es más un mensaje de estado
  default PaymentCompletionResponse toPaymentCompletionResponse(Payment payment, String message) {
    return new PaymentCompletionResponse(
            payment.getId().getValue().toString(),
            payment.getStatus().toString(),
            message
    );
  }

  // Si tuvieras un DTO de respuesta más rico para el "complete"
    /*
    @Mapping(source = "id.value", target = "paymentId")
    @Mapping(source = "status", target = "status")
    PaymentCompletionResponse toPaymentCompletionResponse(Payment payment);
    */
}