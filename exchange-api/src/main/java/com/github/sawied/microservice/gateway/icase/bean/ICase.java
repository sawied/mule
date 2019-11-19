package com.github.sawied.microservice.gateway.icase.bean;

import java.util.Date;

public class ICase {
	
	private String caseId;
	
	private String title;
	
	private Date createTime;
	
	private String park;
	
	private String attraction;
	
	private Date occurenceOn;
	
	private String incidentNo;
	
	private String incidentType;
	
	private String briefDescription;

	public String getCaseId() {
		return caseId;
	}

	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getPark() {
		return park;
	}

	public void setPark(String park) {
		this.park = park;
	}

	public String getAttraction() {
		return attraction;
	}

	public void setAttraction(String attraction) {
		this.attraction = attraction;
	}

	public Date getOccurenceOn() {
		return occurenceOn;
	}

	public void setOccurenceOn(Date occurenceOn) {
		this.occurenceOn = occurenceOn;
	}

	public String getIncidentNo() {
		return incidentNo;
	}

	public void setIncidentNo(String incidentNo) {
		this.incidentNo = incidentNo;
	}

	public String getIncidentType() {
		return incidentType;
	}

	public void setIncidentType(String incidentType) {
		this.incidentType = incidentType;
	}

	public String getBriefDescription() {
		return briefDescription;
	}

	public void setBriefDescription(String briefDescription) {
		this.briefDescription = briefDescription;
	}
	
	
	
	

}
