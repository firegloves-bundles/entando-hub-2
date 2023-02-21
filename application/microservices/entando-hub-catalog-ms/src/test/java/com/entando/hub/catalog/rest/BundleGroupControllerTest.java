package com.entando.hub.catalog.rest;

import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.rest.BundleGroupController.BundleGroupDTO;
import com.entando.hub.catalog.rest.BundleGroupController.BundleGroupNoId;
import com.entando.hub.catalog.service.BundleGroupService;
import com.entando.hub.catalog.service.BundleGroupVersionService;
import com.entando.hub.catalog.service.CatalogService;
import com.entando.hub.catalog.service.OrganisationService;
import com.entando.hub.catalog.service.exception.ConflictException;
import com.entando.hub.catalog.service.security.SecurityHelperService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;

import static com.entando.hub.catalog.config.AuthoritiesConstants.ADMIN;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BundleGroupControllerTest {

    @Mock
    private  BundleGroupService bundleGroupService;
    @Mock
    private  SecurityHelperService securityHelperService;
    @Mock
    private BundleGroupVersionService bundleGroupVersionService;
    @Mock
    private CatalogService catalogService;
    @Mock
    private OrganisationService organisationService;


    private BundleGroupController bundleGroupController;
    @BeforeEach
    void setUp() {
        this.bundleGroupController = new BundleGroupController(bundleGroupService, securityHelperService, bundleGroupVersionService, catalogService, organisationService);
    }

    @Test
    void shouldGetBundleGroupListByOrganisationId() {
        long organisationId = 1L;
        BundleGroupNoId bundleGroup = new BundleGroupNoId("test group", 1L, true, null);
        List<BundleGroup> listBundleGroup = Arrays.asList(bundleGroup.createEntity(Optional.of(1L)));
        List<BundleGroupDTO> expectedListBundleGroupDTO = Arrays.asList(stubBundleGroupDTO(bundleGroup.createEntity(Optional.of(1L))));

        when(bundleGroupService.getBundleGroups(Optional.of(Long.toString(organisationId)))).thenReturn(listBundleGroup);

        ResponseEntity<List<BundleGroupDTO>> response = bundleGroupController.getBundleGroupsByOrganisationId(Long.toString(organisationId));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(expectedListBundleGroupDTO);
    }

    @Test
    void shouldGetBundleGroupById(){
        Long bundleGroupId = 1L;
        BundleGroupNoId bundleGroup = new BundleGroupNoId("test group", 1L, true, null);
        BundleGroup entity = bundleGroup.createEntity(Optional.of(bundleGroupId));
        BundleGroupDTO expectedBundleGroupDTO = stubBundleGroupDTO(entity);

        when(bundleGroupService.getBundleGroup(bundleGroupId)).thenReturn(Optional.of(entity));

        ResponseEntity<BundleGroupDTO> response = bundleGroupController.getBundleGroup(bundleGroupId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(expectedBundleGroupDTO);
    }

    @Test
    void shouldReturnNotFoundWhenBundleGroupNotExists(){
        Long bundleGroupId = 1L;
        BundleGroupNoId bundleGroup = new BundleGroupNoId("test group", 1L, true, null);
        BundleGroup entity = bundleGroup.createEntity(Optional.of(bundleGroupId));
        BundleGroupDTO expectedBundleGroupDTO = stubBundleGroupDTO(entity);

        when(bundleGroupService.getBundleGroup(bundleGroupId)).thenReturn(Optional.empty());

        ResponseEntity<BundleGroupDTO> response = bundleGroupController.getBundleGroup(bundleGroupId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void shouldCreateBundleGroup() {
        BundleGroupNoId bundleGroup = new BundleGroupNoId("test group", 1L, true, null);
        BundleGroup entity = bundleGroup.createEntity(Optional.of(1L));
        BundleGroupDTO expectedBundleGroupDTO = stubBundleGroupDTO(entity);

        when(organisationService.existsById(bundleGroup.getOrganisationId())).thenReturn(true);
        when(bundleGroupService.createBundleGroup(any(BundleGroup.class), any(BundleGroupNoId.class))).thenReturn(entity);

        ResponseEntity<BundleGroupDTO> response = bundleGroupController.createBundleGroup(bundleGroup);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(expectedBundleGroupDTO);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenCreateBundleGroupWithInvalidOrganisationId() {
        BundleGroupNoId bundleGroup = new BundleGroupNoId("test group", 1L, true, 1L);

        when(organisationService.existsById(bundleGroup.getOrganisationId())).thenReturn(false);

        Exception exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            bundleGroupController.createBundleGroup(bundleGroup);
        });

        String expectedMessage = String.format("Organisation with ID %s not found", bundleGroup.getOrganisationId());
        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenCreateBundleGroupWithInvalidCatalogId() {
        BundleGroupNoId bundleGroup = new BundleGroupNoId("test group", 1L, false, 1L);

        when(organisationService.existsById(bundleGroup.getOrganisationId())).thenReturn(true);
        when(catalogService.existCatalogById(bundleGroup.getCatalogId())).thenReturn(false);

        Exception exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            bundleGroupController.createBundleGroup(bundleGroup);
        });

        String expectedMessage = String.format("Catalog with ID %d not found", bundleGroup.getCatalogId());
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void shouldThrowAccessDeniedExceptionWhenCreateBundleGroupWithInvalidUser() {
        BundleGroupNoId bundleGroup = new BundleGroupNoId("test group", 1L, true, null);

        when(organisationService.existsById(bundleGroup.getOrganisationId())).thenReturn(true);
        when(securityHelperService.userIsNotAdminAndDoesntBelongToOrg(bundleGroup.getOrganisationId())).thenReturn(true);

        Exception exception = Assertions.assertThrows(AccessDeniedException.class, () -> {
            bundleGroupController.createBundleGroup(bundleGroup);
        });

        String expectedMessage = String.format("Only %s users can create bundle groups for any organisation, the other ones can create bundle groups only for their organisation", ADMIN);
        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }


    @Test
    void shouldUpdateBundleGroup() {
        Long bundleGroupId = 1L;
        BundleGroupNoId bundleGroup = new BundleGroupNoId("test group", 1L, true, null);
        BundleGroup entity = bundleGroup.createEntity(Optional.of(1L));
        BundleGroupDTO expectedBundleGroupDTO = stubBundleGroupDTO(entity);

        when(organisationService.existsById(bundleGroup.getOrganisationId())).thenReturn(true);
        when(bundleGroupService.createBundleGroup(any(BundleGroup.class), any(BundleGroupNoId.class))).thenReturn(entity);
        when(bundleGroupService.existsById(bundleGroupId)).thenReturn(true);
        when(bundleGroupService.getBundleGroup(bundleGroupId)).thenReturn(Optional.of(entity));
        when(bundleGroupVersionService.isBundleGroupEditable(entity)).thenReturn(true);

        ResponseEntity<BundleGroupDTO> response = bundleGroupController.updateBundleGroup(bundleGroupId, bundleGroup);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(expectedBundleGroupDTO);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenUpdateBundleGroupWithBundleGroupNotExisting() {
        Long bundleGroupId = 1L;
        BundleGroupNoId bundleGroup = new BundleGroupNoId("test group", 1L, true, null);

        when(organisationService.existsById(bundleGroup.getOrganisationId())).thenReturn(true);
        when(bundleGroupService.existsById(bundleGroupId)).thenReturn(false);

        Exception exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            bundleGroupController.updateBundleGroup(bundleGroupId,bundleGroup);
        });

        String expectedMessage = String.format("BundleGroup %s does not exist", bundleGroupId);
        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void shouldThrowConflictExceptionWhenUpdateBundleGroupNotEditable() {
        Long bundleGroupId = 1L;
        BundleGroupNoId bundleGroup = new BundleGroupNoId("test group", 1L, true, null);
        BundleGroup entity = bundleGroup.createEntity(Optional.of(1L));

        when(organisationService.existsById(bundleGroup.getOrganisationId())).thenReturn(true);
        when(bundleGroupService.existsById(bundleGroupId)).thenReturn(true);
        when(bundleGroupService.existsById(bundleGroupId)).thenReturn(true);
        when(bundleGroupService.getBundleGroup(bundleGroupId)).thenReturn(Optional.of(entity));
        when(bundleGroupVersionService.isBundleGroupEditable(entity)).thenReturn(false);

        Exception exception = Assertions.assertThrows(ConflictException.class, () -> {
            bundleGroupController.updateBundleGroup(bundleGroupId,bundleGroup);
        });

        String expectedMessage = String.format("BundleGroup %s is not editable", bundleGroupId);
        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void shouldDeleteBundleGroup(){
        Long bundleGroupId = 1L;

        when(bundleGroupService.existsById(bundleGroupId)).thenReturn(true);

        ResponseEntity<Void> response = bundleGroupController.deleteBundleGroup(bundleGroupId);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void shouldReturnNotFoundWhenDeleteBundleGroupThatNotExist(){
        Long bundleGroupId = 1L;

        when(bundleGroupService.existsById(bundleGroupId)).thenReturn(false);

        ResponseEntity<Void> response = bundleGroupController.deleteBundleGroup(bundleGroupId);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    private BundleGroupDTO stubBundleGroupDTO(BundleGroup bundleGroup){
        return new BundleGroupDTO(bundleGroup);
    }

}
