package org.app.credit.security.facade;
import static org.mockito.Mockito.*;


import org.app.credit.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationFacadeImplTest {

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;
    
    @InjectMocks
    private AuthenticationFacadeImpl authenticationFacade;
    

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("getAuthenticatedUsername: Debería retornar el nombre si está autenticado")
    void shouldReturnUsername_WhenAuthenticated() {
        //ARRANGE
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("username");
        
        //ACT
        String username = authenticationFacade.getAuthenticatedUsername();
        
        //ASSERT
        assertEquals("username", username);
    }

    @Test
    @DisplayName("getAuthenticatedUsername: Debería lanzar error si no hay autenticación")
    void shouldThrowException_WhenNotAuthenticated() {
        //ARRANGE
        when(securityContext.getAuthentication()).thenReturn(null);

        //ACT & ASSERT
        assertThrows(ResourceNotFoundException.class, () ->  authenticationFacade
                .getAuthenticatedUsername());
    }
}