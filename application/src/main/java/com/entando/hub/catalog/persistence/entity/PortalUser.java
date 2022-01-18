package com.entando.hub.catalog.persistence.entity;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class PortalUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique=true)
    private String username;

    private String email;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "portal_user_organisation",
            joinColumns = @JoinColumn(name = "portal_user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "organisation_id", referencedColumnName = "id"))
    private Set<Organisation> organisations = new HashSet<>();

}
