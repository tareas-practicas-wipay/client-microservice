package com.wipay.client.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Datos reducidos de cliente que devuelve la API (solo el id)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleResponse {

    private String id;
}