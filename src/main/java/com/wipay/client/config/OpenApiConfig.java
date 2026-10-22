package com.wipay.client.config;

import com.wipay.client.controller.TokenController;
import com.wipay.client.interceptor.JwtInterceptor;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.QueryParameter;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Client Microservice", version = "1.0.0", description = "Client Microservice API"))
public class OpenApiConfig {

    @Bean
    public OperationCustomizer tokenParameterCustomizer() {
        return (operation, handlerMethod) -> {
            if (handlerMethod.getBeanType() == TokenController.class) {
                return operation; // /tokens es público: no necesita token
            }
            return operation.addParametersItem(new QueryParameter()
                    .name(JwtInterceptor.TOKEN_PARAM)
                    .required(true)
                    .description("JWT con el nombre y la edad de quien hace la petición (generarlo con POST /tokens)")
                    .schema(new StringSchema()));
        };
    }
}
