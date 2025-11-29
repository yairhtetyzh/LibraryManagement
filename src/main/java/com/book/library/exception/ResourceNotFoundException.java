package com.book.library.exception;

public class ResourceNotFoundException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 6519380247592161614L;

	public ResourceNotFoundException(String message) {
        super(message);
    }
}