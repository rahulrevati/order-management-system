package com.ecommerce.category.service;

import com.ecommerce.category.dto.request.CreateCategoryRequest;
import com.ecommerce.category.dto.request.UpdateCategoryRequest;
import com.ecommerce.category.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse createCategory(CreateCategoryRequest categoryRequest);

    CategoryResponse updateCategory(Long id, UpdateCategoryRequest request);

    CategoryResponse getCategoryById(Long id);

    List<CategoryResponse> getAllCategories();


    void deleteCategory(Long id);
}
