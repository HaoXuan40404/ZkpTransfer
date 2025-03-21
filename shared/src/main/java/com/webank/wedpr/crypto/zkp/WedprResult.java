package com.webank.wedpr.crypto.zkp;

public class WedprResult {
    public String wedprErrorMessage;

    /** Checks whether any error occurred. */
    public boolean hasError() {
        return wedprErrorMessage != null;
    }
}
