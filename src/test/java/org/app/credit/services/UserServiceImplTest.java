package org.app.credit.services;

import org.app.credit.entities.Role;
import org.app.credit.entities.User;
import org.app.credit.entities.dtos.UserRegisterDto;
import org.app.credit.entities.dtos.UserResponseDto;
import org.app.credit.entities.mappers.UserMapper;
import org.app.credit.repositories.RoleRepository;
import org.app.credit.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Save: Debería guardar usuario encriptado y con rol CLIENT por defecto")
    void save_ShouldRegisterUser_WhenDataIsValid() {
        // --- ARRANGE ---
        UserRegisterDto registerDto = new UserRegisterDto("angel", "123456", false);

        User userEntity = new User();
        userEntity.setUsername("angel");
        userEntity.setPassword("123456"); // Password plano inicial

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("angel");
        savedUser.setPassword("encodedPassword123"); // Password ya hasheado
        savedUser.setRoles(List.of(new Role(1L, "ROLE_CLIENT", null)));

        UserResponseDto responseDto = new UserResponseDto(1L, "angel", true, List.of("ROLE_CLIENT"));

        // Mocks
        when(userMapper.toEntity(registerDto)).thenReturn(userEntity);
        when(roleRepository.findByName("ROLE_CLIENT")).thenReturn(Optional.of(new Role(1L, "ROLE_CLIENT", null)));
        when(passwordEncoder.encode("123456")).thenReturn("encodedPassword123"); // Simulamos el hash
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(responseDto);

        // --- ACT ---
        UserResponseDto result = userService.save(registerDto);

        // --- ASSERT ---
        assertNotNull(result);
        assertEquals("angel", result.username());
        assertTrue(result.roles().contains("ROLE_CLIENT"));

        // VERIFICACIONES CLAVE DE SEGURIDAD
        verify(passwordEncoder).encode("123456");
        verify(userRepository).save(argThat(user ->
                user.getPassword().equals("encodedPassword123")
                        && user.isEnabled()
        ));
    }

    @Test
    @DisplayName("Save: Debería asignar rol ANALYST si se solicita")
    void save_ShouldAssignAnalystRole_WhenRequested() {
        // --- ARRANGE ---
        UserRegisterDto registerDto = new UserRegisterDto("admin", "123456", true);

        User userEntity = new User();
        userEntity.setAnalyst(true);

        when(userMapper.toEntity(registerDto)).thenReturn(userEntity);
        // Mockeamos que existen ambos roles
        when(roleRepository.findByName("ROLE_CLIENT")).thenReturn(Optional.of(new Role(1L, "ROLE_CLIENT", null)));
        when(roleRepository.findByName("ROLE_ANALYST")).thenReturn(Optional.of(new Role(2L, "ROLE_ANALYST", null)));
        when(passwordEncoder.encode(any())).thenReturn("hash");
        when(userRepository.save(any())).thenReturn(new User());
        when(userMapper.toDto(any())).thenReturn(null);

        // --- ACT ---
        userService.save(registerDto);

        // --- ASSERT ---
        // Verificamos que al guardar, la lista de roles tenga 2 elementos
        verify(userRepository).save(argThat(user -> user.getRoles().size() == 2));
    }

}