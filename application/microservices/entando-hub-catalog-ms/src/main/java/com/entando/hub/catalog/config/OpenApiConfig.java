package com.entando.hub.catalog.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class OpenApiConfig {
 
        @Value("${app.version}")
        private String appVersion;
 
        @Value("${app.name}")
        private String appName;
 
        @Value("${app.security.auth-server-url}")
        private String authServerUrl;
 
        @Value("${app.security.realm}")
        private String realm;
 
        @Value("${app.security.client-ui}")
        private String client;
 
        @Bean
        public OpenAPI customOpenAPI() {
                OAuthFlows flows = new OAuthFlows();
                OAuthFlow flow = new OAuthFlow();
 
                flow.setAuthorizationUrl(authServerUrl + "/realms/" + realm + "/protocol/openid-connect/auth");
 
                Scopes scopes = new Scopes();
                flow.setScopes(scopes);
                flows = flows.implicit(flow);
 
                return new OpenAPI()
                                .components(new Components().addSecuritySchemes("keycloak",
                                                new SecurityScheme().type(SecurityScheme.Type.OAUTH2).flows(flows)))
                                .info(new Info().title(appName)
                                                .version(appVersion))
                                .addSecurityItem(new SecurityRequirement().addList("keycloak",
                                                Arrays.asList("read", "write")));
        }


        @Bean
        GroupedOpenApi appBuilderApis() {
                return GroupedOpenApi.builder().group("appbuilder").pathsToMatch("/**/appbuilder/**").build();
        }
        @Bean
        GroupedOpenApi entApis() {
                return GroupedOpenApi.builder().group("ent").pathsToMatch("/**/ent/**").build();
        }

        @Bean
        GroupedOpenApi hubApis() { // group all APIs with `admin` in the path
                return GroupedOpenApi.builder().group("hub").pathsToExclude("/**/ent/**","/**/appbuilder/**").build();
        }

}
