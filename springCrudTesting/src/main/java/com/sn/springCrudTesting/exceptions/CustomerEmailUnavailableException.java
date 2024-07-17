package com.sn.springCrudTesting.exceptions;

public class CustomerEmailUnavailableException extends RuntimeException {
	
    public CustomerEmailUnavailableException(String message) {
        super(message);
    }
    
}
