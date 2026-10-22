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

@Service
@RequiredArgsConstructor
public class MerchantIntegrationServiceImpl implements MerchantIntegrationService {

    private final MerchantFeignClient merchantFeignClient;
    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory; // EXTRA
    private final RequestTokenProvider requestTokenProvider;

    @Override
    public boolean merchantExists(String merchantId) {
        String token = requestTokenProvider.currentToken();   // en el hilo de la petición

        return circuitBreakerFactory.create(ResilienceConfig.MERCHANT_SERVICE)
                .run(() -> callMerchantService(merchantId, token),
                        error -> {
                            throw unavailable(merchantId, error);
                        });
    }

    /// El 404 se resuelve aquí dentro: para el circuit breaker es una llamada correcta.
    private boolean callMerchantService(String merchantId, String token) {
        try {
            merchantFeignClient.findById(merchantId, true, token);
            return true;
        } catch (FeignException.NotFound ex) {
            return false;
        }
    }

    private ServiceUnavailableException unavailable(String merchantId, Throwable error) {
        String reason = error instanceof CallNotPermittedException
                ? "circuito abierto: merchant-microservice ha fallado varias veces seguidas"
                : "no ha respondido correctamente";
        return new ServiceUnavailableException(
                "No se ha podido consultar el merchant " + merchantId + " (" + reason + ")", error);
    }
}