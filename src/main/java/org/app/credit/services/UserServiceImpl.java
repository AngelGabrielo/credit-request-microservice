package org.app.credit.services;

import lombok.RequiredArgsConstructor;
import org.app.credit.entities.Role;
import org.app.credit.entities.User;
import org.app.credit.entities.dtos.UserRegisterDto;
import org.app.credit.entities.dtos.UserResponseDto;
import org.app.credit.entities.mappers.UserMapper;
import org.app.credit.exceptions.ResourceNotFoundException;
import org.app.credit.repositories.RoleRepository;
import org.app.credit.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;


    @Override
    @Transactional
    public UserResponseDto save(UserRegisterDto userDto) {
        User user = userMapper.toEntity(userDto);

        String roleName = userDto.analyst() ? "ROLE_ANALYST" : "ROLE_CLIENT";

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role " + roleName + " it's not found."));

        user.setRoles(new ArrayList<>(List.of(role)));

        user.setEnabled(true);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userMapper.toDto(userRepository.save(user));
    }
}
