package com.wipay.client.service.impl;

import com.wipay.client.config.ResilienceConfig;
import com.wipay.client.exception.ServiceUnavailableException;
import com.wipay.client.integration.MerchantFeignClient;
import com.wipay.client.integration.RequestTokenProvider;
import com.wipay.client.service.MerchantIntegrationService;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class MerchantIntegrationServiceImpl implements MerchantIntegrationService {

    private final MerchantFeignClient merchantFeignClient;
    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory; // EXTRA
    private final RequestTokenProvider requestTokenProvider;

    @Override
    public boolean merchantExists(String merchantId) {
        String token = requestTokenProvider.currentToken();   // en el hilo de la petición

        return callMerchantService(() -> {
            try {
                merchantFeignClient.findById(merchantId, true, token);
                return true;
            } catch (FeignException.NotFound ex) {
                return false;
            }
        }, "No se ha podido consultar el merchant " + merchantId);
    }

    @Override
    public boolean clientHasMerchants(String clientId) {
        String token = requestTokenProvider.currentToken();   // en el hilo de la petición

        return callMerchantService(() -> {
            try {
                return !merchantFeignClient.findByClientId(clientId, token).isEmpty();
            } catch (FeignException.NotFound ex) {
                return false;   // merchant-microservice no encuentra el cliente: no puede tener merchants
            }
        }, "No se han podido consultar los merchants del cliente " + clientId);
    }

    /// Los 404 se resuelven dentro de cada llamada: para el circuit breaker son llamadas correctas.
    private boolean callMerchantService(Supplier<Boolean> call, String errorMessage) {
        return circuitBreakerFactory.create(ResilienceConfig.MERCHANT_SERVICE)
                .run(call, error -> {
                    throw unavailable(errorMessage, error);
                });
    }

    private ServiceUnavailableException unavailable(String errorMessage, Throwable error) {
        String reason = error instanceof CallNotPermittedException
                ? "circuito abierto: merchant-microservice ha fallado varias veces seguidas"
                : "no ha respondido correctamente";
        return new ServiceUnavailableException(errorMessage + " (" + reason + ")", error);
    }
}
