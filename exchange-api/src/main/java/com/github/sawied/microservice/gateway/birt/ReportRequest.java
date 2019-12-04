package com.github.sawied.microservice.gateway.birt;

/**
 * 
 * @author danan
 *
 */
public class ReportRequest<T> {
	
	private String reportName;
	
	private OutputType outFormart;
	
	private T result;
	
	
	
	public ReportRequest(String reportName, OutputType outFormart, T result) {
		super();
		this.reportName = reportName;
		this.outFormart = outFormart;
		this.result = result;
	}

	public T getResult() {
		return result;
	}
	
	public void setResult(T result) {
		this.result = result;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}


	public OutputType getOutFormart() {
		return outFormart;
	}

	public void setOutFormart(OutputType outFormart) {
		this.outFormart = outFormart;
	}

	@Override
	public String toString() {
		return "ReportRequest [reportName=" + reportName + ",  outFormart="
				+ outFormart + "]";
	}


}
