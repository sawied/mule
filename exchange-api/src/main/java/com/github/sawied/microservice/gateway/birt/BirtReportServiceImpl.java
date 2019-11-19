package com.github.sawied.microservice.gateway.birt;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EXCELRenderOption;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.IGetParameterDefinitionTask;
import org.eclipse.birt.report.engine.api.IParameterDefn;
import org.eclipse.birt.report.engine.api.IRenderOption;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.PDFRenderOption;
import org.eclipse.birt.report.engine.api.RenderOption;
import org.eclipse.core.internal.registry.RegistryProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;


@Service
@Qualifier("birt")
public class BirtReportServiceImpl implements BirtReportService,DisposableBean{
	
	private static final String REPORT_RESULT = "ds";

	private static final Logger LOGGER = LoggerFactory.getLogger(BirtReportServiceImpl.class);

	private IReportEngine birtReportEngine = null;
	
	private Map<String, IReportRunnable> reports = new HashMap<>();
	
	private static final String reportRoot="reports";
	
	
	/**
	 * 
	 */
	@Override
	public byte[] runReport(ReportRequest request) {
		
		LOGGER.info("accept report request {}" , request);
		LOGGER.info("returned response {}",request.getResult());
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		String reportName = request.getReportName();
		OutputType outFormart = request.getOutFormart();
		switch (outFormart) {
		case HTML:
			generateHTMLReport(reports.get(reportName),request,outputStream);
			break;
		case PDF:
			generatePDFReport(reports.get(reportName),request,outputStream);
			break;
		case XLS:
			generateXLSReport(reports.get(reportName),request,outputStream);
			break;
		default:
			throw new IllegalArgumentException("Output type not recognized:" + outFormart);
		}

		return outputStream.toByteArray();
	}
	

	/**
	 * Generate a report as HTML
	 */
	private void generateHTMLReport(IReportRunnable report,ReportRequest request,OutputStream outputStream) {
		IRunAndRenderTask runAndRenderTask = birtReportEngine.createRunAndRenderTask(report);
		IRenderOption options = new RenderOption();
		HTMLRenderOption htmlOptions = new HTMLRenderOption(options);
		htmlOptions.setOutputFormat("html");
		Map<String,Object> appContext = new HashMap<String,Object>();
		appContext.put(REPORT_RESULT, request.getResult());
		runAndRenderTask.setAppContext(appContext);
		try {
			htmlOptions.setOutputStream(outputStream);
			runAndRenderTask.run();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			runAndRenderTask.close();
		}
	}

	/**
	 * Generate a report as PDF
	 */
	private void generatePDFReport(IReportRunnable report,ReportRequest request,OutputStream outputStream) {
		IRunAndRenderTask runAndRenderTask = birtReportEngine.createRunAndRenderTask(report);
		IRenderOption options = new RenderOption();
		PDFRenderOption pdfRenderOption = new PDFRenderOption(options);
		pdfRenderOption.setOutputFormat("pdf");
		runAndRenderTask.setRenderOption(pdfRenderOption);
		Map<String,Object> appContext = new HashMap<String,Object>();
		appContext.put(REPORT_RESULT, request.getResult());
		runAndRenderTask.setAppContext(appContext);
		try {
			pdfRenderOption.setOutputStream(outputStream);
			runAndRenderTask.run();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			runAndRenderTask.close();
		}
	}
	
	
	/**
	 * Generate a report as xls
	 * Content-Type : application/vnd.ms-excel
	 */
	private void generateXLSReport(IReportRunnable report, ReportRequest request,OutputStream outputStream) {
		IRunAndRenderTask runAndRenderTask = birtReportEngine.createRunAndRenderTask(report);
		IRenderOption options = new RenderOption();
		EXCELRenderOption xlsRenderOption = new EXCELRenderOption(options);
		xlsRenderOption.setOutputFormat("xlsx");
		runAndRenderTask.setRenderOption(xlsRenderOption);
		Map<String,Object> appContext = new HashMap<String,Object>();
		appContext.put(REPORT_RESULT, request.getResult());
		runAndRenderTask.setAppContext(appContext);
		try {
			xlsRenderOption.setOutputStream(outputStream);
			runAndRenderTask.run();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			runAndRenderTask.close();
		}
	}
	
	
	
	@PostConstruct
	public void startUp(){
		EngineConfig engineConfig = new EngineConfig();
		RegistryProviderFactory.releaseDefault();
		try {
			Platform.startup(engineConfig);
			IReportEngineFactory reportEngineFactory = (IReportEngineFactory) Platform.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
			birtReportEngine = reportEngineFactory.createReportEngine(engineConfig);
			loadReports();
		} catch (BirtException | IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public List<Report> getReports() {
		List<Report> response = new ArrayList<>();
		for (Map.Entry<String, IReportRunnable> entry : reports.entrySet()) {
			IReportRunnable report = reports.get(entry.getKey());
			IGetParameterDefinitionTask task = birtReportEngine.createGetParameterDefinitionTask(report);
			Report reportItem = new Report(report.getDesignHandle().getProperty("title").toString(), entry.getKey());
			for (Object h : task.getParameterDefns(false)) {
				IParameterDefn def = (IParameterDefn) h;
				reportItem.getParameters()
						.add(new Report.Parameter(def.getPromptText(), def.getName(), getParameterType(def)));
			}
			response.add(reportItem);
		}
		return response;
	}
	
	/**
	 * Load report files to memory
	 * @throws IOException 
	 *
	 */
	public void loadReports() throws EngineException, IOException {
	
		List<String> reportNameList = Arrays.asList(REPORT_NAMES);
		for(String reportName : reportNameList ){
			ClassPathResource classPathResource = new ClassPathResource(reportRoot+"/"+reportName+"."+"rptdesign");
			File file = classPathResource.getFile();
			reports.put(file.getName().replace(".rptdesign", ""),
					birtReportEngine.openReportDesign(file.getAbsolutePath()));
		}

	}
	
	private Report.ParameterType getParameterType(IParameterDefn param) {
		if (IParameterDefn.TYPE_INTEGER == param.getDataType()) {
			return Report.ParameterType.INT;
		}
		return Report.ParameterType.STRING;
	}

	@Override
	public void destroy() throws Exception {
		birtReportEngine.destroy();
		Platform.shutdown();
		
	}

}
