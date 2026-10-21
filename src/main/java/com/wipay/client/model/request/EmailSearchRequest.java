package com.wipay.client.model.request;

import com.wipay.client.model.ValidationPatterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Parámetros de la búsqueda de un cliente por email.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailSearchRequest {

    @NotBlank(message = "El email es obligatorio")
    @Pattern(regexp = ValidationPatterns.EMAIL_REGEX,
            message = "El email no tiene un formato válido")
    private String email;
}