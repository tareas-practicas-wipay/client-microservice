package com.wipay.client.controller;

import com.wipay.client.model.response.MerchantExistsResponse;
import com.wipay.client.service.MerchantIntegrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/merchants")
@RequiredArgsConstructor
@Tag(name = "Merchants", description = "Consultas a merchant-microservice mediante Feign")
public class MerchantCheckController {

    private final MerchantIntegrationService merchantIntegrationService;

    @GetMapping("/{merchantId}/exists")
    @Operation(summary = "Comprobar si existe un merchant",
            description = "Consulta findById de merchant-microservice. Devuelve 503 si no está disponible")
    public MerchantExistsResponse merchantExists(@PathVariable String merchantId) {
        return MerchantExistsResponse.builder()
                .merchantId(merchantId)
                .exists(merchantIntegrationService.merchantExists(merchantId))
                .build();
    }
}