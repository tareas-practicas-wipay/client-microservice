package com.wipay.client.interceptor;

import com.wipay.client.exception.InvalidTokenException;
import com.wipay.client.exception.UnderageException;
import com.wipay.client.model.TokenUser;
import com.wipay.client.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Lee el JWT del parámetro "token" y solo deja llegar la petición al controlador si la persona es mayor de edad.
 */
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    public static final String TOKEN_PARAM = "token";
    public static final String TOKEN_USER_ATTRIBUTE = "tokenUser";
    private static final int MINIMUM_AGE = 18;

    private final JwtService jwtService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getParameter(TOKEN_PARAM);
        if (token == null || token.isBlank()) {
            throw new InvalidTokenException("Falta el parámetro '" + TOKEN_PARAM + "' con el JWT");
        }

        TokenUser user = jwtService.parseToken(token);
        if (user.getAge() < MINIMUM_AGE) {
            throw new UnderageException(user.getName() + " tiene " + user.getAge()
                    + " años. El acceso está restringido a mayores de " + MINIMUM_AGE);
        }

        request.setAttribute(TOKEN_USER_ATTRIBUTE, user);
        return true;
    }
}