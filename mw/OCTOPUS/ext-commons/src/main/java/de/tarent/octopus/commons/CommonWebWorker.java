package de.tarent.octopus.commons;

import javax.jws.WebMethod;

import de.tarent.commons.web.ParametersProcessor;
import de.tarent.commons.web.WebFormat;
import de.tarent.octopus.content.annotation.Result;

/**
 * Set a {@link WebFormat} instance or a {@link ParametersProcessor}
 * instance into the octopus content.
 */
public class CommonWebWorker {
	/**
	 * Set a {@link WebFormat} instance into the octopus content.
	 *
	 * @return
	 */
	@WebMethod
	@Result("format")
	public WebFormat getWebFormat() {
		return new WebFormat();
	}

	/**
	 * Set a {@link ParametersProcessor} instance into the octopus content.
	 *
	 * @return
	 */
	@WebMethod
	@Result("parametersProcessor")
	public ParametersProcessor getParametersProcessor() {
		return new ParametersProcessor();
	}
}
