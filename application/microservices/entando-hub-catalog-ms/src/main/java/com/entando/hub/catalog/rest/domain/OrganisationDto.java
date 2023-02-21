package com.entando.hub.catalog.rest.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrganisationDto extends OrganisationNoId {

  private String organisationId;

  public OrganisationDto(com.entando.hub.catalog.persistence.entity.Organisation entity) {
    super(entity);
    this.organisationId = entity.getId().toString();
  }

  public OrganisationDto(String organisationId, String name, String description) {
    super(name, description);
    this.organisationId = organisationId;
  }

}
