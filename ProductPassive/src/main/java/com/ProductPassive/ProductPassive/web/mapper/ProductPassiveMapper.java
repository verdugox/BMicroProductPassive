package com.ProductPassive.ProductPassive.web.mapper;

import com.ProductPassive.ProductPassive.entity.ProductPassive;
import com.ProductPassive.ProductPassive.web.model.ProductPassiveModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductPassiveMapper {

    ProductPassive modelToEntity (ProductPassiveModel model);
    ProductPassiveModel entityToModel(ProductPassive event);

    @Mapping(target = "id", ignore = true)
    void update(@MappingTarget ProductPassive entity, ProductPassive updateEntity);

}
