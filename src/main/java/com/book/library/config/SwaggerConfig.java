package com.book.library.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI bookLibraryOpenAPI(SwaggerProperties swaggerProperties) {
        List<Server> servers = swaggerProperties.getServers().stream()
                .map(serverProps -> {
                    Server server = new Server();
                    // Resolve environment variables in URL if present
                    String url = resolveEnvironmentVariables(serverProps.getUrl());
                    server.setUrl(url);
                    server.setDescription(serverProps.getDescription());
                    return server;
                })
                .collect(Collectors.toList());

        Contact contact = new Contact();
        contact.setName(swaggerProperties.getContact().getName());
        contact.setEmail(swaggerProperties.getContact().getEmail());
        contact.setUrl(swaggerProperties.getContact().getUrl());

        License license = new License()
                .name(swaggerProperties.getLicense().getName())
                .url(swaggerProperties.getLicense().getUrl());

        Info info = new Info()
                .title(swaggerProperties.getInfo().getTitle())
                .version(swaggerProperties.getInfo().getVersion())
                .description(swaggerProperties.getInfo().getDescription())
                .contact(contact)
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(servers);
    }

    /**
     * Resolves environment variables in URLs
     * Spring Boot handles this automatically with ${} syntax in YAML
     */
    private String resolveEnvironmentVariables(String url) {
        return url;
    }

    @Data
    @Configuration
    @ConfigurationProperties(prefix = "swagger")
    public static class SwaggerProperties {
        private InfoProperties info;
        private ContactProperties contact;
        private LicenseProperties license;
        private List<ServerProperties> servers;

        @Data
        public static class InfoProperties {
            private String title;
            private String version;
            private String description;
        }

        @Data
        public static class ContactProperties {
            private String name;
            private String email;
            private String url;
        }

        @Data
        public static class LicenseProperties {
            private String name;
            private String url;
        }

        @Data
        public static class ServerProperties {
            private String url;
            private String description;
        }
    }
}