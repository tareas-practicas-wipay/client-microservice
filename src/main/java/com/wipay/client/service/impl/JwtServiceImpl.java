package com.wipay.client.service.impl;

import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import com.wipay.client.config.JwtProperties;
import com.wipay.client.exception.InvalidTokenException;
import com.wipay.client.model.TokenUser;
import com.wipay.client.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private static final String CLAIM_NAME = "name";
    private static final String CLAIM_AGE = "age";

    private final SecretKey jwtSigningKey;
    private final JwtProperties jwtProperties;

    @Override
    public String createToken(String name, int age) {
        Instant now = Instant.now();

        return Jwts.builder()
                .claim(CLAIM_NAME, name)
                .claim(CLAIM_AGE, age)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(jwtProperties.expiration())))
                .signWith(jwtSigningKey)
                .compact();
    }

    @Override
    public TokenUser parseToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(jwtSigningKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String name = claims.get(CLAIM_NAME, String.class);
            Integer age = claims.get(CLAIM_AGE, Integer.class);
            if (name == null || age == null) {
                throw new InvalidTokenException("El token no contiene el nombre o la edad");
            }
            return new TokenUser(name, age);

        } catch (JwtException | IllegalArgumentException ex) {
            throw new InvalidTokenException("El token no es válido o ha caducado", ex);
        }
    }
}