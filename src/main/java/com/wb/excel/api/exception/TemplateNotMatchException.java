package com.wb.excel.api.exception;

/**
 * Created on 2014/10/12.
 *
 * @author
 * @version 0.1.0
 */
public class TemplateNotMatchException extends Exception {
    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public TemplateNotMatchException() {
    }

    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public TemplateNotMatchException(String message) {
        super(message);
    }
}
