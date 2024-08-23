package com.walletgenerator.exception;

public class WalletGenerationException extends RuntimeException {

    public WalletGenerationException(String message) {
        super(message);
    }

    public WalletGenerationException(String message, Exception e) {
        super(message, e);
    }

}
