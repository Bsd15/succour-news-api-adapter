package com.stackroute.succour.newsapiadapter.exceptions;

/**
 * Thrown when Newsapi returns empty articles.
 */
public class EmptyArticlesException extends Exception {
    public EmptyArticlesException(String message) {
        super(message);
    }
}
