package com.entando.hub.catalog.rest;

import com.entando.hub.catalog.persistence.entity.Catalog;
import com.entando.hub.catalog.persistence.entity.Organisation;
import com.entando.hub.catalog.service.CatalogService;
import com.entando.hub.catalog.service.dto.CatalogDto;
import com.entando.hub.catalog.service.exception.ConflictException;
import com.entando.hub.catalog.service.exception.NotFoundException;
import com.entando.hub.catalog.service.security.SecurityHelperService;
import com.entando.hub.catalog.testhelper.TestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogControllerTest {

    @Mock
    private CatalogService catalogService;
    @Mock
    private SecurityHelperService securityHelperService;

    private CatalogController catalogController;

    @BeforeEach
    void setUp() {
        this.catalogController = new CatalogController(catalogService, securityHelperService);
    }

    @Test
    void shouldGetCatalogs() {
        List<Catalog> expectedCatalogs = Arrays.asList(stubCatalog(), stubCatalog());
        List<CatalogDto> expectedCatalogsDTO = Arrays.asList(stubCatalogDTO(), stubCatalogDTO());
        when(catalogService.getCatalogs(anyString(), anyBoolean())).thenReturn(expectedCatalogs);
        when(securityHelperService.getContextAuthenticationUsername()).thenReturn("admin");
        when(securityHelperService.isAdmin()).thenReturn(true);

        ResponseEntity<List<CatalogDto>> responseEntity = catalogController.getCatalogs();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<CatalogDto> actualCatalogsDTO = responseEntity.getBody();
        assertThat(actualCatalogsDTO).usingRecursiveComparison().isEqualTo(expectedCatalogsDTO);
    }

    @Test
    void shouldGetEmptyListOfCatalogs() {

        when(catalogService.getCatalogs(anyString(), anyBoolean())).thenReturn(Collections.emptyList());
        when(securityHelperService.getContextAuthenticationUsername()).thenReturn("admin");
        when(securityHelperService.isAdmin()).thenReturn(true);

        ResponseEntity<List<CatalogDto>> responseEntity = catalogController.getCatalogs();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEmpty();
    }

    @Test
    void shouldGetCatalogById() throws NotFoundException {
        Catalog expectedCatalog = stubCatalog();
        CatalogDto expectedCatalogDTO = stubCatalogDTO();
        Long catalogId = expectedCatalogDTO.getId();

        when(this.securityHelperService.isAdmin()).thenReturn(true);
        when(this.securityHelperService.getContextAuthenticationUsername()).thenReturn(TestHelper.ADMIN_USERNAME);
        when(catalogService.getCatalogById(TestHelper.ADMIN_USERNAME, catalogId, true)).thenReturn(expectedCatalog);

        ResponseEntity<CatalogDto> responseEntity = catalogController.getCatalog(catalogId);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        CatalogDto actualCatalogDTO = responseEntity.getBody();
        assertThat(actualCatalogDTO).usingRecursiveComparison().isEqualTo(expectedCatalogDTO);
    }


    @Test
    void shouldCreateCatalog() {
        CatalogDto expectedCatalogDTO = stubCatalogDTO();
        Long organisationId = expectedCatalogDTO.getOrganisationId();

        when(catalogService.createCatalog(organisationId)).thenReturn(stubCatalog());

        ResponseEntity<CatalogDto> responseEntity = catalogController.createCatalog(organisationId);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        CatalogDto actualCatalogDTO = responseEntity.getBody();
        assertThat(actualCatalogDTO).usingRecursiveComparison().isEqualTo(expectedCatalogDTO);
    }

    @Test
    void shouldReturnConflictWithCreateCatalogWhenAlreadyExists() {
        Long organisationId = 1L;

        when(catalogService.createCatalog(organisationId)).thenThrow(ConflictException.class);

        ResponseEntity<CatalogDto> responseEntity = catalogController.createCatalog(organisationId);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }


    @Test
    void shouldReturnNotFoundWithCreateCatalogWhenOrganisationNotExists() {
        Long organisationId = 1L;

        when(catalogService.createCatalog(organisationId)).thenThrow(NotFoundException.class);

        ResponseEntity<CatalogDto> responseEntity = catalogController.createCatalog(organisationId);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }



    @Test
    void shouldDeleteCatalog() {
        Catalog catalog = stubCatalog();
        CatalogDto expectedCatalogDTO = stubCatalogDTO();
        Long catalogId = expectedCatalogDTO.getId();

        when(catalogService.deleteCatalog(catalogId)).thenReturn(catalog);

        ResponseEntity<CatalogDto> responseEntity = catalogController.deleteCatalog(catalogId);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        CatalogDto actualCatalogDTO = responseEntity.getBody();
        assertThat(actualCatalogDTO).usingRecursiveComparison().isEqualTo(expectedCatalogDTO);

    }

    @Test
    void shouldReturnNotFoundWhenDeleteCatalog() {
        Long catalogId = 1L;

        when(catalogService.deleteCatalog(catalogId)).thenThrow(NotFoundException.class);

        ResponseEntity<CatalogDto> responseEntity = catalogController.deleteCatalog(catalogId);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }



    private CatalogDto stubCatalogDTO(){
        return new CatalogDto(1L, 2L, "Entando private catalog");
    }
    private Catalog stubCatalog(){
        return new Catalog().setId(1L).setName("Entando private catalog").setOrganisation(new Organisation().setId(2L));
    }
}
