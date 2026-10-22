package com.wipay.client.config;

import java.time.Duration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Propiedades de los tokens JWT, leídas del prefijo {@code jwt}.
 */
@ConfigurationProperties(prefix = "jwt")
@Validated
public record JwtProperties(
        @NotBlank String secret,
        @NotNull Duration expiration
) {
}