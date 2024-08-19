package org.example.demo.enums;

public enum Status {
    INCOMPLETE(0),
    COMPLETE(1);

    private final int code;

    Status(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
