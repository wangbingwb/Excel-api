package com.wb.excel.api.exception;

/**
 * Created on 2014/10/12.
 *
 * @author
 * @version 0.1.0
 */
public class ColumnNameNotExistException extends RuntimeException {
    private static final long serialVersionUID = 234122996006212387L;

    /**
     * Constructs a new runtime exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public ColumnNameNotExistException() {
        super();
    }

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public ColumnNameNotExistException(String message) {
        super(message);
    }
}
