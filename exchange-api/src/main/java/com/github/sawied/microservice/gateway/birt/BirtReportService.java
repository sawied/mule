package com.github.sawied.microservice.gateway.birt;

import java.io.IOException;
import java.util.List;

import org.eclipse.birt.report.engine.api.EngineException;

public interface BirtReportService {
	
	static final String[] REPORT_NAMES = new String[]{"case_report"};
	
	byte[] runReport(ReportRequest request);
	
	List<Report> getReports();
	
	void loadReports()  throws EngineException, IOException;

}
