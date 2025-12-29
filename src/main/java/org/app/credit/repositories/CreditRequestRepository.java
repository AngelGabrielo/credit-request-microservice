package org.app.credit.repositories;

import org.app.credit.entities.CreditRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditRequestRepository extends JpaRepository<CreditRequest,Long> {
    List<CreditRequest> findByUserUsername(String username);
}
