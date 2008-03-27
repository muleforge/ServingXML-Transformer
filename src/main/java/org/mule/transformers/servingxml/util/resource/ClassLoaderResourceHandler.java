package org.mule.transformers.servingxml.util.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * <p>
 * Description: This resource handler loads files specified in a URL from the
 * classpath.
 * <p>
 * Example: you'll need to put the following in your dtd to load
 * <code>a.dtd</code>: <code>classpath:/a.dtd</code>
 * 
 * @author kennethwestelinck
 */
public class ClassLoaderResourceHandler extends URLStreamHandler {
	/** This is the protocol to be used: <code>classpath</code>. */
	public static final String PROTOCOL = "classpath";

	/**
	 * Open a connection with the specified url.
	 */
	protected URLConnection openConnection(final URL url) throws IOException {
		return new ClassLoaderResourceURLConnection(url);
	}

	/** This method should return a parseable string form of this URL. */
	protected String toExternalForm(final URL url) {
		return PROTOCOL.concat(":").concat(url.getFile());
	}

	/**
	 * Must override to prevent default parsing of our URLs as HTTP-like URLs
	 * (the base class implementation eventually calls setURL(), which is tied
	 * to HTTP URL syntax too much).
	 */
	protected void parseURL(final URL context, final String spec,
			final int start, final int limit) {
		final String resourceName = combineResourceNames(context.getFile(),
				spec.substring(start));
		setURL(context, context.getProtocol(), "", -1, "", "", resourceName,
				null, "");
	}

	/** The URLConnection implementation used by this scheme. */
	private static final class ClassLoaderResourceURLConnection extends
			URLConnection {
		public void connect() {
			// Do nothing, as we will look for the resource in getInputStream().
		}

		public InputStream getInputStream() throws IOException {
			// This always uses the current thread's context loader. A better
			// strategy is to use techniques shown in
			// http://www.javaworld.com/javaworld/javaqa/2003-06/01-qa-0606-load.html.
			final ClassLoader loader = Thread.currentThread()
					.getContextClassLoader();

			// Don't be fooled by our calling url.getFile(): it is just a
			// string,
			// not necessarily a real filename.
			String resourceName = url.getFile();
			while (resourceName.startsWith("/")) {
				resourceName = resourceName.substring(1);
			}
			// if (resourceName.startsWith("/"))

			final InputStream result = loader.getResourceAsStream(resourceName);

			if (result == null)
				throw new IOException("resource [" + resourceName + "] could "
						+ "not be found by classloader ["
						+ loader.getClass().getName() + "]");

			return result;
		}

		protected ClassLoaderResourceURLConnection(final URL url) {
			super(url);
		}

	} // End of nested class.

	private static String combineResourceNames(String base, String relative) {
		if ((base == null) || (base.length() == 0))
			return relative;
		if ((relative == null) || (relative.length() == 0))
			return base;

		if (relative.startsWith("/"))
			// 'relative' is actually absolute in this case.
			return relative.substring(1);

		if (base.endsWith("/"))
			return base.concat(relative);
		else {
			// Replace the name segment after the last separator:
			final int lastBaseSlash = base.lastIndexOf('/');

			if (lastBaseSlash < 0)
				return relative;
			else
				return base.substring(0, lastBaseSlash).concat("/").concat(
						relative);
		}
	}

} // End of class.
