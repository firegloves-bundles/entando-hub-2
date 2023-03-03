package com.entando.hub.catalog.service.dto.apikey;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiKeyResponseDTO {
    private Long id;
    private String apiKey;
    private String label;

}
