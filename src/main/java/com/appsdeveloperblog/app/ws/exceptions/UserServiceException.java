package com.appsdeveloperblog.app.ws.exceptions;

import java.io.Serializable;

public class UserServiceException extends RuntimeException implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1594180208015507087L;

	public UserServiceException(String message) {
		super(message);
	}

}
