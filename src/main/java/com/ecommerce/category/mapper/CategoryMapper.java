package com.ecommerce.category.mapper;

import com.ecommerce.category.dto.request.CreateCategoryRequest;
import com.ecommerce.category.dto.request.UpdateCategoryRequest;
import com.ecommerce.category.dto.response.CategoryResponse;
import com.ecommerce.category.entity.Category;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {
    Category toEntity(CreateCategoryRequest categoryRequest);
    CategoryResponse toResponse(Category category);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCategoryFromRequest(UpdateCategoryRequest request,
                                   @MappingTarget Category category);
}
