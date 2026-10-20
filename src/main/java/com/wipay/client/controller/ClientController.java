package com.wipay.client.controller;

import com.wipay.client.mappers.ClientMapper;
import com.wipay.client.model.ClientModel;
import com.wipay.client.model.request.ClientRequest;
import com.wipay.client.model.request.NameSearchRequest;
import com.wipay.client.model.response.ClientResponse;
import com.wipay.client.model.response.SimpleResponse;
import com.wipay.client.service.ClientService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
@RequestMapping("/clients")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "Gestión de clientes") //Documentación para Swagger
public class ClientController {

    private final ClientService clientService;
    private final ClientMapper clientMapper;

    @PostMapping
    @Operation(summary = "Crear un cliente")
    @ResponseStatus(HttpStatus.CREATED)
    public ClientResponse create(@Valid @RequestBody ClientRequest request) {
        ClientModel created = clientService.create(clientMapper.requestToModel(request));
        return clientMapper.modelToResponse(created);
    }

    /** Debe devolver un ClientResponse o un SimpleResponse, dependiendo de si incluye el parámetro 'GET /clients/{id}?simpleOutput=true'
     *  -SOLUCIÓN-> Devolver un objeto Object que se adapte a los dos casos de retorno
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener un cliente por su id", description = "Con simpleOutput=true devuelve solo el id")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(     // Como devuelve un Object, Swagger no sabe si está bien o no.
            oneOf = {ClientResponse.class, SimpleResponse.class})))             // Con esto le decimos que si Object es una de las dos clases, devuelva 200 (OK)
    public Object findById(@PathVariable String id,
                           @RequestParam(defaultValue = "false") boolean simpleOutput){
        ClientModel client = clientService.findById(id);

        return (simpleOutput) // Si el parámetro es true
                ? clientMapper.modelToSimpleResponse(client) // Devuelve respuesta simple
                : clientMapper.modelToResponse(client);      // Si no devuelve respuesta completa

    }

    @GetMapping("/search/by-name")
    @Operation(summary = "Buscar clientes por nombre",
                description = "Coincidencia parcial sin distinguir mayúsculas ('pay' encuentra 'Wipay')")
    public List<ClientResponse> findByName(@Valid @ParameterObject NameSearchRequest name) {
        return clientMapper.modelsToResponses(clientService.findByName(name.getName()));
    }
}