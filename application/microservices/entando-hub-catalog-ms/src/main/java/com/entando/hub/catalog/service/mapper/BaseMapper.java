package com.entando.hub.catalog.service.mapper;

import java.util.List;

public interface BaseMapper<E, D> {

  E toEntity(D dto);
  D toDto(E entity);

//  Optional<E> toEntity(Optional<D> dto);
//  Optional<D> toDto(Optional<E> entity);
  List<E> toEntity(List<D> dtoList);
  List<D> toDto(List<E> entityList);

}
