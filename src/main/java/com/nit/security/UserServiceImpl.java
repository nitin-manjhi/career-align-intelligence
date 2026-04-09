package com.nit.security;

import com.nit.dto.auth.UserProfileResponse;
import com.nit.exception.ResourceNotFoundException;
import com.nit.mapper.UserMapper;
import com.nit.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    UserRepository userRepository;
    AuthUtil authUtil;
    UserMapper userMapper;

    @Override
    public UserProfileResponse getProfile(Long userId) {
        Long id = (userId == null) ? authUtil.getCurrentUserId() : userId;
        return userRepository.findById(id)
                .map(userMapper::toUserProfileResponse)
                .orElseThrow(() -> new ResourceNotFoundException("User", id.toString()));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Try to find by username first, then by email
        return userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username or email: " + username));
    }
}
