package com.entando.hub.catalog.persistence.entity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.Setter;

/**
 * This entity class is for BUNDLE_GROUP table 
 *
 */
@Entity
@Setter
@Getter
public class BundleGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;

    @ManyToOne
    private Organisation organisation;

    @ManyToMany(mappedBy = "bundleGroups", fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    private Set<Category> categories = new HashSet<>();

//    @ManyToMany(mappedBy = "bundleGroups", fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
//    private Set<Bundle> bundles = new HashSet<>();
    
    @OneToMany(mappedBy = "bundleGroup", fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    private Set<BundleGroupVersion> version = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BundleGroup that = (BundleGroup) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

