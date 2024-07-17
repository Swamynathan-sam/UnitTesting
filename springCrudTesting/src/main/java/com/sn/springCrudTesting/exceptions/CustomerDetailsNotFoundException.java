package com.sn.springCrudTesting.exceptions;

public class CustomerDetailsNotFoundException extends RuntimeException {
	
	   public static final long serialVersionUID = 1L;

	    public static String message = "Id not found ";

	    public CustomerDetailsNotFoundException() {
	        super(message);
	    }

	    public CustomerDetailsNotFoundException(String message) {
	        super(message);
		}


}
