package com.wipay.client.config;

import javax.crypto.SecretKey;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    /// Clave HMAC-SHA256 para firmar y verificar los tokens. Se construye una sola vez.
    @Bean
    public SecretKey jwtSigningKey(JwtProperties properties) {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(properties.secret()));
    }
}