package com.santosh.greenzone.wapchatezee.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.santosh.greenzone.utils.ChatUtils;



@CrossOrigin
@RestController
public class IVRCdrReportHandler {

	private static final Logger logger = LogManager.getLogger(IVRCdrReportHandler.class);
	@Autowired
	Environment env;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	@RequestMapping(value = "/ivrCdrReq",method = RequestMethod.GET)
	@ResponseBody
	public String ivrCdrReportHandler(@RequestParam("aparty") String aparty, @RequestParam("bparty") String bparty,@RequestParam("reason") String reason,@RequestParam("duration") String duration, @RequestParam("lastNode") String lastNode,@RequestParam("callStartTime") String callStartTime,@RequestParam("callEndTime") String callEndTime,HttpServletRequest req,
			HttpServletResponse res) {
		
		logger.info("ivrCdrReq|aparty=" + aparty+"|bparty="+bparty+"|reason="+reason+"|duration="+duration+"|lastNode="+lastNode+"|callStartTime="+callStartTime+"|callEndTime="+callEndTime);
		//logger.info("Query=" + env.getProperty("SQL34_INSERT_TP_CDR"));
		
		
		
		// Replace Table Index & bparty 
		String insertQuery = ChatUtils.getTonePlayerCDRQuery(env.getProperty("SQL43_INSERT_IVR_CDR"),aparty,bparty,reason,duration,lastNode,callStartTime,callEndTime);
		
		logger.info("final SQL Query="+insertQuery);
		
		try {
				int insertQueryResult= jdbcTemplate.update(insertQuery);
				if(insertQueryResult <= 0)
				{
					logger.error("Failed to insert|query="+insertQuery);
				}else {
					logger.info("Successfully to insert|SQL43_INSERT_IVR_CDR|resultChangesRow="+insertQueryResult);
				}
		} catch (Exception e) {
			System.out.println("SQL Exception" + e + "Query=" + insertQuery);
			System.out.println("No Row Insert into  SQL43_INSERT_IVR_CDR");
			e.printStackTrace();
		}
		
		
		String responseString = new String("CDR_Res.result=\'Ok Accepted\';");
		/*
		 * responseString =
		 * responseString.concat("RBT_RES.toneId=\'"+toneInfoDetails.getToneId()+
		 * ".wav\';"); Date date = new Date(); SimpleDateFormat DateFor = new
		 * SimpleDateFormat("dd-M-yyyy hh:mm:ss");
		 * System.out.println("CDR Report|"+DateFor.format(date)+"|aparty="+aparty+
		 * "|bparty="+bparty+"|status="+toneInfoDetails.getStatus()+"|toneId="+
		 * toneInfoDetails.getToneId()+"|");
		 */
		return responseString;
	}
	
	
}
