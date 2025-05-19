package com.zeta.backend.enums;

public enum InstallmentPlan {
    THREE(3), SIX(6), NINE(9), TWELVE(12);

    private final int months;

    InstallmentPlan(int months) {
        this.months = months;
    }

    public int getMonths() {
        return months;
    }

    public static boolean isValid(int value) {
        for (InstallmentPlan plan : values()) {
            if (plan.months == value) return true;
        }
        return false;
    }
}
