package org.designer.tongrong_property_company_2nd.common;

public class AccessDeniedException extends RuntimeException {

    private final String message;

    public AccessDeniedException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
