package com.entando.hub.catalog.service.dto;

import com.entando.hub.catalog.persistence.entity.BundleGroupVersion;
import com.entando.hub.catalog.persistence.entity.DescriptorVersion;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.persistence.*;
import java.util.Set;

@Data
@Builder
@Jacksonized
public class BundleEntityDto {

    private Long id;
    private String name;
    private String description;
    private String gitRepoAddress;
    private String gitSrcRepoAddress;
    private String dependencies;
    private DescriptorVersion descriptorVersion = DescriptorVersion.V1;
    private Set<BundleGroupVersion> bundleGroupVersions;

}
