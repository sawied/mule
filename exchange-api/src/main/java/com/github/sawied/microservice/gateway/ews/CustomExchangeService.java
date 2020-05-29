package com.github.sawied.microservice.gateway.ews;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.AuthenticationStrategy;
import org.apache.http.config.Registry;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;

import microsoft.exchange.webservices.data.core.CookieProcessingTargetAuthenticationStrategy;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;

public class CustomExchangeService extends ExchangeService{

	private static final String CASE_MANAGEMENT_EWS = "CASE_MANAGEMENT_EWS";
	
	private  HttpClientConnectionManager httpConnectionManager;
	
	public CustomExchangeService(ExchangeVersion exchange2010Sp2) {
		super(exchange2010Sp2);
		super.setCustomUserAgent(CASE_MANAGEMENT_EWS);
		customHttpClient();
		this.getHttpHeaders().put("Authorization", "Basic bWFpbGFkZHJlc3NAaG9zdC5jb206cGFzc3dvcmQ=");
	}
	
	
	public void customHttpClient() {
	
		if(this.httpClient!=null) {
			try {
				this.httpClient.close();
				
				 Registry<ConnectionSocketFactory> registry = createConnectionSocketFactoryRegistry();
				 HttpClientConnectionManager httpConnectionManager = new BasicHttpClientConnectionManager(registry);
				    
				    AuthenticationStrategy authStrategy = new CookieProcessingTargetAuthenticationStrategy();
				    this.httpClient = HttpClients.custom()
				      .setConnectionManager(httpConnectionManager)
				      .setTargetAuthenticationStrategy(authStrategy)
				      .build();
				    this.httpConnectionManager = httpConnectionManager;	
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
	public void releaseConnection() {
		if(this.httpConnectionManager!=null) {
			this.httpConnectionManager.closeExpiredConnections();
			this.httpConnectionManager.closeIdleConnections(5, TimeUnit.SECONDS);
		}
	}

	
	
	
}
