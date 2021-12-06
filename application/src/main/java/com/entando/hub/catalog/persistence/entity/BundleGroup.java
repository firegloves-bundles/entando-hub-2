package com.entando.hub.catalog.persistence.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Setter
@Getter
public class BundleGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String description;
    private String documentationUrl;
    private String bundleGroupUrl;
    private String version;

    @UpdateTimestamp
    private LocalDateTime lastUpdate;

    @Lob
    private String descriptionImage;
    @Enumerated(EnumType.STRING)
    private Status status = Status.NOT_PUBLISHED;

    @ManyToOne
    private Organisation organisation;

    @ManyToMany(mappedBy = "bundleGroups", fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    private Set<Category> categories = new HashSet<>();

    @ManyToMany(mappedBy = "bundleGroups", fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    private Set<Bundle> bundles = new HashSet<>();

/*
    public void addCategory(Category category) {
        this.categories.add(category);
        Objects.requireNonNullElseGet(category.getBundleGroups(), category::getBundleGroups).add(this);
    }

    //TODO performance check, we can do better
    public void mergeCategories(Set<Category> newCategories) {
        //add all the new ones
        newCategories.stream().forEach(this::addCategory);
        //to exclude
        Set<Category> toExclude = this.categories.stream().filter(category -> !newCategories.contains(category)).collect(Collectors.toSet());
        //remove the old ones
        toExclude.stream().forEach(category -> {
            this.categories.remove(category);
            Objects.requireNonNullElseGet(category.getBundleGroups(), category::getBundleGroups).remove(this);
        });
    }
*/


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

    @Override
    public String toString() {
        return "BundleGroup{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", descriptionImage='" + descriptionImage + '\'' +
                ", organisation=" + organisation +
                ", version=" + version +
                '}';
    }

    public  enum Status {
        NOT_PUBLISHED, PUBLISHED, PUBLISH_REQ, DELETE_REQ, DELETED
    }
}

