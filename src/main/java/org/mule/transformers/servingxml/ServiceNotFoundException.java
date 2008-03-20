package org.mule.transformers.servingxml;

public class ServiceNotFoundException extends Exception {
	private static final long serialVersionUID = -811006578972622526L;

	public ServiceNotFoundException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ServiceNotFoundException(String arg0) {
		super(arg0);
	}
}
