package com.github.sawied.microservice.gateway.ews;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;

public class CustomExchangeService extends ExchangeService{

	private static final String CASE_MANAGEMENT_EWS = "CASE_MANAGEMENT_EWS";
	
	public CustomExchangeService(ExchangeVersion exchange2010Sp2) {
		super(exchange2010Sp2);
		super.setCustomUserAgent(CASE_MANAGEMENT_EWS);
	}

	
	
	
	
	
}
