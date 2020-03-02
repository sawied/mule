package com.github.sawied.microservice.gateway;

import static org.junit.Assert.assertNotNull;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.sawied.microservice.gateway.ews.ExchangeAPI;
import com.github.sawied.microservice.gateway.ews.ExchangeExCeption;



@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(
		properties={"logging.level.org.apache.http.wire=debug "})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ExchangeTest {
	
	private static String id = "AQMkADAwATZiZmYAZC1mMGYzLTU1ADllLTAwAi0wMAoARgAAAy3Ssp2iB1BAufsNVQNIiX8HAIC8gItBA1xEg5S2jNHnPAEAAAIBDAAAAIC8gItBA1xEg5S2jNHnPAEAA8Vw9GkAAAA=";

	@Autowired
	private ExchangeAPI exchangeApi;
	
	//@Test
	public void initSuccess() throws InterruptedException {
		assertNotNull(exchangeApi);
	}
	
	//@Test
	public void aSendMailSuccess() throws InterruptedException, ExchangeExCeption {
		exchangeApi.sendMessage();
	}
	
	//@Test
	public void bRelyMailSuccess() throws InterruptedException, ExchangeExCeption {
		exchangeApi.replyMail(id);
	}
	
    @Test
	public void cretrieveMarkedMailSuccess() throws InterruptedException {
		exchangeApi.syncEWSMessage();
	}
    
	//@Test
    public void timeoutConnectionTest() throws ExchangeExCeption, InterruptedException {
    	exchangeApi.sendMessage();
    	 Thread.currentThread().sleep(1000*600);
    	 exchangeApi.syncEWSMessage();
    }
   

}
