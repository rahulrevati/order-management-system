package com.ecommerce.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class BulkProductImportResponse {
    private final int importedCount;
    private final int rejectedCount;
    private final List<String> errors;
}
