package com.entando.hub.catalog.service.dto;

import com.entando.hub.catalog.persistence.entity.Bundle;
import com.entando.hub.catalog.persistence.entity.BundleGroup;
import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * This is the exact match of com.entando.hub.catalog.persistence.entity.BundleGroupVersion.
 * TODO find usage in the FE and and possibly use the proper dto
 */
@Data
@NoArgsConstructor
public class BundleGroupVersionEntityDto {

    private Long id;

    private String description;

    private String documentationUrl;

    private String version;

    private String descriptionImage;

    private BundleGroupVersion.Status status = BundleGroupVersion.Status.NOT_PUBLISHED;

    private Boolean displayContactUrl;

    private String contactUrl;

    private BundleGroup bundleGroup;

    private Set<Bundle> bundles = new HashSet<>();

    private LocalDateTime lastUpdated;
}
