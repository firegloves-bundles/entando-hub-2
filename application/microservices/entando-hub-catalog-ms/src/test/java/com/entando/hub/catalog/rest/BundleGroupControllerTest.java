package com.entando.hub.catalog.rest;

import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.rest.dto.BundleGroupDto;
import com.entando.hub.catalog.service.BundleGroupService;
import com.entando.hub.catalog.service.BundleGroupVersionService;
import com.entando.hub.catalog.service.OrganisationService;
import com.entando.hub.catalog.service.exception.ConflictException;
import com.entando.hub.catalog.service.mapper.BundleGroupMapper;
import com.entando.hub.catalog.service.mapper.BundleGroupMapperImpl;
import com.entando.hub.catalog.service.security.SecurityHelperService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.entando.hub.catalog.config.AuthoritiesConstants.ADMIN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ComponentScan(basePackageClasses = {BundleGroupMapper.class, BundleGroupMapperImpl.class})
class BundleGroupControllerTest {

    @Spy
    private BundleGroupMapper bundleGroupMapper = new BundleGroupMapperImpl();
    @Mock
    private  BundleGroupService bundleGroupService;
    @Mock
    private  SecurityHelperService securityHelperService;
    @Mock
    private BundleGroupVersionService bundleGroupVersionService;
    @Mock
    private OrganisationService organisationService;


    private BundleGroupController bundleGroupController;
    @BeforeEach
    void setUp() {
        this.bundleGroupController = new BundleGroupController(bundleGroupService, securityHelperService, bundleGroupVersionService, organisationService, bundleGroupMapper);
    }

    @Test
    void shouldGetBundleGroupListByOrganisationId() {
        long organisationId = 1L;
        BundleGroupDto bundleGroup = stubBundleGroupCreate("test group", 1L, true);
//        List<BundleGroup> listBundleGroup = Arrays.asList(bundleGroup.createEntity(Optional.of(1L)));
        bundleGroup.setBundleGroupId("1"); // FIXME shouldn't be necessary
        BundleGroup entity = bundleGroupMapper.toEntity(bundleGroup);
        List<BundleGroup> listBundleGroup = Arrays.asList(entity);
        List<BundleGroupDto> expectedListBundleGroupDTO = Arrays.asList(stubBundleGroupDTO(entity));

        when(bundleGroupService.getBundleGroups(Optional.of(Long.toString(organisationId)))).thenReturn(listBundleGroup);

        ResponseEntity<List<BundleGroupDto>> response = bundleGroupController.getBundleGroupsByOrganisationId(Long.toString(organisationId));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(expectedListBundleGroupDTO);
    }

    @Test
    void shouldGetBundleGroupById(){
        Long bundleGroupId = 1L;
        BundleGroupDto bundleGroup = stubBundleGroupCreate("test group", 1L, true);
//        BundleGroup entity = bundleGroup.createEntity(Optional.of(bundleGroupId));
        bundleGroup.setBundleGroupId(bundleGroupId.toString());
        BundleGroup entity = bundleGroupMapper.toEntity(bundleGroup);
        BundleGroupDto expectedBundleGroupDTO = stubBundleGroupDTO(entity);

        when(bundleGroupService.getBundleGroup(bundleGroupId)).thenReturn(Optional.of(entity));

        ResponseEntity<BundleGroupDto> response = bundleGroupController.getBundleGroup(bundleGroupId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(expectedBundleGroupDTO);
    }

    @Test
    void shouldReturnNotFoundWhenBundleGroupNotExists(){
        Long bundleGroupId = 1L;
        BundleGroupDto bundleGroup = stubBundleGroupCreate("test group", 1L, true);
//        BundleGroup entity = bundleGroup.createEntity(Optional.of(bundleGroupId));
        bundleGroup.setBundleGroupId(bundleGroupId.toString());
        BundleGroup entity = bundleGroupMapper.toEntity(bundleGroup);
        BundleGroupDto expectedBundleGroupDTO = stubBundleGroupDTO(entity);

        when(bundleGroupService.getBundleGroup(bundleGroupId)).thenReturn(Optional.empty());

        ResponseEntity<BundleGroupDto> response = bundleGroupController.getBundleGroup(bundleGroupId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void shouldCreateBundleGroup() {
        BundleGroupDto bundleGroup = stubBundleGroupCreate("test group", 1L, true);
//        BundleGroup entity = bundleGroup.createEntity(Optional.of(1L));
        bundleGroup.setBundleGroupId("1");
        BundleGroup entity = bundleGroupMapper.toEntity(bundleGroup);
        BundleGroupDto expectedBundleGroupDTO = stubBundleGroupDTO(entity);

        when(organisationService.existsById(bundleGroup.getOrganisationId())).thenReturn(true);
        when(bundleGroupService.createBundleGroup(any(BundleGroup.class), any(BundleGroupDto.class))).thenReturn(entity);

        ResponseEntity<BundleGroupDto> response = bundleGroupController.createBundleGroup(bundleGroup);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(expectedBundleGroupDTO);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenCreateBundleGroupWithInvalidOrganisationId() {
        BundleGroupDto bundleGroup = stubBundleGroupCreate("test group", 1L, true);

        when(organisationService.existsById(bundleGroup.getOrganisationId())).thenReturn(false);

        Exception exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            bundleGroupController.createBundleGroup(bundleGroup);
        });

        String expectedMessage = String.format("Organisation with ID %s not found", bundleGroup.getOrganisationId());
        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void shouldThrowAccessDeniedExceptionWhenCreateBundleGroupWithInvalidUser() {
        BundleGroupDto bundleGroup = stubBundleGroupCreate("test group", 1L, true);

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
        BundleGroupDto bundleGroup = stubBundleGroupCreate("test group", 1L, true);
//        BundleGroup entity = bundleGroup.createEntity(Optional.of(1L));
        bundleGroup.setBundleGroupId(bundleGroupId.toString());
        BundleGroup entity = bundleGroupMapper.toEntity(bundleGroup);
        BundleGroupDto expectedBundleGroupDTO = stubBundleGroupDTO(entity);

        when(organisationService.existsById(bundleGroup.getOrganisationId())).thenReturn(true);
        when(bundleGroupService.createBundleGroup(any(BundleGroup.class), any(BundleGroupDto.class))).thenReturn(entity);
        when(bundleGroupService.existsById(bundleGroupId)).thenReturn(true);
        when(bundleGroupService.getBundleGroup(bundleGroupId)).thenReturn(Optional.of(entity));
        when(bundleGroupVersionService.isBundleGroupEditable(entity)).thenReturn(true);

        ResponseEntity<BundleGroupDto> response = bundleGroupController.updateBundleGroup(bundleGroupId, bundleGroup);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(expectedBundleGroupDTO);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUpdateBundleGroupWithBundleGroupNotExisting() {
        Long bundleGroupId = 1L;
        BundleGroupDto bundleGroup = stubBundleGroupCreate("test group", 1L, true);

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
    void shouldThrowConflictExceptionWhenUpdateBundleGroupNotEditable() {
        Long bundleGroupId = 1L;
        BundleGroupDto bundleGroup = stubBundleGroupCreate("test group", 1L, true);
//        BundleGroup entity = bundleGroup.createEntity(Optional.of(1L));
        bundleGroup.setBundleGroupId(bundleGroupId.toString());
        BundleGroup entity = bundleGroupMapper.toEntity(bundleGroup);

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
    void shouldDeleteBundleGroup(){
        Long bundleGroupId = 1L;

        when(bundleGroupService.existsById(bundleGroupId)).thenReturn(true);

        ResponseEntity<Void> response = bundleGroupController.deleteBundleGroup(bundleGroupId);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void shouldReturnNotFoundWhenDeleteBundleGroupThatNotExist(){
        Long bundleGroupId = 1L;

        when(bundleGroupService.existsById(bundleGroupId)).thenReturn(false);

        ResponseEntity<Void> response = bundleGroupController.deleteBundleGroup(bundleGroupId);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    private BundleGroupDto stubBundleGroupDTO(BundleGroup bundleGroup){
        return bundleGroupMapper.toDto(bundleGroup);
    }

    private BundleGroupDto stubBundleGroupCreate(String name, long organisationId, boolean isPublic) {
        BundleGroupDto dto = new BundleGroupDto();
        
        dto.setName(name);
        dto.setOrganisationId(organisationId);
        dto.setPublicCatalog(isPublic);
        return  dto;
    }
}
