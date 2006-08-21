package de.tarent.octopus.commons;

import javax.jws.WebMethod;

import de.tarent.commons.web.ParametersProcessor;
import de.tarent.commons.web.WebFormat;
import de.tarent.octopus.content.annotation.Result;

public class CommonWebWorker {
	@WebMethod
	@Result("format")
	public WebFormat getWebFormat() {
		return new WebFormat();
	}

	@WebMethod
	@Result("parametersProcessor")
	public ParametersProcessor getParametersProcessor() {
		return new ParametersProcessor();
	}

}
