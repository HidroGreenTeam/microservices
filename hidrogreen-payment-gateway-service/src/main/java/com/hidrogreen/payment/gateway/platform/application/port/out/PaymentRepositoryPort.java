package com.hidrogreen.payment.gateway.platform.application.port.out;

import com.hidrogreen.payment.gateway.platform.domain.model.Payment;
import com.hidrogreen.payment.gateway.platform.domain.valueobjects.PaymentId;

import java.util.Optional;

public interface PaymentRepositoryPort {

  /**
   * @param payment El objeto Payment a guardar.
   * @return El objeto Payment guardado (puede ser el mismo objeto o una nueva instancia
   *         representando el estado persistido, dependiendo de la implementación).
   */
  Payment save(Payment payment);

  /**
   * @param id El PaymentId del pago a buscar.
   * @return Un Optional conteniendo el Payment si se encuentra, o un Optional vacío si no.
   */
  Optional<Payment> findById(PaymentId id);

  /**
   * @param providerPaymentId El ID del pago asignado por el proveedor (e.g., PayPal).
   * @return Un Optional conteniendo el Payment si se encuentra, o un Optional vacío si no.
   */
  Optional<Payment> findByProviderPaymentId(String providerPaymentId);

  // List<Payment> findByStatus(Payment.PaymentStatus status);
  // void deleteById(PaymentId id);
}