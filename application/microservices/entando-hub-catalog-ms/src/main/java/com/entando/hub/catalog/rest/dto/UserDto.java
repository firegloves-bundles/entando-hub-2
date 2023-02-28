package com.entando.hub.catalog.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
public class UserDto {
  @Schema(example = "1d97d896-4761-21fc-8217-17d5d13a104b")
  private String id;
  private Date created;

  @Schema(example = "admin")
  private String username;
  private boolean enabled;

  @Schema(example = "Admin")
  private String firstName;

  @Schema(example = "Administrator")
  private String lastName;

  @Schema(example = "admin@localhost")
  private String email;
  private Set<String> organisationIds;

  public UserDto(com.entando.hub.catalog.service.model.UserRepresentation user) {
    this.id = user.getId();
    this.created = new Date(user.getCreatedTimestamp());
    this.username = user.getUsername();
    this.enabled = user.isEnabled();
    this.firstName = user.getFirstName();
    this.lastName = user.getLastName();
    this.email = user.getEmail();
    this.organisationIds = user.getOrganisationIds().stream().map(Object::toString).collect(Collectors.toSet());
  }
}
