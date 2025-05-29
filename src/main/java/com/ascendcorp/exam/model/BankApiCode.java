package com.ascendcorp.exam.model;

import java.util.Arrays;

public enum BankApiCode {
    APPROVED("approved"),
    INVALID_DATA("invalid_data"),
    TRANSACTION_ERROR("transaction_error"),
    UNKNOWN("unknown"),
    UNSUPPORTED_CODE("unsupported_code");

    private final String code;

    BankApiCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static BankApiCode fromString(String text) {
        return Arrays.stream(values())
                .filter(apiCode -> apiCode.code.equalsIgnoreCase(text))
                .findFirst()
                .orElse(UNSUPPORTED_CODE); // Default to UNSUPPORTED_CODE if no match
    }
}