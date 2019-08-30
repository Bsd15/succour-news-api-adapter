package com.stackroute.succour.newsapiadapter.exceptions;

public class EmptyQueryParamsException extends Exception {
    private static final String message = "Empty Query parameters. Please give at least one query param.";

    public EmptyQueryParamsException() {
        super(message);
    }
}
