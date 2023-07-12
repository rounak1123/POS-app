package com.increff.pos.service;

import java.util.ArrayList;
import java.util.List;

public class ApiException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public ApiException(String str) {
		super(str);
	}

}
