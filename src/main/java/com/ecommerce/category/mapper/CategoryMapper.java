//package com.ecommerce.category.mapper;
//
//import com.ecommerce.category.dto.CategoryRequest;
//import com.ecommerce.category.dto.CategoryResponse;
//import com.ecommerce.category.entity.Category;
//
//public class CategoryMapper {
//
//    public static Category toEntity(CategoryRequest categoryRequest) {
//        return Category.builder()
//                .name(categoryRequest.getName())
//                .description(categoryRequest.getDescription())
//                .build();
//    }
//
//    public static CategoryResponse toResponse(Category category) {
//        return CategoryResponse.builder()
//                .id(category.getId())
//                .name(category.getName())
//                .description(category.getDescription())
//                .createdAt(category.getCreatedAt())
//                .updatedAt(category.getUpdatedAt())
//                .build();
//    }
//}
