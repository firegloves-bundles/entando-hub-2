package com.entando.hub.catalog.persistence.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;


@Entity
@Setter
@Getter
@EqualsAndHashCode
public class PrivateCatalogApiKey {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "username", referencedColumnName = "username")
    private PortalUser portalUser;

    @Column(nullable = false, length = 128)
    private String label;

    @Column(nullable = false, unique = true, length = 128)
    private String apiKey;

    @Column(nullable = false)
    private Date creationDate;

    @Column(nullable = false)
    private Date lastUpdateDate;

    @PrePersist
    void createdAt() {
        this.creationDate = this.lastUpdateDate = new Date();
    }

    @PreUpdate
    void updatedAt() {
        this.lastUpdateDate = new Date();
    }

    @Override
    public String toString() {
        return "PrivateCatalogApiKey{" +
                "id=" + id +
                ", username='" + portalUser.getUsername() + '\'' +
                ", label='" + label + '\'' +
                ", creationDate='" + creationDate + '\'' +
                ", lastUpdateDate='" + lastUpdateDate + '\'' +
                '}';
    }
}
