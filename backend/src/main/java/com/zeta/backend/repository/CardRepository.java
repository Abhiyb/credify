package com.zeta.backend.repository;

import com.zeta.backend.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByApplicationUserUserId(Long userId);
    Optional<Card> findByCardNumberAndCvvAndExpiryMonthAndExpiryYearAndUserEmail(
            String cardNumber,
            String cvv,
            String expiryMonth,
            String expiryYear,
            String userEmail
    );
}
