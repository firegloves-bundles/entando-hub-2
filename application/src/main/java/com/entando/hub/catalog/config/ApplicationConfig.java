package com.entando.hub.catalog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
@Getter
public class ApplicationConfig {

	@Value("${app.hub-group-detail-base-url}")
	private String appHubGroupDetailBaseUrl;

}
