package com.vault.secure_vault.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI (Swagger) configuration for Secure Vault.
 * <p>
 * Defines API metadata and configures JWT Bearer authentication for all secured endpoints.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Name of the security scheme used in OpenAPI documentation.
     */
    public static final String SECURITY_SCHEME_NAME = "bearerAuth";

    /**
     * Configures OpenAPI metadata and security scheme.
     *
     * @return configured {@link OpenAPI} instance
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Secure Vault API")
                        .description("Secure Vault backend APIs for authentication, file storage, and versioning")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Secure Vault Team")
                                .email("support@securevault.local"))
                )
                .addSecurityItem(
                        new SecurityRequirement().addList(SECURITY_SCHEME_NAME)
                )
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        SECURITY_SCHEME_NAME,
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                );
    }
}
