package com.github.sawied.microservice.gateway.icase.service;

import java.util.Date;

import com.github.sawied.microservice.gateway.report.incident.ICase;
import com.github.sawied.microservice.gateway.report.incident.ICaseResult;

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
		
		iCase.setPark("Adventure Cove Waterpark");
		iCase.setAttraction("Dolphin Lagoons/Pools");
		iCase.setOccurenceOn(new Date());
		iCase.setIncidentNo("IR2019-01440");
		iCase.setIncidentType("7000(Injury/Medical Emergency)");
		iCase.setBriefDescription("Ps2(Hamzah) was notified by medic at 1640hrs on Code 7000 at bluewater bay sunlounger area . Mother claimed son get injured while trying to get on top of the sun lounger . Son suffered superficial cut on right knee . Medic cleaned the wound with saline water and covered with band aid . Guest released at 1630hrs");
		result.getCases().add(iCase);
		
		// create case for display
				ICase iCase1 = new ICase();
				iCase1.setCaseId("CASE-ID");
				iCase1.setTitle("CASE Title");
				iCase1.setCreateTime(new Date());
				
				iCase1.setPark("Adventure Cove Waterpark");
				iCase1.setAttraction("General Area Development");
				iCase1.setOccurenceOn(new Date());
				iCase1.setIncidentNo("IR2019-01441");
				iCase1.setIncidentType("7000(Injury/Medical Emergency)");
				iCase1.setBriefDescription("Ps2(Hamzah) was notified by medic at 1640hrs on Code 7000 at bluewater bay sunlounger area . Mother claimed son get injured while trying to get on top of the sun lounger . Son suffered superficial cut on right knee . Medic cleaned the wound with saline water and covered with band aid . Guest released at 1630hrs");
				result.getCases().add(iCase1);
		
		// create criteria
		result.getConditions().add("Custoer name equal Kup");
		
		result.setOwner("James X W Zhang");
		
		result.setReportTitle("incident report");
		
		return result;
		
	}

}
