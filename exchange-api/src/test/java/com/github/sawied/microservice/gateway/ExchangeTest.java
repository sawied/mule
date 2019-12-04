package com.github.sawied.microservice.gateway;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.sawied.microservice.gateway.ews.ExchangeAPI;



@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(
		properties={"logging.level.org.apache.http.wire=debug "})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ExchangeTest {

	@Autowired
	private ExchangeAPI exchangeApi;
	
	//@Test
	public void aSendMailSuccess() throws InterruptedException {
		exchangeApi.sendMessage();
	}
	
	@Test
	public void bMarkMailListSuccess() throws InterruptedException {
		//exchangeApi.markMailList();
	}
	@Test
	public void cretrieveMarkedMailSuccess() throws InterruptedException {
		exchangeApi.retrieveMarkedMailList();
	}
	

}
