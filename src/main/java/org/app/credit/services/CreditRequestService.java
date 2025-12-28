package org.app.credit.services;

import org.app.credit.entities.CreditRequest;

import java.util.List;
import java.util.Optional;

public interface CreditRequestService {
    List<CreditRequest> findByUsername();
    List<CreditRequest> findAll();
    CreditRequest save(CreditRequest creditRequest);
    Optional<CreditRequest> update(Long id,  CreditRequest creditRequest);
}
