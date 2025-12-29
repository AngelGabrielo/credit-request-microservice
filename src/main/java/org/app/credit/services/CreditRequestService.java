package org.app.credit.services;

import org.app.credit.entities.CreditRequest;
import org.app.credit.entities.dtos.CreditRequestCreatedDto;
import org.app.credit.entities.dtos.CreditRequestEvaluateDto;
import org.app.credit.entities.dtos.CreditRequestResponseDto;

import java.util.List;
import java.util.Optional;

public interface CreditRequestService {
    List<CreditRequestResponseDto> findByUsername();
    List<CreditRequestResponseDto> findAll();
    CreditRequestResponseDto save(CreditRequestCreatedDto creditRequest);
    CreditRequestResponseDto update(Long id,  CreditRequestEvaluateDto creditRequest);
}
