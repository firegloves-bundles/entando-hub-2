package com.entando.hub.catalog.rest.dto.apikey;

import lombok.Data;

@Data
public class GetApiKeyResponseDTO {
    private Long id;
    private String username;
    private String label;
}
