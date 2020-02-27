package com.github.sawied.microservice.gateway;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.github.sawied.microservice.gateway.ews.CustomExchangeService;
import com.github.sawied.microservice.gateway.ews.ExchangeAPI;
import com.github.sawied.microservice.gateway.ews.MailTraceListener;
import com.github.sawied.microservice.gateway.icase.service.ICaseService;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;

@SpringBootApplication
@EnableAutoConfiguration(exclude={MongoAutoConfiguration.class, MongoRepositoriesAutoConfiguration .class,MongoDataAutoConfiguration.class})
public class ExchangeApiApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(ExchangeApiApplication.class, args);
	}

	@Bean(name="exchangeService",destroyMethod = "close")
	public ExchangeService exchangeService(@Value("${ews.email.account}") String emailAccount,
			@Value("${ews.email.password}") String mailPassword, @Value("${ews.email.url}") String url)
			throws Exception {
		ExchangeService service = new CustomExchangeService(ExchangeVersion.Exchange2010_SP2);
		ExchangeCredentials credentials = new WebCredentials(emailAccount,mailPassword);
		service.setCredentials(credentials);
		service.setUrl(new URI(url));
		service.setTraceListener(new MailTraceListener());
		service.setTraceEnabled(true);
		return service;
	}
	

	@Bean
	public ExchangeAPI exchengAPI() {
		return new ExchangeAPI();
	}
	
	@Bean
	public ICaseService caseService(){
		return new ICaseService();
	}

}
