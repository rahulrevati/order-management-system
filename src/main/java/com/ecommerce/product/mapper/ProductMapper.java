//package com.ecommerce.product.mapper;
//
//import com.ecommerce.product.dto.ProductRequest;
//import com.ecommerce.product.dto.ProductResponse;
//import com.ecommerce.product.entity.Product;
//
//public class ProductMapper {
//
//    public static Product toEntity(ProductRequest productRequest) {
//        return Product.builder()
//                .name(productRequest.getName())
//                .description(productRequest.getDescription())
//                .price(productRequest.getPrice())
//                .stockQuantity(productRequest.getStockQuantity())
//                .imageUrl(productRequest.getImageUrl())
//                .build();
//    }
//
//    public static ProductResponse toResponse(Product product) {
//        return ProductResponse.builder()
//                .id(product.getId())
//                .name(product.getName())
//                .description(product.getDescription())
//                .price(product.getPrice())
//                .stockQuantity(product.getStockQuantity())
//                .imageUrl(product.getImageUrl())
//                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
//                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
//                .createdAt(product.getCreatedAt())
//                .updatedAt(product.getUpdatedAt())
//                .build();
//    }
//}
