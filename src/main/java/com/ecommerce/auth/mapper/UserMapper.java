package com.ecommerce.auth.mapper;

import com.ecommerce.auth.dto.Requests.RegisterRequest;
import com.ecommerce.auth.dto.Responses.RegisterResponse;
import com.ecommerce.auth.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    User toEntity(RegisterRequest registerRequest);
    RegisterResponse toRegisterResponse(User user);
}
