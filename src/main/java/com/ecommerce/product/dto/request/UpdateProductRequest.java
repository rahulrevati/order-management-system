package com.ecommerce.product.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(max = 150)
    private String name;

    @Size(max = 1000)
    private String description;

    @NotBlank(message = "SKU is required")
    @Size(max = 50)
    private String sku;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal price;

    @NotNull
    @Min(0)
    private Integer stockQuantity;

    @Size(max = 500)
    private String imageUrl;

    @NotNull
    private Long categoryId;

    @NotNull
    private Boolean active;
}