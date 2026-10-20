package com.wipay.client.service.impl;

import com.wipay.client.exception.ConflictException;
import com.wipay.client.exception.NotFoundException;
import com.wipay.client.mappers.ClientMapper;
import com.wipay.client.model.ClientModel;
import com.wipay.client.model.entity.ClientEntity;
import com.wipay.client.repository.ClientRepository;
import com.wipay.client.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * ---------------------------------------------------------
 *                       (create)
 * [ SERVICE ] === mapper(Model->Entity) ==> [ REPOSITORY ]
 * ---------------------------------------------------------
 * [ SERVICE ] <== mapper(Model<-Entity) === [ REPOSITORY ]
 *       (create/findById/findByEmail/findByName)
 * ---------------------------------------------------------
 */

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private static final String STATUS_ACTIVE = "ACTIVE";

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @Override
    public ClientModel create(ClientModel client) {
        if (clientRepository.findByEmail(client.getEmail()).isPresent()) {
            throw new ConflictException("Ya existe un cliente con el email " + client.getEmail());
        }

        client.setId(UUID.randomUUID().toString());
        client.setStatus(STATUS_ACTIVE);
        client.setCreatedDate(Instant.now());

        ClientEntity saved = clientRepository.save(clientMapper.modelToEntity(client));
        return clientMapper.entityToModel(saved);
    }

    @Override
    public ClientModel findById(String id) {
        return clientRepository.findById(id)
                .map(clientMapper::entityToModel)
                .orElseThrow(() -> new NotFoundException("No existe ningún cliente con id " + id));
    }

    @Override
    public List<ClientModel> findByName(String name) {
        return clientMapper.entitiesToModels(clientRepository.findByName(name));
    }

    @Override
    public ClientModel findByEmail(String email) {
        return clientRepository.findByEmail(email)
                .map(clientMapper::entityToModel)
                .orElseThrow(() -> new NotFoundException("No existe ningún cliente con email " + email));
    }

}
