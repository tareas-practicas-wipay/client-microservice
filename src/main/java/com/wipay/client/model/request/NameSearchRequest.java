package com.wipay.client.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Búsqueda por nombre (coincidencia parcial, sin distinguir mayúsculas)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NameSearchRequest {
    @NotBlank(message = "El texto a buscar es obligatorio")
    private String name;
}
