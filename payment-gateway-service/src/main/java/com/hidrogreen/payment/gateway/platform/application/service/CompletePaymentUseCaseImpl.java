package com.hidrogreen.payment.gateway.platform.application.service;

import com.hidrogreen.payment.gateway.platform.application.port.in.CompletePaymentUseCase;
import com.hidrogreen.payment.gateway.platform.application.port.out.PaymentRepositoryPort;
import com.hidrogreen.payment.gateway.platform.domain.exception.PaymentException;
import com.hidrogreen.payment.gateway.platform.domain.model.Payment;
import com.hidrogreen.payment.gateway.platform.domain.port.PaymentProviderExecutionResult;
import com.hidrogreen.payment.gateway.platform.domain.port.PaymentProviderPort;
import com.hidrogreen.payment.gateway.platform.domain.valueobjects.PaymentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CompletePaymentUseCaseImpl implements CompletePaymentUseCase {

  private static final Logger logger = LoggerFactory.getLogger(CompletePaymentUseCaseImpl.class);

  private final PaymentRepositoryPort paymentRepository;
  private final PaymentProviderPort paymentProviderPort;

  public CompletePaymentUseCaseImpl(PaymentRepositoryPort paymentRepository,
                                    PaymentProviderPort paymentProviderPort) {
    this.paymentRepository = paymentRepository;
    this.paymentProviderPort = paymentProviderPort;
  }

  @Override
  public Payment completePayment(CompletePaymentCommand command) {
    // 1. Encontrar el pago en nuestro sistema usando el ID del proveedor
    Optional<Payment> paymentOptional = paymentRepository.findByProviderPaymentId(command.getProviderPaymentId());

    if (paymentOptional.isEmpty()) {
      logger.error("Payment not found for providerPaymentId: {}", command.getProviderPaymentId());
      throw new PaymentException("Payment not found for provider ID: " + command.getProviderPaymentId());
    }

    Payment payment = paymentOptional.get();
    logger.info("Found payment {} for provider ID {}", payment.getId().getValue(), command.getProviderPaymentId());

    // Verificar si el pago ya fue procesado para evitar doble procesamiento
    if (payment.getStatus() == PaymentStatus.APPROVED || payment.getStatus() == PaymentStatus.FAILED) {
      logger.warn("Payment {} with provider ID {} already in terminal state: {}",
              payment.getId().getValue(), command.getProviderPaymentId(), payment.getStatus());
      return payment; // Devolver el estado actual, no intentar procesar de nuevo
    }

    try {
      // 2. Ejecutar/Confirmar el pago con el proveedor
      PaymentProviderExecutionResult executionResult = paymentProviderPort.executePayment(
              command.getProviderPaymentId(),
              command.getPayerId()
      );

      // 3. Actualizar el estado de nuestro Payment basado en el resultado del proveedor
      if (executionResult.isSuccess()) {
        payment.markAsApproved();
        logger.info("Payment {} (Provider ID: {}) successfully approved.",
                payment.getId().getValue(), command.getProviderPaymentId());
      } else {
        payment.markAsFailed("Provider execution indicated failure for payment " + command.getProviderPaymentId());
        logger.warn("Payment {} (Provider ID: {}) failed at provider execution.",
                payment.getId().getValue(), command.getProviderPaymentId());
      }

      // 4. Persistir el estado final del pago
      return paymentRepository.save(payment);

    } catch (Exception e) {
      // Si hay un error durante la ejecución con el proveedor (ej. una excepción de red)
      logger.error("Error executing payment with provider for payment {} (Provider ID: {}): {}",
              payment.getId().getValue(), command.getProviderPaymentId(), e.getMessage(), e);
      // Se podria querer un estado intermedio como PENDING_CONFIRMATION o simplemente FAILED
      // Aquí se marca como FAILED.
      payment.markAsFailed("Provider execution failed with exception: " + e.getMessage());
      paymentRepository.save(payment);
      throw new PaymentException("Payment completion failed for payment " + payment.getId().getValue(), e);
    }
  }

  @Override
  public void markPaymentAsCancelledByProvider(String providerPaymentId) {
    if (providerPaymentId == null || providerPaymentId.trim().isEmpty()) {
      logger.warn("Attempted to mark payment as cancelled with null or empty providerPaymentId.");
      return;
    }

    Optional<Payment> paymentOptional = paymentRepository.findByProviderPaymentId(providerPaymentId);

    if (paymentOptional.isEmpty()) {
      // Si no se encuentra el pago, podría ser un token que no resultó en un pago creado,
      // o un pago que no guardamos. Logueamos y continuamos, ya que el usuario ya canceló.
      logger.warn("Payment not found for providerPaymentId: {} during cancellation. No state updated.", providerPaymentId);
      return;
    }

    Payment payment = paymentOptional.get();
    logger.info("Marking payment {} (Provider ID: {}) as cancelled due to provider callback.",
            payment.getId().getValue(), providerPaymentId);

    // Solo cambiar estado si es apropiado (e.g., no si ya está APPROVED o FAILED por otra razón)
    if (payment.getStatus() == PaymentStatus.PENDING_APPROVAL || payment.getStatus() == PaymentStatus.CREATED) {
      payment.markAsCancelled();
      paymentRepository.save(payment);
      logger.info("Payment {} (Provider ID: {}) marked as CANCELLED.",
              payment.getId().getValue(), providerPaymentId);
    } else {
      logger.warn("Payment {} (Provider ID: {}) not marked as CANCELLED due to current state: {}",
              payment.getId().getValue(), providerPaymentId, payment.getStatus());
    }
  }
}
