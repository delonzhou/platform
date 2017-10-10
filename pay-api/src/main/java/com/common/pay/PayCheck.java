package com.common.pay;

public class PayCheck {

    public static void requireNonBlank(String cs, String message) {
        if (isBlank(cs)) {
            throw new NullPointerException(message);
        }
    }

    public static boolean isBlank(String cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

}
