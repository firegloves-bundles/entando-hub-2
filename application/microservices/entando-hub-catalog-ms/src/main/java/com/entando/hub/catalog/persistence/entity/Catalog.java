package com.entando.hub.catalog.persistence.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.experimental.Accessors;


@Entity
@Getter
@Setter
@Accessors(chain = true)
@ToString
@SequenceGenerator(name = "catalog_id", sequenceName = "SEQ_CATALOG_ID", allocationSize = 1)
public class Catalog {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "catalog_id")
    private Long id;
    @OneToOne
    @JoinColumn(name = "organisation_id")
    private com.entando.hub.catalog.persistence.entity.Organisation organisation;
    @Schema(example = "Entando Catalog")
    @Column
    private String name;
}
