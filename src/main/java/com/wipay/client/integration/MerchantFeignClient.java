package com.wipay.client.integration;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Cliente HTTP de merchant-microservice. Feign genera la implementación al arrancar.
 */
@FeignClient(name = "merchant-service", url = "${merchant.service.url}")
public interface MerchantFeignClient {

    @GetMapping("/merchants/{id}")
    MerchantSimpleResponse findById(@PathVariable("id") String id,
                                    @RequestParam("simpleOutput") boolean simpleOutput,
                                    @RequestParam("token") String token);
}