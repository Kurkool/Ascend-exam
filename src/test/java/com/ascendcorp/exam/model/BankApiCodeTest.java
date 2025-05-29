package com.ascendcorp.exam.model;

import org.junit.Test;
import static junit.framework.TestCase.assertEquals;

public class BankApiCodeTest {

    @Test
    public void testFromString_Approved() {
        assertEquals(BankApiCode.APPROVED, BankApiCode.fromString("approved"));
    }

    @Test
    public void testFromString_InvalidData() {
        assertEquals(BankApiCode.INVALID_DATA, BankApiCode.fromString("invalid_data"));
    }

    @Test
    public void testFromString_TransactionError() {
        assertEquals(BankApiCode.TRANSACTION_ERROR, BankApiCode.fromString("transaction_error"));
    }

    @Test
    public void testFromString_Unknown() {
        assertEquals(BankApiCode.UNKNOWN, BankApiCode.fromString("unknown"));
    }

    @Test
    public void testFromString_UnsupportedCode() {
        assertEquals(BankApiCode.UNSUPPORTED_CODE, BankApiCode.fromString("not_a_code"));
    }

    @Test
    public void testFromString_CaseInsensitive() {
        assertEquals(BankApiCode.APPROVED, BankApiCode.fromString("ApPrOvEd"));
    }

    @Test
    public void testFromString_Null() {
        assertEquals(BankApiCode.UNSUPPORTED_CODE, BankApiCode.fromString(null));
    }
}
