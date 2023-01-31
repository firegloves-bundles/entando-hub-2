package com.entando.hub.catalog.persistence.entity;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import org.hibernate.annotations.UpdateTimestamp;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BaseEntity {
	
	@NotNull
    private Integer createdBy;
	
	@NotNull
    private Integer updatedby;
	
	
	@NotNull
	@UpdateTimestamp
    private LocalDateTime lastUpdatedDate;
	
	@NotNull
	@UpdateTimestamp
    private LocalDateTime createdDate;
}
