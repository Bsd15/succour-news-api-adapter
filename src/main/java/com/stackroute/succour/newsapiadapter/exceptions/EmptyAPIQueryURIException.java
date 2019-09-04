package com.stackroute.succour.newsapiadapter.exceptions;

/**
 * Thrown when API query is null or empty.
 */
public class EmptyAPIQueryURIException extends Exception {
    private static final String message = "Empty API Query is given.";

    public EmptyAPIQueryURIException() {
        super(message);
    }
}
