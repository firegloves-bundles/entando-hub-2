package com.entando.hub.catalog.persistence.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Getter
@Setter
@Accessors(chain = true)
public class PortalUser implements Serializable{

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
