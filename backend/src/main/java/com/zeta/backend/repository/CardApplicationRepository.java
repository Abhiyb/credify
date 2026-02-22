package com.zeta.backend.repository;

import com.zeta.backend.model.CardApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardApplicationRepository extends JpaRepository<CardApplication, Long> {

    boolean existsByUserUserIdAndCardTypeAndRequestedLimit(Long userId, String cardType, Double requestedLimit);

    List<CardApplication> findByUserUserId(Long userId);

}
