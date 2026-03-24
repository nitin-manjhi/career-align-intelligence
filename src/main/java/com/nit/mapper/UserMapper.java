package com.nit.mapper;

import com.nit.dto.auth.SignupRequest;
import com.nit.dto.auth.UserProfileResponse;
import com.nit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "usageLimit", ignore = true)
    @Mapping(target = "analysisCount", ignore = true)
    @Mapping(target = "generationCount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "premiumActive", ignore = true)
    @Mapping(target = "premiumUsageLimit", ignore = true)
    @Mapping(target = "premiumUsageCount", ignore = true)
    @Mapping(target = "suspended", ignore = true)
    User toEntity(SignupRequest signupRequest);

    @Mapping(target = "premiumActive", expression = "java(user.getRole() == com.nit.entity.Role.ADMIN || user.isPremiumActive())")
    UserProfileResponse toUserProfileResponse(User user);

}
