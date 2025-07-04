package com.hidrogreen.payment.gateway.platform.application.service;

import com.hidrogreen.payment.gateway.platform.application.port.in.InitiatePaymentUseCase;
import com.hidrogreen.payment.gateway.platform.application.port.out.PaymentRepositoryPort;
import com.hidrogreen.payment.gateway.platform.domain.exception.PaymentException;
import com.hidrogreen.payment.gateway.platform.domain.model.Payment;
import com.hidrogreen.payment.gateway.platform.domain.port.PaymentProviderCreationResult;
import com.hidrogreen.payment.gateway.platform.domain.port.PaymentProviderPort;
import com.hidrogreen.payment.gateway.platform.domain.valueobjects.Amount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service 
public class InitiatePaymentUseCaseImpl implements InitiatePaymentUseCase {

  private static final Logger logger = LoggerFactory.getLogger(InitiatePaymentUseCaseImpl.class);

  private final PaymentRepositoryPort paymentRepository;
  private final PaymentProviderPort paymentProviderPort;

  public InitiatePaymentUseCaseImpl(PaymentRepositoryPort paymentRepository,
                                    PaymentProviderPort paymentProviderPort) {
    this.paymentRepository = paymentRepository;
    this.paymentProviderPort = paymentProviderPort;
  }

  @Override
  public Payment initiatePayment(InitiatePaymentCommand command, String successUrl, String cancelUrl) {
    // 1. Validar el comando (aunque ya podría estar validado en el Command mismo o en el DTO)
    // Por simplicidad, asumimos que los datos básicos son correctos.

    // 2. Crear el objeto Amount del dominio
    Amount domainAmount = new Amount(command.getAmountValue(), command.getCurrency());

    // 3. Crear la entidad Payment en nuestro sistema
    // El estado inicial será PENDING_APPROVAL o similar
    Payment payment = new Payment(command.getOrderDescription(), domainAmount);

    // 4. Persistir el pago con su estado inicial
    // Esto es importante para tener un registro antes de llamar al proveedor externo.
    // Si la llamada al proveedor falla, ya tenemos un rastro.
    Payment savedPayment = paymentRepository.save(payment);
    logger.info("Payment {} initiated with status {}. Persisted.",
            savedPayment.getId().getValue(), savedPayment.getStatus());

    try {
      // 5. Interactuar con el proveedor de pagos para crear el pago externamente
      PaymentProviderCreationResult providerResult = paymentProviderPort.createPayment(
              savedPayment.getAmount(),          // Usar datos del objeto Payment ya creado
              savedPayment.getOrderDescription(),
              cancelUrl,                     // URL de cancelación para el proveedor
              successUrl                     // URL de éxito para el proveedor
      );

      // 6. Actualizar nuestro Payment con la información del proveedor
      savedPayment.markAsCreatedByProvider(providerResult.getProviderPaymentId(), providerResult.getApprovalUrl());

      // 7. Persistir los cambios (ID del proveedor, URL de aprobación)
      Payment updatedPayment = paymentRepository.save(savedPayment);
      logger.info("Payment {} updated with provider ID {} and approval URL. Status: {}.",
              updatedPayment.getId().getValue(),
              updatedPayment.getPaymentProviderId(),
              updatedPayment.getStatus());

      return updatedPayment;

    } catch (Exception e) {
      // Si la creación en el proveedor falla, marcamos nuestro pago como fallido
      // o lo dejamos en PENDING_APPROVAL para un reintento, dependiendo de la política.
      // Aquí, lo marcaremos como FAILED.
      logger.error("Failed to create payment with provider for internal payment ID {}: {}",
              savedPayment.getId().getValue(), e.getMessage(), e);
      savedPayment.markAsFailed("Provider payment creation failed: " + e.getMessage());
      paymentRepository.save(savedPayment); // Guardar el estado fallido
      // Relanzar una excepción específica de la aplicación o del dominio
      throw new PaymentException("Payment initiation failed at provider step for payment " + savedPayment.getId().getValue(), e);
    }
  }
}
