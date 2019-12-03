package com.github.sawied.microservice.gateway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.github.sawied.microservice.gateway.ews.ExchangeAPI;

@RestController
public class EwsController {

	
	@Autowired
	private ExchangeAPI exchangeApi;
	
	@RequestMapping("/mails")
	public @ResponseBody String retrieveMail() {
		exchangeApi.mailList();
		return "SUCCESS";
	}
	
}
