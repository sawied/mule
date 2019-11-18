package com.github.sawied.microservice.gateway.birt;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.assertj.core.util.Lists;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EXCELRenderOption;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.EngineConstants;
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

import com.github.sawied.microservice.gateway.report.OutputType;
import com.github.sawied.microservice.gateway.report.Report;

@Service
@Qualifier("birt")
public class ReportRunnerImpl implements ReportRunner,DisposableBean{
	
	private Logger logger = LoggerFactory.getLogger(ReportRunnerImpl.class);

	private IReportEngine birtReportEngine = null;
	
	private Map<String, IReportRunnable> reports = new HashMap<>();
	
	private static final String[] REPORT_NAMES = new String[]{"case_report"};
	
	private static final String reportRoot="reports";
	
	
	/**
	 * 
	 */
	@Override
	public byte[] runReport(ReportRequest request) {
		
		OutputStream outputStream = new ByteArrayOutputStream();
		
		String reportName = request.getReportName();
		OutputType outFormart = request.getOutFormart();
		switch (outFormart) {
		case HTML:
			generateHTMLReport(reports.get(reportName),outputStream);
			break;
		case PDF:
			generatePDFReport(reports.get(reportName),outputStream);
			break;
		case XLS:
			//generateXLSReport(reports.get(reportName), response, request);
			//break;
		default:
			throw new IllegalArgumentException("Output type not recognized:" + outFormart);
		}
		String reportName = request.getReportName();
		IReportRunnable iReportRunnable = reports.get(reportName);
		IRunAndRenderTask runAndRenderTask = birtReportEngine.createRunAndRenderTask(iReportRunnable);
		runAndRenderTask.getAppContext().put("reportResult", request.getResult());
		return null;
	}
	
	public void generateMainReport(String reportName, OutputType output, HttpServletResponse response,
			HttpServletRequest request) {
	
	}

	/**
	 * Generate a report as HTML
	 */
	@SuppressWarnings("unchecked")
	private void generateHTMLReport(IReportRunnable report,OutputStream outputStream) {
		IRunAndRenderTask runAndRenderTask = birtEngine.createRunAndRenderTask(report);
		response.setContentType(birtEngine.getMIMEType("html"));
		IRenderOption options = new RenderOption();
		HTMLRenderOption htmlOptions = new HTMLRenderOption(options);
		htmlOptions.setOutputFormat("html");
		htmlOptions.setBaseImageURL("/" + reportsPath + imagesPath);
		htmlOptions.setImageDirectory(imageFolder);
		htmlOptions.setImageHandler(htmlImageHandler);
		runAndRenderTask.setRenderOption(htmlOptions);
		runAndRenderTask.getAppContext().put(EngineConstants.APPCONTEXT_BIRT_VIEWER_HTTPSERVET_REQUEST, request);

		try {
			htmlOptions.setOutputStream(response.getOutputStream());
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
	@SuppressWarnings("unchecked")
	private void generatePDFReport(IReportRunnable report, HttpServletResponse response, HttpServletRequest request) {
		IRunAndRenderTask runAndRenderTask = birtEngine.createRunAndRenderTask(report);
		response.setContentType(birtEngine.getMIMEType("pdf"));
		IRenderOption options = new RenderOption();
		PDFRenderOption pdfRenderOption = new PDFRenderOption(options);
		pdfRenderOption.setOutputFormat("pdf");
		runAndRenderTask.setRenderOption(pdfRenderOption);
		runAndRenderTask.getAppContext().put(EngineConstants.APPCONTEXT_PDF_RENDER_CONTEXT, request);

		try {
			pdfRenderOption.setOutputStream(response.getOutputStream());
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
	@SuppressWarnings("unchecked")
	private void generateXLSReport(IReportRunnable report, HttpServletResponse response, HttpServletRequest request) {
		IRunAndRenderTask runAndRenderTask = birtEngine.createRunAndRenderTask(report);
		response.setContentType(birtEngine.getMIMEType("pdf"));
		IRenderOption options = new RenderOption();
		EXCELRenderOption xlsRenderOption = new EXCELRenderOption(options);
		xlsRenderOption.setOutputFormat("xlsx");
		runAndRenderTask.setRenderOption(xlsRenderOption);
		//runAndRenderTask.getAppContext().put(EngineConstants., request);

		try {
			xlsRenderOption.setOutputStream(response.getOutputStream());
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
			
		} catch (BirtException e) {
			e.printStackTrace();
		}
	}
	
	
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
	
		ArrayList<String> reportNameList = Lists.list(REPORT_NAMES);
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
