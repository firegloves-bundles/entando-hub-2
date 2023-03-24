package com.entando.hub.catalog.persistence.entity;

import java.io.Serializable;
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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.ToString;
import lombok.experimental.Accessors;
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
@Accessors(chain = true)
@Table(uniqueConstraints = { @UniqueConstraint(name = "bundle_group_version_unique_key",columnNames = { "BUNDLE_GROUP_ID", "VERSION" }) })
public class BundleGroupVersion implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(nullable = false)
	private Long id;

	@Column(nullable = false, length = 4000)
	private String description;

	@Column(nullable = false)
	private String documentationUrl;

	@Column(nullable = false)
	private String version;

	@Lob
	@ToString.Exclude
	private String descriptionImage;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Status status = Status.NOT_PUBLISHED;

	@Column
	private Boolean displayContactUrl;

	@Column
	private String contactUrl;

	@ManyToOne
	private BundleGroup bundleGroup;

	@ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
	@JoinTable(name = "bundle_versions",
			joinColumns = @JoinColumn(name = "bundle_group_version_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "bundle_id", referencedColumnName = "id"))
	private Set<Bundle> bundles = new HashSet<>();

	@UpdateTimestamp
	private LocalDateTime lastUpdated;

	public enum Status {
		NOT_PUBLISHED, PUBLISHED, PUBLISH_REQ, DELETE_REQ, DELETED, ARCHIVE
	}
}
