package com.zeta.backend.repository;

import com.zeta.backend.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByApplicationUserUserId(Long userId);
}
