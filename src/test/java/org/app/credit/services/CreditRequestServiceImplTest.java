package org.app.credit.services;

import org.app.credit.entities.CreditRequest;
import org.app.credit.entities.User;
import org.app.credit.entities.dtos.CreditRequestCreatedDto;
import org.app.credit.entities.dtos.CreditRequestEvaluateDto;
import org.app.credit.entities.dtos.CreditRequestResponseDto;
import org.app.credit.entities.enums.RequestStateEnum;
import org.app.credit.entities.mappers.CreditRequestMapper;
import org.app.credit.exceptions.BusinessException;
import org.app.credit.repositories.CreditRequestRepository;
import org.app.credit.repositories.UserRepository;
import org.app.credit.security.facade.AuthenticationFacade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class CreditRequestServiceImplTest {
    @Mock
    private CreditRequestRepository creditRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CreditRequestMapper mapper;

    @Mock
    private AuthenticationFacade authenticationFacade;

    @InjectMocks
    private CreditRequestServiceImpl service;

    @Test
    @DisplayName("Save: Debería guardar y retornar DTO cuando todo es válido")
    void save_ShouldReturnDto_WhenDataIsValid() {
        // --- ARRANGE (Preparar el escenario) ---
        String username = "angel";
        CreditRequestCreatedDto dto = new CreditRequestCreatedDto(
                new BigDecimal("5000"), 30, "Buying a Laptop"
        );

        User userMock = new User();
        userMock.setUsername(username);
        userMock.setId(1L);

        CreditRequest entityMock = new CreditRequest();
        entityMock.setAmount(new BigDecimal("5000"));

        CreditRequest savedEntityMock = new CreditRequest();
        savedEntityMock.setId(100L);
        savedEntityMock.setAmount(new BigDecimal("5000"));

        CreditRequestResponseDto responseDtoMock = new CreditRequestResponseDto(
                100L, new BigDecimal("5000"), 30, "Compra de Laptop", "PENDING", null
        );

        // Programamos el comportamiento de los Mocks
        when(authenticationFacade.getAuthenticatedUsername()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(userMock));
        when(mapper.toEntity(dto)).thenReturn(entityMock);
        when(creditRequestRepository.save(any(CreditRequest.class))).thenReturn(savedEntityMock);
        when(mapper.toDto(savedEntityMock)).thenReturn(responseDtoMock);

        // --- ACT (Ejecutar la acción) ---
        CreditRequestResponseDto result = service.save(dto);

        // --- ASSERT (Verificar resultados) ---
        assertNotNull(result);
        assertEquals(100L, result.id());
        assertEquals(new BigDecimal("5000"), result.amount());

        // Verificamos que el servicio haya llamado a los métodos correctos
        verify(authenticationFacade).getAuthenticatedUsername();
        verify(userRepository).findByUsername(username);
        verify(creditRequestRepository).save(entityMock);
    }

    @Test
    @DisplayName("Save: Debería lanzar BusinessException cuando el usuario no existe")
    void save_ShouldThrowBusinessException_WhenUserNotFound() {
        // ARRANGE
        String username = "angel";
        CreditRequestCreatedDto dto = new CreditRequestCreatedDto(
                new BigDecimal("5000"), 30, "Buying a Laptop"
        );

        //Necesitamos mockear el Facade porque tu servicio lo llama para buscar el username
        when(authenticationFacade.getAuthenticatedUsername()).thenReturn(username);
        //El escenario principal: El repositorio devuelve vacío
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // --- ACT & ASSERT (Ejecutar y Verificar al mismo tiempo) ---
        // La sintaxis correcta es: ClaseExcepción.class, () -> métodoQueFalla()
        assertThrows(BusinessException.class, () -> service.save(dto));

        // --- VERIFY (Confirmar qué pasó) ---
        verify(authenticationFacade).getAuthenticatedUsername();
        verify(userRepository).findByUsername(username);

        // ¡IMPORTANTE! Verificar que NUNCA se guardó nada en la BD de créditos
        // Esto asegura que la transacción se detuvo correctamente.
        verify(creditRequestRepository, never()).save(any());
    }


    @Test
    @DisplayName("FindByUsername: Debería retornar lista de DTOs del usuario autenticado")
    void findByUsername_ShouldReturnList_WhenUserIsAuthenticated() {
        // ARRANGE
        String username = "angel";
        List<CreditRequest> entities = List.of(new CreditRequest(), new CreditRequest());
        List<CreditRequestResponseDto> dtos = List.of(
                new CreditRequestResponseDto(1L, BigDecimal.TEN, 10, "R1", "PENDING", null),
                new CreditRequestResponseDto(2L, BigDecimal.ONE, 5, "R2", "APPROVED", null)
        );

        when(authenticationFacade.getAuthenticatedUsername()).thenReturn(username);
        when(creditRequestRepository.findByUserUsername(username)).thenReturn(entities);
        when(mapper.toDtoList(entities)).thenReturn(dtos);

        // ACT
        List<CreditRequestResponseDto> result = service.findByUsername();

        // ASSERT
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(creditRequestRepository).findByUserUsername(username);
    }

    @Test
    @DisplayName("FindByUsername: Debería retornar toda la lista de DTOs")
    void findAll_ShouldReturnList_WhenUserIsAuthenticated() {
        // ARRANGE
        List<CreditRequest> entities = List.of(new CreditRequest(), new CreditRequest(),  new CreditRequest(),  new CreditRequest());
        List<CreditRequestResponseDto> dtos = List.of(
                new CreditRequestResponseDto(1L, BigDecimal.TEN, 10, "R1", "PENDING", null),
                new CreditRequestResponseDto(2L, BigDecimal.ONE, 5, "R2", "APPROVED", null),
                new CreditRequestResponseDto(3L, BigDecimal.TEN, 10, "R3", "PENDING", null),
                new CreditRequestResponseDto(4L, BigDecimal.ONE, 5, "R3", "APPROVED", null)
        );

        when(creditRequestRepository.findAll()).thenReturn(entities);
        when(mapper.toDtoList(entities)).thenReturn(dtos);

        // ACT
        List<CreditRequestResponseDto> result = service.findAll();

        // ASSERT
        assertNotNull(result);
        assertEquals(4, result.size());
        verify(creditRequestRepository).findAll();
    }

    @Test
    @DisplayName("Update: Debería actualizar estado y retornar DTO")
    void update_ShouldUpdateState_WhenRequestExists() {
        // ARRANGE
        Long id = 1L;
        CreditRequestEvaluateDto evaluateDto = new CreditRequestEvaluateDto(RequestStateEnum.REJECTED);

        CreditRequest requestDB = new CreditRequest();
        requestDB.setId(id);
        requestDB.setState(RequestStateEnum.PENDING); // Estado original

        CreditRequest requestUpdated = new CreditRequest();
        requestUpdated.setId(id);
        requestUpdated.setState(RequestStateEnum.REJECTED); // Estado esperado

        // Simulamos que SÍ existe en BD
        when(creditRequestRepository.findById(id)).thenReturn(Optional.of(requestDB));
        // Simulamos el guardado (devuelve la entidad ya modificada)
        when(creditRequestRepository.save(any(CreditRequest.class))).thenReturn(requestUpdated);

        CreditRequestResponseDto responseDto = new CreditRequestResponseDto(
                id, BigDecimal.TEN, 10, "Rason", "REJECTED", null
        );
        when(mapper.toDto(any(CreditRequest.class))).thenReturn(responseDto);

        // ACT
        CreditRequestResponseDto result = service.update(id, evaluateDto);

        // ASSERT
        assertNotNull(result);
        assertEquals("REJECTED", result.state());

        // Verificamos que se llamó al setter con el nuevo estado
        assertEquals(RequestStateEnum.REJECTED, requestDB.getState());
    }

    @Test
    @DisplayName("Update: Debería lanzar BusinessException cuando el ID no existe")
    void update_ShouldThrowException_WhenRequestNotFound() {
        // ARRANGE
        Long nonExistentId = 999L;
        CreditRequestEvaluateDto evaluateDto = new CreditRequestEvaluateDto(RequestStateEnum.REJECTED);

        // Simulamos que el repositorio devuelve vacío
        when(creditRequestRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(BusinessException.class, () -> service.update(nonExistentId, evaluateDto));

        // VERIFY
        verify(creditRequestRepository).findById(nonExistentId);
        // Aseguramos que NUNCA intentó guardar nada
        verify(creditRequestRepository, never()).save(any());
    }

}