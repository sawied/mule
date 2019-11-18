package com.github.sawied.microservice.gateway.icase.service;

import java.util.Date;

import com.github.sawied.microservice.gateway.icase.bean.ICase;
import com.github.sawied.microservice.gateway.icase.bean.ICaseResult;

public class ICaseService {
	
	
	/**
	 *  list Case
	 * @return ICaseResult
	 */
	public ICaseResult listCases(){
		ICaseResult result =	new ICaseResult();
		
		// create case for display
		ICase iCase = new ICase();
		iCase.setCaseId("CASE-ID");
		iCase.setTitle("CASE Title");
		iCase.setCreateTime(new Date());
		
		result.getCases().add(iCase);
		
		// create criteria
		result.getConditions().add("Custoer name equal Kup");
		
		result.setOwner("James X W Zhang");
		
		result.setReportTitle("incident report");
		
		return result;
		
	}

}
