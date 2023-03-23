package com.entando.hub.catalog.persistence.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@SequenceGenerator(name = "organisation_id", sequenceName = "SEQ_ORGANISATION_ID", allocationSize = 1)
public class Organisation implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "organisation_id")
    private Long id;

    @Column(unique = true)
    private String name;

    private String description;

    @OneToMany(mappedBy = "organisation", fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    private Set<BundleGroup> bundleGroups;

    @ManyToMany(mappedBy = "organisations", fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    private Set<PortalUser> portalUsers;

/*
    public void addBundleGroup(BundleGroup bundleGroup) {
        this.bundleGroups.add(bundleGroup);
        bundleGroup.setOrganisation(this);
    }
    //TODO performance check, we can do better
    public void mergeBundleGroups(Set<BundleGroup> newBundleGroups) {
        if(this.bundleGroups==null) {
            this.bundleGroups=new HashSet<>();
        }
        //add all the new ones
        newBundleGroups.stream().forEach(this::addBundleGroup);
        //to exclude
        Set<BundleGroup> toExclude = this.bundleGroups.stream().filter(bundleGroup -> !newBundleGroups.contains(bundleGroup)).collect(Collectors.toSet());
        //remove the old ones
        toExclude.stream().forEach(bundleGroup -> {
            this.bundleGroups.remove(bundleGroup);
            bundleGroup.setOrganisation(null);
        });
    }
*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Organisation that = (Organisation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Organisation{" +
                "id=" + id +
                ", Name='" + name + '\'' +
                ", Description='" + description + '\'' +
                '}';
    }
}
