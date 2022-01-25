package com.entando.hub.catalog.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.Organisation;
import com.entando.hub.catalog.rest.OrganisationController.OrganisationNoId;
import com.entando.hub.catalog.service.OrganisationService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@RunWith(MockitoJUnitRunner.Silent.class)
public class OrganisationControllerTest {
	
@InjectMocks
OrganisationController organisationController;
@Mock
OrganisationService organisationService;

@Test
public void testGetOrganisations() {
	List<Organisation> organisationList = new ArrayList<>();
	Organisation organisation =new Organisation();
	organisation.setId(3001L);
	organisation.setName("Technical");
	organisation.setDescription("New Organisation");
	organisationList.add(organisation);
	Mockito.when(organisationService.getOrganisations()).thenReturn(organisationList);
	List<com.entando.hub.catalog.rest.OrganisationController.Organisation> organisationResultList = organisationController.getOrganisations();
	assertNotNull(organisationResultList);
	assertEquals(organisationList.get(0).getName(),organisationResultList.get(0).getName());
	organisationList.get(0).getName().equals(organisationResultList.get(0).getName());
	//assertEquals(organisationList.get(0).getId(),organisationResultList.get(0).getOrganisationId());
	assertEquals(organisationList.get(0).getDescription(),organisationResultList.get(0).getDescription());
	//assertEquals(organisationList.hashCode(),organisationResultList.hashCode());
	//assertEquals(organisationList,organisationResultList);


}
@Test
public void testGetOrganisation() {
	List<Organisation> organisationList = new ArrayList<>();
	Organisation organisation =new Organisation();
	organisation.setId(3001L);
	organisation.setName("Technical");
	organisation.setDescription("New Organisation");
	organisationList.add(organisation);
	String organisationId = Long.toString(organisation.getId());
	Mockito.when(organisationService.getOrganisation(organisationId)).thenReturn(Optional.of(organisation));
	ResponseEntity<com.entando.hub.catalog.rest.OrganisationController.Organisation> organisationResult = organisationController.getOrganisation(organisationId);
	assertNotNull(organisationResult);
	assertEquals(HttpStatus.OK,organisationResult.getStatusCode());
}
@Test
public void testGetOrganisationFails() {
	List<Organisation> organisationList = new ArrayList<>();
	Organisation organisation =new Organisation();
	organisation.setId(3001L);
	organisation.setName("Technical");
	organisation.setDescription("New Organisation");
	organisationList.add(organisation);
	String organisationId = Long.toString(organisation.getId());
	Mockito.when(organisationService.getOrganisation(null)).thenReturn(Optional.of(organisation));
	ResponseEntity<com.entando.hub.catalog.rest.OrganisationController.Organisation> organisationResult = organisationController.getOrganisation(organisationId);
	assertNotNull(organisationResult);
	assertEquals(HttpStatus.NOT_FOUND,organisationResult.getStatusCode());

}
@Test
public void testCreateOrganisation() {
	List<Organisation> organisationList = new ArrayList<>();
	Organisation organisation =new Organisation();
	organisation.setId(3001L);
	organisation.setName("Technical");
	organisation.setDescription("New Organisation");
	BundleGroup bundleGroup = new BundleGroup();
	bundleGroup.setId(1001L);
	bundleGroup.setName("New Xyz");
	Set<BundleGroup> aa =  new LinkedHashSet<BundleGroup>();
	aa.add(bundleGroup);
	organisation.setBundleGroups(aa);
	organisationList.add(organisation);
	String organisationId = Long.toString(organisation.getId());
	OrganisationNoId organisationNoId = new OrganisationNoId(organisation.getName(),organisation.getDescription());
	com.entando.hub.catalog.rest.OrganisationController.Organisation org1 = new com.entando.hub.catalog.rest.OrganisationController.Organisation(organisationId, organisation.getName(), organisation.getDescription());
	Mockito.when(organisationService.createOrganisation(organisationNoId.createEntity(Optional.empty()), organisationNoId)).thenReturn(organisation);
	ResponseEntity<com.entando.hub.catalog.rest.OrganisationController.Organisation> organisationResult = organisationController.createOrganisation(organisationNoId);
	assertNotNull(organisationResult);
	assertEquals(HttpStatus.CREATED,organisationResult.getStatusCode());
}
@Test
public void testUpdateOrganisation() {
	List<Organisation> organisationList = new ArrayList<>();
	Organisation organisation =new Organisation();
	organisation.setId(3001L);
	organisation.setName("Technical");
	organisation.setDescription("New Organisation");
	organisationList.add(organisation);
	String organisationId = Long.toString(organisation.getId());
	organisation.getBundleGroups();
	OrganisationNoId organisationNoId = new OrganisationNoId(organisation);
	Mockito.when(organisationService.getOrganisation(organisationId)).thenReturn(Optional.of(organisation));
	Mockito.when(organisationService.createOrganisation(organisationNoId.createEntity(Optional.of(organisationId)) , organisationNoId)).thenReturn(organisation);
	ResponseEntity<com.entando.hub.catalog.rest.OrganisationController.Organisation> organisationResult = organisationController.updateOrganisation(organisationId, organisationNoId);
	assertNotNull(organisationResult);
	assertEquals(HttpStatus.OK,organisationResult.getStatusCode());
}
@Test
public void testUpdateOrganisationFails() {
	List<Organisation> organisationList = new ArrayList<>();
	Organisation organisation =new Organisation();
	organisation.setId(3001L);
	organisation.setName("Technical");
	organisation.setDescription("New Organisation");
	organisationList.add(organisation);
	String organisationId = Long.toString(organisation.getId());
	OrganisationNoId organisationNoId = new OrganisationNoId(organisation);
	Mockito.when(organisationService.getOrganisation(null)).thenReturn(Optional.of(organisation));
	ResponseEntity<com.entando.hub.catalog.rest.OrganisationController.Organisation> organisationResult = organisationController.updateOrganisation(organisationId, organisationNoId);
	assertNotNull(organisationResult);
	assertEquals(HttpStatus.NOT_FOUND,organisationResult.getStatusCode());
}
@Test
public void testDeleteOrganisation()
{
	List<Organisation> organisationList = new ArrayList<>();
	Organisation organisation =new Organisation();
	organisation.setId(3001L);
	organisation.setName("Technical");
	organisation.setDescription("New Organisation");
	organisationList.add(organisation);
	String organisationId = Long.toString(organisation.getId());
	OrganisationNoId organisationNoId = new OrganisationNoId(organisation);
	Mockito.when(organisationService.getOrganisation(organisationId)).thenReturn(Optional.of(organisation));
	organisationService.deleteOrganisation(organisationId);
	ResponseEntity<com.entando.hub.catalog.rest.OrganisationController.Organisation> deleteOrganisationResult = organisationController.deleteOrganisation(organisationId);
	assertNotNull(deleteOrganisationResult);
	assertEquals(HttpStatus.NO_CONTENT,deleteOrganisationResult.getStatusCode());
}
@Test
public void testDeleteOrganisationFails()
{
	List<Organisation> organisationList = new ArrayList<>();
	Organisation organisation =new Organisation();
	organisation.setId(3001L);
	organisation.setName("Technical");
	organisation.setDescription("New Organisation");
	organisationList.add(organisation);
	String organisationId = Long.toString(organisation.getId());
	OrganisationNoId organisationNoId = new OrganisationNoId(organisation);
	Mockito.when(organisationService.getOrganisation(null)).thenReturn(Optional.of(organisation));
	ResponseEntity<com.entando.hub.catalog.rest.OrganisationController.Organisation> deleteOrganisationResult = organisationController.deleteOrganisation(organisationId);
	assertNotNull(deleteOrganisationResult);
	assertEquals(HttpStatus.NOT_FOUND,deleteOrganisationResult.getStatusCode());
}
}

