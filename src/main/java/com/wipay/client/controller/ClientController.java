package com.wipay.client.controller;

import com.wipay.client.mappers.ClientMapper;
import com.wipay.client.model.ClientModel;
import com.wipay.client.model.request.ClientRequest;
import com.wipay.client.model.response.ClientResponse;
import com.wipay.client.service.ClientService;
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

/**
 * ---------------------------------------------------------
 *                         (create)
 * [ CONTROLLER ] === mapper(Request->Model) ==> [ SERVICE ]
 * ---------------------------------------------------------
 * [ CONTROLLER ] <== mapper(Request<-Model) === [ SERVICE ]
 *                         (create)
 * ---------------------------------------------------------
 */
@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "Gestión de clientes") //Documentación para Swagger
public class ClientController {

    private final ClientService clientService;
    private final ClientMapper clientMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear un cliente")
    public ClientResponse create(@Valid @RequestBody ClientRequest request) {
        ClientModel created = clientService.create(clientMapper.requestToModel(request));
        return clientMapper.modelToResponse(created);
    }
}
