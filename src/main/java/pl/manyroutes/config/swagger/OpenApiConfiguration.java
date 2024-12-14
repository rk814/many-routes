package pl.manyroutes.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI defineOpenApi() {
        Server server = new Server();
        server.setUrl("localhost:8080/");
        server.setDescription("Development");

        Contact contact = new Contact();
        contact.name("Rafał Krawczyk");
        contact.email("r.krawczyk@live.com");

        Info info = new Info();
        info.contact(contact);
        info.description("Many Routes is a RESTful Spring application designed for mountain and not only mountain enthusiasts.");
        info.title("Many Routes");
        info.version("0.1.0-SNAPSHOT");

        return new OpenAPI().info(info).servers(List.of(server));
    }
}
