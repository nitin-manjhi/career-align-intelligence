package com.nit.mapper;


import com.nit.dto.auth.SignupRequest;
import com.nit.dto.auth.UserProfileResponse;
import com.nit.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(SignupRequest signupRequest);

    UserProfileResponse toUserProfileResponse(User user);

}
