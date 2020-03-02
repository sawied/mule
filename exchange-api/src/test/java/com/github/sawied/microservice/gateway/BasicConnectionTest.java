package com.github.sawied.microservice.gateway;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Test;

public class BasicConnectionTest {
	
	private String  references = "<tencent_8D3A3C2141DF8DAB376251EC2BF368398308@qq.com>";

	@Test
	public void test() {
		String orign = null;
		if(references!=null) {
			String removedBlankString  = references.trim();
			
			if(removedBlankString.startsWith("<")) {
				int indexOf = removedBlankString.indexOf(">");
				orign=removedBlankString.substring(0, indexOf+1);
			}
		}
		System.out.println(orign);
	}

}
