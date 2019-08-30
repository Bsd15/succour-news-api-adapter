package com.stackroute.succour.newsapiadapter.exceptions;

public class EmptyAPIQueryURIException extends Exception {
    private static final String message = "Empty API Query is given.";

    public EmptyAPIQueryURIException() {
        super(message);
    }
}
