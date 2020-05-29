package com.github.sawied.microservice.gateway.ews;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import microsoft.exchange.webservices.data.misc.ITraceListener;

public class MailTraceListener implements ITraceListener{

	private static final Logger LOG = LoggerFactory.getLogger(MailTraceListener.class);
	
	@Override
	public void trace(String traceType, String traceMessage) {
		LOG.debug("trace Type  ---------{}", traceType);
		//LOG.debug("trace message:  {} : ---------", traceMessage);
	}

}
