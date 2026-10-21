package com.wipay.client.service.impl;

import com.wipay.client.exception.ServiceUnavailableException;
import com.wipay.client.integration.MerchantFeignClient;
import com.wipay.client.service.MerchantIntegrationService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MerchantIntegrationServiceImpl implements MerchantIntegrationService {

    private final MerchantFeignClient merchantFeignClient;

    @Override
    public boolean merchantExists(String merchantId) {
        try {
            merchantFeignClient.findById(merchantId, true);
            return true;
        } catch (FeignException.NotFound ex) {
            return false;
        } catch (FeignException ex) {
            throw new ServiceUnavailableException(
                    "No se ha podido consultar merchant-microservice para el merchant " + merchantId, ex);
        }
    }
}