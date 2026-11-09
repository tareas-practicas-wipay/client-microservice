package com.wipay.client.integration;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Cliente HTTP de merchant-microservice. Feign genera la implementación al arrancar.
 */
@FeignClient(name = "merchant-service", url = "${merchant.service.url}")
public interface MerchantFeignClient {

    @GetMapping("/merchants/{id}")
    MerchantSimpleResponse findById(@PathVariable("id") String id,
                                    @RequestParam("simpleOutput") boolean simpleOutput,
                                    @RequestParam("token") String token);

    /** Merchants activos de un cliente. Solo se lee el id de cada uno. */
    @GetMapping("/merchants/search/by-client")
    List<MerchantSimpleResponse> findByClientId(@RequestParam("clientId") String clientId,
                                                @RequestParam("token") String token);
}
