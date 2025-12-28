package org.app.credit.controllers;

import jakarta.validation.Valid;
import org.app.credit.entities.CreditRequest;
import org.app.credit.services.CreditRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/requests")
public class CreditRequestController {
    @Autowired
    private CreditRequestService creditRequestService;

    @GetMapping("/evaluate")
    public ResponseEntity<List<CreditRequest>> findAll() {
        return ResponseEntity.ok(creditRequestService.findAll());
    }

    @GetMapping("mine")
    public ResponseEntity<List<CreditRequest>> findMine() {
        return ResponseEntity.ok(creditRequestService.findByUsername());
    }

    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody CreditRequest creditRequest) {
        return ResponseEntity.ok(creditRequestService.save(creditRequest));
    }

    @PutMapping("/evaluate/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody CreditRequest creditRequest, @PathVariable Long id) {
        Optional<CreditRequest> optionalCreditRequest = creditRequestService.update(id, creditRequest);

        if(optionalCreditRequest.isPresent()){
            return ResponseEntity.ok(optionalCreditRequest.get());
        }

        return ResponseEntity.notFound().build();
    }


}
