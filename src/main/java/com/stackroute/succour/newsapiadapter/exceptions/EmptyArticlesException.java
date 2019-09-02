package com.stackroute.succour.newsapiadapter.exceptions;

/**
 * Thrown when Newsapi returns empty articles.
 */
public class EmptyArticlesException extends Exception {
    private static final String message = "No articles are present in the array.";
    public EmptyArticlesException() {
        super(message);
    }
}
