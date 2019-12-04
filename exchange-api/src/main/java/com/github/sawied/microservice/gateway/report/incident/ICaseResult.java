package com.github.sawied.microservice.gateway.report.incident;

import java.util.ArrayList;
import java.util.List;

public class ICaseResult {
	
	private List<ICase> cases = new ArrayList<ICase>();
	
	private List<String> Conditions = new ArrayList<String>();
	
	private String owner;
	
	private String reportTitle;

	public List<ICase> getCases() {
		return cases;
	}

	public void setCases(List<ICase> cases) {
		this.cases = cases;
	}

	public List<String> getConditions() {
		return Conditions;
	}

	public void setConditions(List<String> conditions) {
		Conditions = conditions;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getReportTitle() {
		return reportTitle;
	}

	public void setReportTitle(String reportTitle) {
		this.reportTitle = reportTitle;
	}
	
	

}
