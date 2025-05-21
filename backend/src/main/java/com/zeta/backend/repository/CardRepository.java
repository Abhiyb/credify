package com.zeta.backend.repository;

import com.zeta.backend.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {
    Card findByApplicationUserUserId(Long userId);
}
