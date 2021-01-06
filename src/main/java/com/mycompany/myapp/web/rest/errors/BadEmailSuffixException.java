package com.mycompany.myapp.web.rest.errors;


public class BadEmailSuffixException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public BadEmailSuffixException(String message) {
        super(ErrorConstants.INVALID_EMAIL_SUFFIX, "Incorrect email suffix", "userManagement", message);
    }
}
