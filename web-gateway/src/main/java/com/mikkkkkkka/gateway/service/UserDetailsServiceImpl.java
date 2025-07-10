package com.mikkkkkkka.gateway.service;

import com.mikkkkkkka.common.model.dto.UserDto;
import com.mikkkkkkka.gateway.dao.UserRepository;
import com.mikkkkkkka.gateway.model.UserDetailsImpl;
import com.mikkkkkkka.gateway.model.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username " + username + " not found"));
        return new UserDetailsImpl(user);
    }

    public UserDto registerUser(UserDto user) {
        User userEntity = User.builder()
                .id(null)
                .username(user.username())
                .password(passwordEncoder.encode(user.password()))
                .role(user.role())
                .ownerId(user.ownerId())
                .build();
        return userRepo.save(userEntity).toDto();
    }
}
