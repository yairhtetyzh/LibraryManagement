package com.book.library.exception;

public class ResourceAlreadyExistsException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -6887847767908359978L;

	public ResourceAlreadyExistsException(String message) {
        super(message);
    }
}