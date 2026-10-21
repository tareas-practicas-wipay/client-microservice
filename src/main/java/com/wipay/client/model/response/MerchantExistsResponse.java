package com.wipay.client.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Indica si un merchant existe en merchant-microservice.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantExistsResponse {

    private String merchantId;
    private boolean exists;
}