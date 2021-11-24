package com.entando.hub.catalog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
@Getter
public class ApplicationConfig {

	@Value("${HUB_GROUP_DETAIL_BASE_URL}")
	private String appHubGroupDetailBaseUrl;

}
