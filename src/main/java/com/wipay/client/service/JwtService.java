package com.wipay.client.service;

import com.wipay.client.model.TokenUser;

public interface JwtService {

    String createToken(String name, int age);

    TokenUser parseToken(String token);
}