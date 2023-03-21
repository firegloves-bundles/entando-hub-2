package com.entando.hub.catalog.service.mapper;

import com.entando.hub.catalog.service.mapper.inclusion.BaseMapperMethods;

import java.util.List;

public interface BaseStandardMapper<E, D> extends BaseMapperMethods {

  E toEntity(D dto);
  D toDto(E entity);

  List<E> toEntity(List<D> dtoList);
  List<D> toDto(List<E> entityList);

}
