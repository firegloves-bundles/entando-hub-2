package com.entando.hub.catalog.persistence.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.UpdateTimestamp;

import lombok.Getter;
import lombok.Setter;

/**
 * This entity class is for BUNDLE_GROUP_VERSION table
 *
 */
@Entity
@Setter
@Getter
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "BUNDLE_GROUP_ID", "VERSION" }) })
public class BundleGroupVersion {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(nullable = false)
	private Long id;

	@Column(nullable = false, length = 600)
	private String description;

	@Column(nullable = false)
	private String documentationUrl;

	@Column(nullable = false)
	private String version;

	@Lob
	private String descriptionImage;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Status status = Status.NOT_PUBLISHED;

	@ManyToOne
	private BundleGroup bundleGroup;

	@ManyToMany(mappedBy = "bundleGroupVersions", fetch = FetchType.LAZY, cascade = { CascadeType.MERGE })
	private Set<Bundle> bundles = new HashSet<>();

	@UpdateTimestamp
	private LocalDateTime lastUpdated;

	public enum Status {
		NOT_PUBLISHED, PUBLISHED, PUBLISH_REQ, DELETE_REQ, DELETED, ARCHIVE
	}
}
