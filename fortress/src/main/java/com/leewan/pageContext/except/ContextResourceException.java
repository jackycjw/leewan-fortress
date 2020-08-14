package com.leewan.pageContext.except;

public class ContextResourceException extends RuntimeException {

	public ContextResourceException(String msg) {
		super(msg);
	}
	
	public ContextResourceException(String message, Throwable cause) {
		super(message, cause);
	}
}
