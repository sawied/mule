package com.github.sawied.microservice.gateway;


import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

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
	
	@Test
	public void plainText() throws IOException{
		
		final ClassPathResource classPathResource = new ClassPathResource("test.html");
		byte[] bytes=FileCopyUtils.copyToByteArray(classPathResource.getInputStream());
		Document document = Jsoup.parse(new String(bytes,"UTF-8"));
		document.outputSettings().prettyPrint(true);
		String output=document.wholeText().trim() ;
		System.out.println(output);
	}

	
	@Test
	public void fileExtension() throws IOException{
		
	 System.out.println(FilenameUtils.getExtension("RE:RE 3 Test 030320"));
	}
}
