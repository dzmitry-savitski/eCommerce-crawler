package com.upwork.dsavitski.crawler.exceptions;

public class CrawlerException extends RuntimeException {
    public CrawlerException() {
    }

    public CrawlerException(String message) {
        super(message);
    }

    public CrawlerException(String message, Throwable cause) {
        super(message, cause);
    }

    public CrawlerException(Throwable cause) {
        super(cause);
    }
}
