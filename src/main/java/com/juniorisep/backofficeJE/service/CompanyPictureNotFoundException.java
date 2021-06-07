package com.juniorisep.backofficeJE.service;

import com.juniorisep.backofficeJE.web.rest.errors.ErrorConstants;
import lombok.Getter;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Getter
public class CompanyPictureNotFoundException extends AbstractThrowableProblem {
    private static final long serialVersionUID = 1L;

    private final String fileName;

    public CompanyPictureNotFoundException(String defaultMessage, String fileName) {
        this(ErrorConstants.DEFAULT_TYPE, defaultMessage, fileName);
    }

    public CompanyPictureNotFoundException(URI type, String defaultMessage, String fileName) {
        super(type, defaultMessage, Status.NOT_FOUND, null, null, null, getAlertParameters(fileName));
        this.fileName = fileName;
    }

    private static Map<String, Object> getAlertParameters(String fileName) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("params", fileName);
        return parameters;
    }
}
