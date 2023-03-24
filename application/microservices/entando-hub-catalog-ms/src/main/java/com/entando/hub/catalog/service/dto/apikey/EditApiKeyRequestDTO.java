package com.entando.hub.catalog.service.dto.apikey;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

@Data
public class EditApiKeyRequestDTO {
    @NotEmpty(message = "label is a mandatory field")
    @Length(min = 1, max = 128)
    private String label;
}
