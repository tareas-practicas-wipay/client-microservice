package com.wipay.client.integration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Respuesta de GET /merchants/{id}?simpleOutput=true de merchant-microservice.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MerchantSimpleResponse {

    private String id;
}