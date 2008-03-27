package org.mule.transformers.servingxml.util.resource;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

/**
 * Description: This is our factory that will create class resource handlers.
 * 
 * @author kennethwestelinck
 */
public class ClassLoaderResourceHandlerFactory implements
		URLStreamHandlerFactory {

	/** Default constructor. */
	public ClassLoaderResourceHandlerFactory() {
	}

	/**
	 * Create the handler for our custom URL. Example:
	 * <code>classpath:/a.dtd</code>
	 * 
	 * @param protocol
	 *            the protocol found in the URL
	 * @return a URLStreamHandler object if this protocol will be handled by us
	 */
	public URLStreamHandler createURLStreamHandler(String protocol) {
		if (protocol.equals(ClassLoaderResourceHandler.PROTOCOL)) {
			return new ClassLoaderResourceHandler();
		}
		return null;
	}
}
