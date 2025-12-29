package org.app.credit.controllers;

import jakarta.validation.Valid;
import org.app.credit.entities.dtos.CreditRequestCreatedDto;
import org.app.credit.entities.dtos.CreditRequestEvaluateDto;
import org.app.credit.entities.dtos.CreditRequestResponseDto;
import org.app.credit.services.CreditRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/requests")
public class CreditRequestController {
    @Autowired
    private CreditRequestService creditRequestService;

    @GetMapping("/evaluate")
    public ResponseEntity<List<CreditRequestResponseDto>> findAll() {
        return ResponseEntity.ok(creditRequestService.findAll());
    }

    @GetMapping("mine")
    public ResponseEntity<List<CreditRequestResponseDto>> findMine() {
        return ResponseEntity.ok(creditRequestService.findByUsername());
    }

    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody CreditRequestCreatedDto creditRequest) {
        return ResponseEntity.ok(creditRequestService.save(creditRequest));
    }

    @PutMapping("/evaluate/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody CreditRequestEvaluateDto creditRequest, @PathVariable Long id) {
        return ResponseEntity.ok(creditRequestService.update(id, creditRequest));
    }


}
