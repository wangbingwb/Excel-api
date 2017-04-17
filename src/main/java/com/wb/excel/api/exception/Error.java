package com.wb.excel.api.exception;

import java.io.Serializable;

public class Error implements Serializable {

    private static final long serialVersionUID = 3L;

    private String code;

    private ErrorType type;

    private String message;

    public Error() {
    }

    public Error(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public Error(ErrorType type, String message) {
        this.type = type;
        this.code = type.toString();
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ErrorType getType() {
        return type;
    }

    public void setType(ErrorType type) {
        this.type = type;
    }
}
