package org.app.credit.services;

import lombok.RequiredArgsConstructor;
import org.app.credit.entities.CreditRequest;
import org.app.credit.entities.User;
import org.app.credit.entities.dtos.CreditRequestCreatedDto;
import org.app.credit.entities.dtos.CreditRequestResponseDto;
import org.app.credit.entities.enums.RequestStateEnum;
import org.app.credit.entities.mappers.CreditRequestMapper;
import org.app.credit.exceptions.BusinessException;
import org.app.credit.repositories.CreditRequestRepository;
import org.app.credit.repositories.UserRepository;
import org.app.credit.security.facade.AuthenticationFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CreditRequestServiceImpl implements CreditRequestService {

    private static final BigDecimal MAX_AMOUNT = new BigDecimal("50000");

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


        if (creditRequest.getAmount().compareTo(MAX_AMOUNT) > 0) {
            throw new BusinessException("Maximum allowed amount is 50000");
        }

        if (creditRequest.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Amount cannot be negative or zero");
        }

        creditRequest.setState(RequestStateEnum.PENDING);
        creditRequest.setUser(user);
        creditRequest.setDate(LocalDateTime.now());
        return mapper.toDto(creditRequestRepository.save(creditRequest));
    }

    @Override
    @Transactional
    public Optional<CreditRequestResponseDto> update(Long id, CreditRequestCreatedDto creditRequestDto) {
        CreditRequest creditRequest = mapper.toEntity(creditRequestDto);

        Optional<CreditRequest> optionalCreditRequest = creditRequestRepository.findById(id);

        if (optionalCreditRequest.isPresent()) {

            CreditRequest creditRequestDB = optionalCreditRequest.get();

            creditRequestDB.setState(creditRequest.getState());

            return Optional.of(mapper.toDto(creditRequestRepository.save(creditRequestDB)));
        }

        return Optional.empty();
    }

    private String getUsername(){
        return authenticationFacade.getAuthenticatedUsername();
    }
}
