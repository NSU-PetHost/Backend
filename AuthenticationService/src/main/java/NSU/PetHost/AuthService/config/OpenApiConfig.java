package NSU.PetHost.AuthService.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.persistence.Column;

@OpenAPIDefinition(
        info = @Info(
                title = "PetHost system API",
                description = "API authentication service",
                version = "1.0.0",
                contact = @Contact(
                        name = "Aleksandr Kardash",
                        email = "a.kardash@g.nsu.ru",
                        url = "https://github.com/Bel9shik"
                )
        ),
        security = @SecurityRequirement(name = "JWT")
)
@SecurityScheme(
        name = "JWT",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class OpenApiConfig {
}
