package com.sn.springCrudTesting.exceptions;

public class CustomerNotFoundException extends RuntimeException{
	
    public CustomerNotFoundException(String message) {
        super(message);
    }

}
