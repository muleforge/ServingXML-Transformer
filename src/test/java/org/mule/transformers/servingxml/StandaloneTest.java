package org.mule.transformers.servingxml;

import java.net.URL;

import org.custommonkey.xmlunit.XMLTestCase;
import org.mule.transformers.servingxml.util.resource.ClassLoaderResourceHandlerFactory;
import org.mule.util.IOUtils;

public class StandaloneTest extends XMLTestCase {
	private DefaultTransformer transformer;

	@Override
	protected void setUp() throws Exception {
		URL.setURLStreamHandlerFactory(new ClassLoaderResourceHandlerFactory());
		transformer = new DefaultTransformer();
		transformer.setName("books");
		transformer.setConfig("classpath:/resources-books.xml");
		transformer.initialise();
	}

	public void testDoTransform() throws Exception {
		String input = IOUtils.getResourceAsString("classpath:/books.txt",
				StandaloneTest.class);
		String expected = IOUtils.getResourceAsString(
				"classpath:/books-expected.xml", StandaloneTest.class);
		String result = (String) transformer.doTransform(input, null);

		// TODO: use xml comparison capabilities of XMLTestCase
		assertEquals(expected, result);
	}
}
