package com.foodsupply.management.auth.service;

import com.foodsupply.management.auth.dto.AuthResponse;
import com.foodsupply.management.auth.dto.LoginRequest;
import com.foodsupply.management.auth.dto.RegisterRequest;
import com.foodsupply.management.auth.entity.AppUser;
import com.foodsupply.management.auth.entity.Role;
import com.foodsupply.management.auth.repository.AppUserRepository;
import com.foodsupply.management.auth.repository.RoleRepository;
import com.foodsupply.management.auth.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AppUserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(AppUserRepository userRepository, RoleRepository roleRepository,
                        PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken");
        }

        String roleName = request.getRoleName() != null ? request.getRoleName() : "VIEWER";
        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

        AppUser user = new AppUser();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(role);

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername(), role.getRoleName());
        return new AuthResponse(token, user.getUsername(), role.getRoleName());
    }

    public AuthResponse login(LoginRequest request) {
        AppUser user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid username or password");
        }

        if (!user.isActive()) {
            throw new RuntimeException("Account is disabled");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().getRoleName());
        return new AuthResponse(token, user.getUsername(), user.getRole().getRoleName());
    }
}
