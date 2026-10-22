package com.wipay.client.controller;

import com.wipay.client.model.request.TokenRequest;
import com.wipay.client.model.response.TokenResponse;
import com.wipay.client.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tokens")
@RequiredArgsConstructor
@Tag(name = "Tokens", description = "Generación de JWT de prueba con nombre y edad")
public class TokenController {

    private final JwtService jwtService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Generar un JWT de prueba",
            description = "Simula la persona que envía las peticiones. **Solo para pruebas**")
    public TokenResponse create(@Valid @RequestBody TokenRequest request) {
        return new TokenResponse(jwtService.createToken(request.getName(), request.getAge()));
    }
}