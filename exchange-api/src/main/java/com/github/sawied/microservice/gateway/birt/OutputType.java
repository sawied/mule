package com.github.sawied.microservice.gateway.birt;

import org.eclipse.birt.report.engine.api.IRenderOption;
import org.springframework.http.MediaType;

public enum OutputType {
	HTML(IRenderOption.OUTPUT_FORMAT_HTML),
    PDF(IRenderOption.OUTPUT_FORMAT_PDF),
    XLS("xls"),
    INVALID("invalid");

    String val;
    OutputType(String val) {
        this.val = val;
    }

    public static OutputType from(String text) {
        for (OutputType output : values()) {
            if(output.val.equalsIgnoreCase(text)) return output;
        }
        return INVALID;
    }
    
    
    public String contentType(){
    	String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
    	if(HTML.equals(this)){
    		contentType = MediaType.TEXT_HTML_VALUE;
    	}
    	if(PDF.equals(this)){
    		contentType = MediaType.APPLICATION_PDF_VALUE;
    	}
    	if(XLS.equals(this)){
    		contentType = "application/vnd.ms-excel";
    	}
    	return contentType;
    }
}
