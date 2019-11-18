package com.github.sawied.microservice.gateway.birt;

import com.github.sawied.microservice.gateway.report.OutputType;

/**
 * 
 * @author danan
 *
 */
public class ReportRequest {
	
	private String reportName;
	
	private Object reportParams;
	
	private OutputType outFormart;
	
	private Object result;
	

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public Object getReportParams() {
		return reportParams;
	}

	public void setReportParams(Object reportParams) {
		this.reportParams = reportParams;
	}

	public OutputType getOutFormart() {
		return outFormart;
	}

	public void setOutFormart(OutputType outFormart) {
		this.outFormart = outFormart;
	}
	
	

}
