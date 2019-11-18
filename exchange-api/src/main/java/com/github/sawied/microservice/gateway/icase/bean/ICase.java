package com.github.sawied.microservice.gateway.icase.bean;

import java.util.Date;

public class ICase {
	
	private String caseId;
	
	private String title;
	
	private Date createTime;

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
	
	

}
