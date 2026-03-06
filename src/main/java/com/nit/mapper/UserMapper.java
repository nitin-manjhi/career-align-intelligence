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
    User toEntity(SignupRequest signupRequest);

    UserProfileResponse toUserProfileResponse(User user);

}
