package com.ecommerce.category.service;

import com.ecommerce.category.dto.request.CreateCategoryRequest;
import com.ecommerce.category.dto.request.UpdateCategoryRequest;
import com.ecommerce.category.dto.response.CategoryResponse;
import com.ecommerce.category.entity.Category;
import com.ecommerce.category.mapper.CategoryMapper;
import com.ecommerce.category.repository.CategoryRepository;
import com.ecommerce.common.exception.ResourceAlreadyExistsException;
import com.ecommerce.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryResponse createCategory(CreateCategoryRequest request) {

        log.info("Creating category with name: {}", request.getName());

        if (categoryRepository.existsByName(request.getName())) {
            throw new ResourceAlreadyExistsException(
                    "Category already exists with name: " + request.getName());
        }

        Category category = categoryMapper.toEntity(request);

        Category savedCategory = categoryRepository.save(category);

        log.info("Category created successfully with id: {}", savedCategory.getId());

        return categoryMapper.toResponse(savedCategory);
    }

    @Override
    public CategoryResponse updateCategory(Long id, UpdateCategoryRequest request) {

        log.info("Updating category with id: {}", id);

        Category category = getCategoryEntity(id);

        if (!category.getName().equalsIgnoreCase(request.getName())
                && categoryRepository.existsByName(request.getName())) {

            throw new ResourceAlreadyExistsException(
                    "Category already exists with name: " + request.getName());
        }

        categoryMapper.updateCategoryFromRequest(request, category);

        Category updatedCategory = categoryRepository.save(category);

        log.info("Category updated successfully.");

        return categoryMapper.toResponse(updatedCategory);
    }

    @Override
    public void deleteCategory(Long id) {

        log.info("Deleting category with id: {}", id);

        Category category = getCategoryEntity(id);

        categoryRepository.delete(category);

        log.info("Category deleted successfully.");
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {

        Category category = getCategoryEntity(id);

        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {

        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    private Category getCategoryEntity(Long id) {

        return categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found with id: " + id));
    }
}