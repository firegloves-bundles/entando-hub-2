package com.entando.hub.catalog.service;

import com.entando.hub.catalog.persistence.CatalogRepository;
import com.entando.hub.catalog.persistence.entity.Catalog;
import com.entando.hub.catalog.persistence.entity.Organisation;
import com.entando.hub.catalog.service.dto.CatalogDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class CatalogServiceTest {

    @Mock
    private CatalogRepository catalogRepository;
    @Mock
    private OrganisationService organisationService;

    private CatalogService catalogService;

    @BeforeEach
    void setUp() {
            this.catalogService = new CatalogService(catalogRepository, organisationService);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void shouldReturnAllCatalogs() {
        List<Catalog> catalogs = Arrays.asList(stubCatalog());
        when(this.catalogRepository.findAll()).thenReturn(catalogs);

        List<CatalogDTO> expectedCatalogsDTO =  Arrays.asList(stubCatalogDTO());

        List<CatalogDTO> actualCatalogsDTO = catalogService.getCatalogs();

        assertThat(actualCatalogsDTO).hasSize(expectedCatalogsDTO.size());

        for (int i =0; i < actualCatalogsDTO.size(); i++) {
            assertThat(actualCatalogsDTO.get(i).getId()).isEqualTo(expectedCatalogsDTO.get(i).getId());
            assertThat(actualCatalogsDTO.get(i).getName()).isEqualTo(expectedCatalogsDTO.get(i).getName());
            assertThat(actualCatalogsDTO.get(i).getOrganisationId()).isEqualTo(expectedCatalogsDTO.get(i).getOrganisationId());
        }
    }

    @Test
    void shouldReturnEmptyCatalogList() {
        when(this.catalogRepository.findAll()).thenReturn(new ArrayList<>());

        List<CatalogDTO> actualCatalogsDTO = catalogService.getCatalogs();

        assertThat(actualCatalogsDTO).hasSize(0);

    }

    @Test
    void shouldReturnCatalogById(){
        Long id = 1L;
        Optional<Catalog> catalog = Optional.of(stubCatalog());
        when(this.catalogRepository.findById(id)).thenReturn(catalog);

        ResponseEntity<CatalogDTO> actualCatalogDTO = catalogService.getCatalogById(id);

        CatalogDTO expectedCatalogDTO = stubCatalogDTO();

        assertThat(actualCatalogDTO.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actualCatalogDTO.getBody().getId()).isEqualTo(expectedCatalogDTO.getId());
        assertThat(actualCatalogDTO.getBody().getName()).isEqualTo(expectedCatalogDTO.getName());
        assertThat(actualCatalogDTO.getBody().getOrganisationId()).isEqualTo(expectedCatalogDTO.getOrganisationId());

    }

    @Test
    void shouldReturnNotFoundWhenCatalogNotExists(){
        Long id = 1L;
        Optional<Catalog> catalog = Optional.empty();
        when(this.catalogRepository.findById(id)).thenReturn(catalog);

        ResponseEntity<CatalogDTO> actualCatalogDTO = catalogService.getCatalogById(id);

        assertThat(actualCatalogDTO.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(actualCatalogDTO.getBody()).isNull();
    }

    private Catalog stubCatalog(){
        return new Catalog().setId(1L).setName("Entando private catalog").setOrganisation(new Organisation().setId(2L));
    }

    private CatalogDTO stubCatalogDTO(){
        return new CatalogDTO(1L, 2L, "Entando private catalog");
    }
}