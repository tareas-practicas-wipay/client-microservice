package com.wipay.client.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Datos completos de cliente que devuelve la API
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponse {
    private String id;
    private String name;
    private String surname;
    private String cifNifNie;
    private String phone;
    private String email;
    private String status;
    private Instant createdDate;
}
