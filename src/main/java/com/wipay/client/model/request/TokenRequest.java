package com.wipay.client.model.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Datos de la persona simulada para la que se genera un token.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotNull(message = "La edad es obligatoria")
    @Min(value = 0, message = "La edad no puede ser negativa")
    @Max(value = 125, message = "La edad no es válida")
    private Integer age;
}