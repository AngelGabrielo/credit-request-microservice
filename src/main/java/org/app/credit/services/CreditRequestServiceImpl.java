package org.app.credit.services;

import lombok.RequiredArgsConstructor;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CreditRequestServiceImpl implements CreditRequestService {

    private final CreditRequestMapper mapper;
    private final AuthenticationFacade authenticationFacade;
    private final CreditRequestRepository creditRequestRepository;
    private final UserRepository userRepository;


    @Override
    @Transactional(readOnly = true)
    public List<CreditRequestResponseDto> findByUsername() {
        return mapper.toDtoList(creditRequestRepository.findByUserUsername(getUsername()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CreditRequestResponseDto> findAll() {
        return mapper.toDtoList(creditRequestRepository.findAll());
    }

    @Override
    @Transactional
    public CreditRequestResponseDto save(CreditRequestCreatedDto creditRequestDto) {

        CreditRequest creditRequest = mapper.toEntity(creditRequestDto);

        User user = userRepository.findByUsername(getUsername())
                .orElseThrow(() -> new BusinessException("User not found"));

        creditRequest.setState(RequestStateEnum.PENDING);
        creditRequest.setUser(user);
        creditRequest.setDate(LocalDateTime.now());
        return mapper.toDto(creditRequestRepository.save(creditRequest));
    }

    @Override
    @Transactional
    public CreditRequestResponseDto update(Long id, CreditRequestEvaluateDto creditRequestDto) {

        CreditRequest creditRequestDB = creditRequestRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Credit request not found"));

        creditRequestDB.setState(creditRequestDto.state());

        return mapper.toDto(creditRequestRepository.save(creditRequestDB));

    }

    private String getUsername(){
        return authenticationFacade.getAuthenticatedUsername();
    }
}
