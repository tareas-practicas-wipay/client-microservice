package com.wipay.client.service;

import com.wipay.client.model.ClientModel;

import java.util.List;

public interface ClientService {
    ClientModel create(ClientModel client);

    ClientModel findById(String id);

    List<ClientModel> findByName(String name);

    ClientModel findByEmail(String email);

    ClientModel update(String id, ClientModel changes);
}
