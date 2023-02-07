package com.entando.hub.catalog.persistence.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Getter
@Setter
@ToString
@SequenceGenerator(name = "catalog_id", sequenceName = "SEQ_CATALOG_ID", allocationSize = 1)
public class Catalog {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "catalog_id")
    private Long id;

    @Column(unique = true)
    private Long organisationId;
    @Schema(example = "Entando Catalog")
    @Column
    private String name;
}
