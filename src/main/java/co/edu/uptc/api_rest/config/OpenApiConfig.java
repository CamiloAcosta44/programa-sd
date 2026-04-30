package co.edu.uptc.api_rest.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${program.id}")
    private String replicaId;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API REST Distribuidos")
                        .version("1.0.0")
                        .description("API para gestión de productos en arquitectura distribuida. Réplica activa: **" + replicaId + "**")
                        .contact(new Contact()
                                .name("UPTC - Sistemas Distribuidos")
                                .email("distribuidos@uptc.edu.co"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("/").description("Servidor actual (réplica: " + replicaId + ")")
                ));
    }
}