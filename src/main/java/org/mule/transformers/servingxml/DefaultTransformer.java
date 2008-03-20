package org.mule.transformers.servingxml;

import java.io.ByteArrayOutputStream;
import java.util.logging.Level;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.mule.transformers.AbstractTransformer;
import org.mule.umo.lifecycle.InitialisationException;
import org.mule.umo.transformer.TransformerException;

import com.servingxml.app.AppContext;
import com.servingxml.app.DefaultAppContext;
import com.servingxml.app.DefaultServiceContext;
import com.servingxml.app.Service;
import com.servingxml.app.ServiceContext;
import com.servingxml.app.Flow;
import com.servingxml.app.FlowImpl;
import com.servingxml.io.streamsink.OutputStreamSinkAdaptor;
import com.servingxml.io.streamsink.StreamSink;
import com.servingxml.io.streamsource.StreamSource;
import com.servingxml.io.streamsource.StringStreamSource;
import com.servingxml.ioc.resources.IocContainer;
import com.servingxml.ioc.resources.IocContainerFactory;
import com.servingxml.util.record.ParameterBuilder;
import com.servingxml.util.record.Record;
import com.servingxml.util.system.RuntimeContext;

public class DefaultTransformer extends AbstractTransformer {
	private static final long serialVersionUID = 8820909385444863910L;
	private Logger log = Logger.getLogger(this.getClass());
	private AppContext appContext = null;
	private Object lock = new Object();
	private String config = null;

	public DefaultTransformer() {
		super.registerSourceType(String.class);
		super.setReturnClass(String.class);
	}

	public void initialise() throws InitialisationException {
		synchronized (lock) {
			// try to create the app context
			try {
				appContext = getAppContext();
			} catch (Exception e) {
				throw new InitialisationException(e, this);
			}
		}
	}

	protected AppContext getAppContext() throws Exception {
		IocContainerFactory iocContainerFactory = new IocContainerFactory();
		iocContainerFactory.loadComponentDefinitions();

		IocContainer resources = iocContainerFactory.createIocContainer(config);

		return new DefaultAppContext(this.getName(), resources, new MyLogger());
	}

	public Object clone() throws CloneNotSupportedException {
		/*
		 * Somewhere, down the line, we will be cloned. Make sure that our
		 * private members are cloned as well.
		 */
		try {
			DefaultTransformer clone = (DefaultTransformer) BeanUtils
					.cloneBean(this);
			synchronized (lock) {
				clone.config = this.config;
				clone.appContext = this.appContext;
			}
			return clone;
		} catch (Exception e) {
			throw new CloneNotSupportedException(e.getMessage());
		}
	}

	protected Object doTransform(Object src, String encoding)
			throws TransformerException {
		try {
			return this.doTransform2(src, encoding);
		} catch (Exception e) {
			throw new TransformerException(this, e);
		}
	}

	protected Object doTransform2(Object src, String encoding) throws Exception {
		StreamSource in = null;
		if (src instanceof String) {
			in = new StringStreamSource((String) src);
		}
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		StreamSink out = new OutputStreamSinkAdaptor(outputStream);

		synchronized (lock) {
			ServiceContext serviceContext = new DefaultServiceContext(
					appContext, this.getName());
			Service service = (Service) appContext.getResources()
					.lookupServiceComponent(Service.class, this.getName());

			/* Add some parameters, if any. */
			ParameterBuilder paramBuilder = new ParameterBuilder();
			Record parameters = paramBuilder.toRecord();

			Flow flow = new FlowImpl(parameters, in, out);

			service.execute(serviceContext, flow);
		}

		return outputStream.toString();
	}

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	private class MyLogger implements com.servingxml.util.system.Logger {

		public void error(RuntimeContext context, String message) {
			log.error(message);
		}

		public void notice(RuntimeContext context, String message) {
			log.info(message);
		}

		public void printStackTrace(RuntimeContext context, Throwable t) {
			log.error("Error: ", t);
		}

		public void trace(RuntimeContext context, String sourceClass,
				String sourceMethod, String message, Level level) {
			log.debug(message);
		}

		public void warning(RuntimeContext context, String message) {
			log.warn(message);
		}
	}
}
