//package com.ecommerce.category.controller;
//
//import com.ecommerce.category.dto.CategoryRequest;
//import com.ecommerce.category.dto.CategoryResponse;
//import com.ecommerce.category.service.CategoryService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/categories")
//@RequiredArgsConstructor
//public class CategoryController {
//
//    private final CategoryService categoryService;
//
//    @PostMapping
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest categoryRequest) {
//        CategoryResponse categoryResponse = categoryService.createCategory(categoryRequest);
//        return new ResponseEntity<>(categoryResponse, HttpStatus.CREATED);
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
//        CategoryResponse categoryResponse = categoryService.getCategoryById(id);
//        return ResponseEntity.ok(categoryResponse);
//    }
//
//    @GetMapping
//    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
//        List<CategoryResponse> categories = categoryService.getAllCategories();
//        return ResponseEntity.ok(categories);
//    }
//
//    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<CategoryResponse> updateCategory(
//            @PathVariable Long id,
//            @Valid @RequestBody CategoryRequest categoryRequest) {
//        CategoryResponse categoryResponse = categoryService.updateCategory(id, categoryRequest);
//        return ResponseEntity.ok(categoryResponse);
//    }
//
//    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
//        categoryService.deleteCategory(id);
//        return ResponseEntity.noContent().build();
//    }
//}
