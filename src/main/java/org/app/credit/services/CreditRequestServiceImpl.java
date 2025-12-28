package org.app.credit.services;

import org.app.credit.entities.CreditRequest;
import org.app.credit.entities.User;
import org.app.credit.entities.enums.RequestStateEnum;
import org.app.credit.exceptions.BusinessException;
import org.app.credit.exceptions.ResourceNotFoundException;
import org.app.credit.repositories.CreditRequestRepository;
import org.app.credit.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CreditRequestServiceImpl implements CreditRequestService {

    private static final BigDecimal MAX_AMOUNT = new BigDecimal("50000");

    @Autowired
    private CreditRequestRepository creditRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<CreditRequest> findByUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResourceNotFoundException("Username not found");
        }
        String username = auth.getName();
        return creditRequestRepository.findByUserUsername(username);
    }

    @Override
    public List<CreditRequest> findAll() {
        return creditRequestRepository.findAll();
    }

    @Override
    public CreditRequest save(CreditRequest creditRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResourceNotFoundException("Username not found");
        }
        String username = auth.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("User not found"));


        if (creditRequest.getAmount() == null) {
            throw new BusinessException("Amount is required");
        }

        if (creditRequest.getAmount().compareTo(MAX_AMOUNT) > 0) {
            throw new BusinessException("Maximum allowed amount is 50000");
        }

        if (creditRequest.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Amount cannot be negative or zero");
        }

        creditRequest.setState(RequestStateEnum.PENDING);
        creditRequest.setUser(user);
        return creditRequestRepository.save(creditRequest);
    }

    @Override
    public Optional<CreditRequest> update(Long id, CreditRequest creditRequest) {
        Optional<CreditRequest> optionalCreditRequest = creditRequestRepository.findById(id);

        if (optionalCreditRequest.isPresent()) {

            CreditRequest creditRequestDB = optionalCreditRequest.get();

            creditRequestDB.setState(creditRequest.getState());

            return Optional.of(creditRequestRepository.save(creditRequestDB));
        }

        return optionalCreditRequest;
    }
}
