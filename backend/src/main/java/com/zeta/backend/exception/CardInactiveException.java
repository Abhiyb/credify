package com.zeta.backend.exception;

/**
 * Exception thrown when a card is not in an active state for transactions.
 */
public class CardInactiveException extends BadRequestException {
  public CardInactiveException(Long cardId) {
    super("Card with ID " + cardId + " is not active");
  }
}