package com.increff.pos.service;

public class ApiException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public ApiException(String str) {
		super(str);
	}

}
