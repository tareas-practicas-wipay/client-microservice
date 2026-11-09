package com.wipay.client.service.impl;

import com.wipay.client.exception.ConflictException;
import com.wipay.client.exception.NotFoundException;
import com.wipay.client.mappers.ClientMapper;
import com.wipay.client.model.ClientModel;
import com.wipay.client.model.EntityStatus;
import com.wipay.client.model.entity.ClientEntity;
import com.wipay.client.repository.ClientRepository;
import com.wipay.client.service.ClientService;
import com.wipay.client.service.MerchantIntegrationService;
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

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final MerchantIntegrationService merchantIntegrationService;

    @Override
    public ClientModel create(ClientModel client) {
        if (clientRepository.findByEmail(client.getEmail()).isPresent()) {
            throw new ConflictException("Ya existe un cliente con el email " + client.getEmail());
        }

        client.setId(UUID.randomUUID().toString());
        client.setStatus(EntityStatus.ACTIVE);
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
    public List<ClientModel> findAll() {
        return clientMapper.entitiesToModels(clientRepository.findAll());
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

    @Override
    public ClientModel update(String id, ClientModel changes) {
        ClientModel existing = findById(id);

        clientRepository.findByEmail(changes.getEmail())
                .filter(other -> !other.getId().equals(id))
                .ifPresent(other -> {
                    throw new ConflictException("Ya existe otro cliente con el email " + changes.getEmail());
                });

        clientMapper.updateModel(changes, existing);

        ClientEntity saved = clientRepository.save(clientMapper.modelToEntity(existing));
        return clientMapper.entityToModel(saved);
    }

    /// Borrado lógico: el cliente se queda en la tabla con status INACTIVE.
    /// No se permite si aún tiene merchants activos, para no dejarlos sin cliente.
    @Override
    public void delete(String id) {
        ClientModel existing = findById(id);

        if (merchantIntegrationService.clientHasMerchants(id)) {
            throw new ConflictException("No se puede borrar el cliente " + id
                    + " porque tiene merchants activos. Hay que borrarlos antes");
        }

        existing.setStatus(EntityStatus.INACTIVE);
        clientRepository.save(clientMapper.modelToEntity(existing));
    }
}
