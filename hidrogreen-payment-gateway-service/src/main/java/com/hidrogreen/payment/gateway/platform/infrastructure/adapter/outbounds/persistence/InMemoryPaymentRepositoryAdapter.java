package com.hidrogreen.payment.gateway.platform.infrastructure.adapter.outbounds.persistence;

import com.hidrogreen.payment.gateway.platform.application.port.out.PaymentRepositoryPort;
import com.hidrogreen.payment.gateway.platform.domain.model.Payment;
import com.hidrogreen.payment.gateway.platform.domain.valueobjects.PaymentId;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryPaymentRepositoryAdapter implements PaymentRepositoryPort {

  private final Map<PaymentId, Payment> paymentsById = new ConcurrentHashMap<>();
  private final Map<String, PaymentId> paymentsByProviderId = new ConcurrentHashMap<>();

  @Override
  public Payment save(Payment payment) {
    // En una implementación real, aquí se clonaría el objeto para evitar efectos secundarios
    // si el objeto payment es modificado después de guardarlo.
    // Para simplificar, aquí guardamos la referencia directamente.
    paymentsById.put(payment.getId(), payment);
    if (payment.getPaymentProviderId() != null) {
      paymentsByProviderId.put(payment.getPaymentProviderId(), payment.getId());
    }
    return payment; // Devolver el mismo objeto o una copia
  }

  @Override
  public Optional<Payment> findById(PaymentId id) {
    return Optional.ofNullable(paymentsById.get(id));
    // .map(this::clonePayment); // Si quisiéramos devolver una copia
  }

  @Override
  public Optional<Payment> findByProviderPaymentId(String providerPaymentId) {
    return Optional.ofNullable(paymentsByProviderId.get(providerPaymentId))
            .flatMap(this::findById);
    // .map(this::clonePayment); // Si quisiéramos devolver una copia
  }

  // Método de utilidad para clonar (si fuera necesario para evitar mutaciones)
  // Para este ejemplo simple, no es estrictamente necesario, pero es buena práctica en sistemas complejos.
    /*
    private Payment clonePayment(Payment original) {
        return new Payment(
            original.getId(),
            original.getOrderDescription(),
            original.getAmount(), // Amount es un VO, inmutable
            original.getStatus(),
            original.getPaymentProviderId(),
            original.getApprovalUrl()
        );
    }
    */

  // Para fines de prueba o depuración, podríamos querer limpiar el repositorio
  public void clear() {
    paymentsById.clear();
    paymentsByProviderId.clear();
  }
}
