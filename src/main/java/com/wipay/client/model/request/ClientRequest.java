package com.wipay.client.model.request;

import com.wipay.client.model.ValidationPatterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "Los apellidos son obligatorios")
    private String surname;

    @NotBlank(message = "El CIF/NIF/NIE es obligatorio")
    @Pattern(regexp = ValidationPatterns.CIF_NIF_NIE_REGEX,
            message = "El CIF/NIF/NIE no tiene un formato válido")
    private String cifNifNie;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = ValidationPatterns.PHONE_REGEX,
            message = "El teléfono no tiene un formato válido")
    private String phone;

    @NotBlank(message = "El email es obligatorio")
    @Pattern(regexp = ValidationPatterns.EMAIL_REGEX,
            message = "El email no tiene un formato válido")
    private String email;

}
