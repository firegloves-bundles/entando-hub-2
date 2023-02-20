package com.entando.hub.catalog.rest.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class Organisation extends OrganisationNoId {

  private final String organisationId;

  public Organisation(com.entando.hub.catalog.persistence.entity.Organisation entity) {
    super(entity);
    this.organisationId = entity.getId().toString();
  }

  public Organisation(String organisationId, String name, String description) {
    super(name, description);
    this.organisationId = organisationId;
  }

}
