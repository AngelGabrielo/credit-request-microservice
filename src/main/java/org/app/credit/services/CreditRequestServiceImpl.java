package org.app.credit.services;

import org.app.credit.entities.CreditRequest;
import org.app.credit.entities.User;
import org.app.credit.entities.dtos.CreditRequestCreatedDto;
import org.app.credit.entities.dtos.CreditRequestResponseDto;
import org.app.credit.entities.enums.RequestStateEnum;
import org.app.credit.entities.mappers.CreditRequestMapper;
import org.app.credit.exceptions.BusinessException;
import org.app.credit.exceptions.ResourceNotFoundException;
import org.app.credit.repositories.CreditRequestRepository;
import org.app.credit.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CreditRequestServiceImpl implements CreditRequestService {

    private static final BigDecimal MAX_AMOUNT = new BigDecimal("50000");

    @Autowired
    private final CreditRequestMapper mapper;

    @Autowired
    private CreditRequestRepository creditRequestRepository;

    @Autowired
    private UserRepository userRepository;

    public CreditRequestServiceImpl(CreditRequestMapper mapper) {
        this.mapper = mapper;
    }


    @Override
    public List<CreditRequestResponseDto> findByUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResourceNotFoundException("Username not found");
        }
        String username = auth.getName();
        return mapper.toDtoList(creditRequestRepository.findByUserUsername(username));
    }

    @Override
    public List<CreditRequestResponseDto> findAll() {
        return mapper.toDtoList(creditRequestRepository.findAll());
    }

    @Override
    public CreditRequestResponseDto save(CreditRequestCreatedDto creditRequestDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResourceNotFoundException("Username not found");
        }
        String username = auth.getName();

        CreditRequest creditRequest = mapper.toEntity(creditRequestDto);

        User user = userRepository.findByUsername(username)
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
}
