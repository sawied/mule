package com.github.sawied.microservice.gateway.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.birt.report.engine.api.EngineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.sawied.microservice.gateway.birt.BirtReportService;
import com.github.sawied.microservice.gateway.birt.OutputType;
import com.github.sawied.microservice.gateway.birt.Report;
import com.github.sawied.microservice.gateway.birt.ReportRequest;
import com.github.sawied.microservice.gateway.icase.service.ICaseService;
import com.github.sawied.microservice.gateway.report.incident.ICaseResult;



@Controller
public class BirtReportController {
	
    private static final Logger log = LoggerFactory.getLogger(BirtReportController.class);

    @Autowired
    private BirtReportService reportService;
    
    @Autowired
    private ICaseService caseService;

    @RequestMapping(produces = "application/json", method = RequestMethod.GET, value = "/report")
    @ResponseBody
    public List<Report> listReports() {
        return reportService.getReports();
    }

    @RequestMapping(produces = "application/json", method = RequestMethod.GET, value = "/report/reload")
    @ResponseBody
    public ResponseEntity<Void> reloadReports(HttpServletResponse response) throws IOException {
        try {
            log.info("Reloading reports");
            reportService.loadReports();
        } catch (EngineException e) {
            log.error("There was an error reloading the reports in memory: ", e);
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/report/{name}")
    @ResponseBody
    public void generateFullReport(HttpServletResponse response, HttpServletRequest request,
                                   @PathVariable("name") String name, @RequestParam("output") String output) throws IOException {
        log.info("Generating full report: " + name + "; format: " + output);
        
        if(!Arrays.asList(BirtReportService.REPORT_NAMES).contains(name)){
        	log.error("no report named {}", name);
        	throw new IllegalArgumentException("no report named "+ name);
        }
        
        OutputType format = OutputType.from(output);
        response.setContentType(format.contentType());
        
       /*******core code********/
        ICaseResult listCases = caseService.listCases();
        
        
        ReportRequest<ICaseResult> reportRequest = new ReportRequest<ICaseResult>(name,format,listCases);
    
        byte[] data =reportService.runReport(reportRequest);
  
        
        FileCopyUtils.copy(data, response.getOutputStream());
    }
    
 
}
