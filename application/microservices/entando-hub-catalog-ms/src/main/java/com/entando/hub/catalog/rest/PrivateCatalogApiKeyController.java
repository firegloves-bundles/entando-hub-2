package com.entando.hub.catalog.rest;

import com.entando.hub.catalog.persistence.entity.PrivateCatalogApiKey;
import com.entando.hub.catalog.service.PrivateCatalogApiKeyService;
import com.entando.hub.catalog.service.dto.apikey.AddApiKeyRequestDTO;
import com.entando.hub.catalog.service.dto.apikey.ApiKeyResponseDTO;
import com.entando.hub.catalog.service.dto.apikey.EditApiKeyRequestDTO;
import com.entando.hub.catalog.service.security.SecurityHelperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static com.entando.hub.catalog.config.AuthoritiesConstants.*;

@RestController
@Validated
@RequestMapping("/api/private-catalog-api-key")
public class PrivateCatalogApiKeyController {

    private final PrivateCatalogApiKeyService privateCatalogApiKeyService;
    private final SecurityHelperService securityHelperService;

    public PrivateCatalogApiKeyController(PrivateCatalogApiKeyService privateCatalogApiKeyService, SecurityHelperService securityHelperService) {
        this.privateCatalogApiKeyService = privateCatalogApiKeyService;
        this.securityHelperService = securityHelperService;
    }

    @Operation(summary = "Get private catalog api keys", description = "Get the api keys of the logged user")
    @RolesAllowed({ADMIN, AUTHOR, MANAGER})
    @GetMapping(value = "/", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponse(responseCode = "200", description = "OK", content = @Content)
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    public PagedContent<ApiKeyResponseDTO, PrivateCatalogApiKey> getApiKeys(@RequestParam Integer page, @RequestParam Integer pageSize) {
        PagedContent<ApiKeyResponseDTO, PrivateCatalogApiKey> apiKeys;
        String username = this.securityHelperService.getContextAuthenticationUsername();
        apiKeys = this.privateCatalogApiKeyService.getApiKeysByUsername(username, page, pageSize);
        return apiKeys;
    }

    @Operation(summary = "Add new private catalog api key for the logged user")
    @RolesAllowed({ADMIN, AUTHOR, MANAGER})
    @PostMapping(value = "/", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponse(responseCode = "200", description = "OK", content = @Content)
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    public ResponseEntity<ApiKeyResponseDTO> addApiKey(@Valid @RequestBody AddApiKeyRequestDTO request) {
        String username = this.securityHelperService.getContextAuthenticationUsername();
        ApiKeyResponseDTO apiKeyResponseDTO = this.privateCatalogApiKeyService.addApiKey(username, request.getLabel());
        return new ResponseEntity<>(apiKeyResponseDTO, HttpStatus.OK);
    }

    @Operation(summary = "Edit api key label for the logged user")
    @RolesAllowed({ADMIN, AUTHOR, MANAGER})
    @PutMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponse(responseCode = "200", description = "OK", content = @Content)
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    public ResponseEntity<Void> editMyLabel(@PathVariable @NotNull Long id, @Valid @RequestBody EditApiKeyRequestDTO request) {
        String username = this.securityHelperService.getContextAuthenticationUsername();
        this.privateCatalogApiKeyService.editLabel(id, username, request.getLabel());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Delete an api key for the logged user")
    @RolesAllowed({ADMIN, AUTHOR, MANAGER})
    @DeleteMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponse(responseCode = "200", description = "OK", content = @Content)
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    public ResponseEntity<Void> deleteMyApiKey(@PathVariable @NotNull Long id) {
        String username = this.securityHelperService.getContextAuthenticationUsername();
        this.privateCatalogApiKeyService.deleteApiKey(id, username);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Regenerate an api key for the logged user")
    @RolesAllowed({ADMIN, AUTHOR, MANAGER})
    @PostMapping(value = "/regenerate/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiResponse(responseCode = "200", description = "OK", content = @Content)
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    public ResponseEntity<ApiKeyResponseDTO> regenerateMyApiKey(@PathVariable @NotNull long id) {
        String username = this.securityHelperService.getContextAuthenticationUsername();
        ApiKeyResponseDTO apiKeyResponseDTO = this.privateCatalogApiKeyService.regenerateApiKey(id, username);
        return new ResponseEntity<>(apiKeyResponseDTO, HttpStatus.OK);
    }

}
