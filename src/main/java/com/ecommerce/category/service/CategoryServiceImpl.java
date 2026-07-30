//package com.ecommerce.category.service;
//
//import com.ecommerce.category.dto.CategoryRequest;
//import com.ecommerce.category.dto.CategoryResponse;
//import com.ecommerce.category.entity.Category;
//import com.ecommerce.category.mapper.CategoryMapper;
//import com.ecommerce.category.repository.CategoryRepository;
//import com.ecommerce.common.exception.DuplicateResourceException;
//import com.ecommerce.common.exception.ResourceNotFoundException;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class CategoryServiceImpl implements CategoryService {
//
//    private final CategoryRepository categoryRepository;
//
//    @Override
//    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
//        if (categoryRepository.existsByName(categoryRequest.getName())) {
//            throw new DuplicateResourceException("Category", "name", categoryRequest.getName());
//        }
//
//        Category category = CategoryMapper.toEntity(categoryRequest);
//        Category savedCategory = categoryRepository.save(category);
//        return CategoryMapper.toResponse(savedCategory);
//    }
//
//    @Override
//    public CategoryResponse getCategoryById(Long id) {
//        Category category = categoryRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
//        return CategoryMapper.toResponse(category);
//    }
//
//    @Override
//    public List<CategoryResponse> getAllCategories() {
//        List<Category> categories = categoryRepository.findAll();
//        return categories.stream()
//                .map(CategoryMapper::toResponse)
//                .toList();
//    }
//
//    @Override
//    public CategoryResponse updateCategory(Long id, CategoryRequest categoryRequest) {
//        Category category = categoryRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
//
//        if (!category.getName().equals(categoryRequest.getName()) &&
//                categoryRepository.existsByName(categoryRequest.getName())) {
//            throw new DuplicateResourceException("Category", "name", categoryRequest.getName());
//        }
//
//        category.setName(categoryRequest.getName());
//        category.setDescription(categoryRequest.getDescription());
//
//        Category updatedCategory = categoryRepository.save(category);
//        return CategoryMapper.toResponse(updatedCategory);
//    }
//
//    @Override
//    public void deleteCategory(Long id) {
//        Category category = categoryRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
//        categoryRepository.delete(category);
//    }
//}
