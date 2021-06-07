package com.juniorisep.backofficeJE.web.rest.errors;


public class InvalidEmailSuffixException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public InvalidEmailSuffixException(String message) {
        super(ErrorConstants.INVALID_EMAIL_SUFFIX, "Incorrect email suffix", "userManagement", message);
    }
}
