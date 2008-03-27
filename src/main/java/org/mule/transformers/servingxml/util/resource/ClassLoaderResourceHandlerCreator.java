package org.mule.transformers.servingxml.util.resource;

import java.net.URL;

/**
 * @author kennethwestelinck
 */
public class ClassLoaderResourceHandlerCreator {
	/**
	 * Default constructor.
	 */
	public ClassLoaderResourceHandlerCreator() {
		URL.setURLStreamHandlerFactory(new ClassLoaderResourceHandlerFactory());
	}
}
