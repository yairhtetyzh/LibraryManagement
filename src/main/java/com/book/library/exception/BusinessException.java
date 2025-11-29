package com.book.library.exception;

public class BusinessException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -1599489490500053743L;

	public BusinessException(String message) {
        super(message);
    }
}