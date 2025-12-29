package org.app.credit.services;

import lombok.RequiredArgsConstructor;
import org.app.credit.entities.Role;
import org.app.credit.entities.User;
import org.app.credit.entities.dtos.UserRegisterDto;
import org.app.credit.entities.dtos.UserResponseDto;
import org.app.credit.entities.mappers.UserMapper;
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

        Optional<Role> optionalRoleUser = roleRepository.findByName("ROLE_CLIENT");
        List<Role> roles = new ArrayList<>();

        optionalRoleUser.ifPresent(roles::add);

        if(user.isAnalyst()){
            Optional<Role> optionalRoleAdmin = roleRepository.findByName("ROLE_ANALYST");
            optionalRoleAdmin.ifPresent(roles::add);
        }

        user.setRoles(roles);
        user.setEnabled(true);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userMapper.toDto(userRepository.save(user));
    }
}
