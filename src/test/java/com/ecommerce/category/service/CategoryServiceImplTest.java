package com.ecommerce.category.service;

import com.ecommerce.category.dto.request.CreateCategoryRequest;
import com.ecommerce.category.dto.request.UpdateCategoryRequest;
import com.ecommerce.category.dto.response.CategoryResponse;
import com.ecommerce.category.entity.Category;
import com.ecommerce.category.mapper.CategoryMapper;
import com.ecommerce.category.repository.CategoryRepository;
import com.ecommerce.common.exception.ResourceAlreadyExistsException;
import com.ecommerce.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;
    private CreateCategoryRequest createRequest;
    private UpdateCategoryRequest updateRequest;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Electronics");
        category.setDescription("Electronic items");

        createRequest = new CreateCategoryRequest();
        createRequest.setName("Electronics");
        createRequest.setDescription("Electronic items");

        updateRequest = new UpdateCategoryRequest();
        updateRequest.setName("Updated Electronics");
        updateRequest.setDescription("Updated description");
    }

    @Test
    void createCategory_Success() {
        when(categoryRepository.existsByName(anyString())).thenReturn(false);
        when(categoryMapper.toEntity(any(CreateCategoryRequest.class))).thenReturn(category);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.toResponse(any(Category.class))).thenReturn(new CategoryResponse());

        CategoryResponse response = categoryService.createCategory(createRequest);

        assertNotNull(response);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void createCategory_CategoryAlreadyExists() {
        when(categoryRepository.existsByName(anyString())).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> categoryService.createCategory(createRequest));

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void updateCategory_Success() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(categoryRepository.existsByName(anyString())).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.toResponse(any(Category.class))).thenReturn(new CategoryResponse());

        CategoryResponse response = categoryService.updateCategory(1L, updateRequest);

        assertNotNull(response);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void updateCategory_CategoryNotFound() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.updateCategory(1L, updateRequest));

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void updateCategory_NameAlreadyExists() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(categoryRepository.existsByName(anyString())).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> categoryService.updateCategory(1L, updateRequest));

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void deleteCategory_Success() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));

        categoryService.deleteCategory(1L);

        verify(categoryRepository).delete(any(Category.class));
    }

    @Test
    void deleteCategory_CategoryNotFound() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategory(1L));

        verify(categoryRepository, never()).delete(any(Category.class));
    }

    @Test
    void getCategoryById_Success() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(categoryMapper.toResponse(any(Category.class))).thenReturn(new CategoryResponse());

        CategoryResponse response = categoryService.getCategoryById(1L);

        assertNotNull(response);
        verify(categoryRepository).findById(1L);
    }

    @Test
    void getCategoryById_CategoryNotFound() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryById(1L));
    }

    @Test
    void getAllCategories_Success() {
        List<Category> categories = Arrays.asList(category);
        when(categoryRepository.findAll()).thenReturn(categories);
        when(categoryMapper.toResponse(any(Category.class))).thenReturn(new CategoryResponse());

        List<CategoryResponse> responses = categoryService.getAllCategories();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(categoryRepository).findAll();
    }
}
