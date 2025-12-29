package org.app.credit.services;

import org.app.credit.entities.CreditRequest;
import org.app.credit.entities.dtos.CreditRequestCreatedDto;
import org.app.credit.entities.dtos.CreditRequestResponseDto;

import java.util.List;
import java.util.Optional;

public interface CreditRequestService {
    List<CreditRequestResponseDto> findByUsername();
    List<CreditRequestResponseDto> findAll();
    CreditRequestResponseDto save(CreditRequestCreatedDto creditRequest);
    Optional<CreditRequestResponseDto> update(Long id,  CreditRequestCreatedDto creditRequest);
}
