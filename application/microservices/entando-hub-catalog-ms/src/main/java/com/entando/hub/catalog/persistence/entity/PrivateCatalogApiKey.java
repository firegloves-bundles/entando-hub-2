package com.entando.hub.catalog.persistence.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Setter
@Getter
public class PrivateCatalogApiKey {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "username", referencedColumnName = "username")
    private PortalUser portalUser;

    @Column(nullable = false, length = 128)
    private String label;

    @Column(nullable = false, unique = true)
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrivateCatalogApiKey privateCatalogApiKey = (PrivateCatalogApiKey) o;
        return Objects.equals(id, privateCatalogApiKey.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
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
