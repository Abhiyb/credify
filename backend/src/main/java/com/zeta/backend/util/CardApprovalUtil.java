package com.zeta.backend.util;

import com.zeta.backend.model.UserProfile;

public class CardApprovalUtil {

    public static String determineApplicationStatus(UserProfile profile, String cardType, double requestedLimit) {
        double income = profile.getAnnualIncome();
        double maxAllowedLimit = getMaxLimitForCardAndIncome(cardType, income);

        double ratio = requestedLimit / maxAllowedLimit;

        if (ratio <= 1.0) {
            return "APPROVED";
        } else if (ratio <= 1.2) {
            return "PENDING";
        } else {
            return "REJECTED";
        }
    }

    private static double getMaxLimitForCardAndIncome(String cardType, double income) {
        double baseMultiplier = switch (cardType.toUpperCase()) {
            case "VISA" -> 0.3;
            case "MASTERCARD" -> 0.5;
            case "AMERICANEXPRESS", "AMEX" -> 0.7;
            default -> 0.4;
        };

        return income * baseMultiplier;
    }

    public static double getMinLimitForCardAndIncome(String cardType, double income) {
        double baseMultiplier = switch (cardType.toUpperCase()) {
            case "VISA" -> 0.1;
            case "MASTERCARD" -> 0.2;
            case "AMERICANEXPRESS", "AMEX" -> 0.4;
            default -> 0.2;
        };
        return income * baseMultiplier;
    }
}
