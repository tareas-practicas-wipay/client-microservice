package com.wipay.client.integration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Id de un merchant de merchant-microservice.
 * Sirve para GET /merchants/{id}?simpleOutput=true y para cada elemento de GET /merchants/search/by-client,
 * que trae más campos: solo se lee el id y el resto se ignora.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MerchantSimpleResponse {

    private String id;
}
