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
public class IvrLanguageSet {
	
	
	private static final Logger logger = LogManager.getLogger(IvrLanguageSet.class);
	@Autowired
	Environment env;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	@RequestMapping(value = "/ivrLangChange",method = RequestMethod.GET)
	@ResponseBody
	public String getToneIdInforXML(@RequestParam("aparty") String aparty, @RequestParam("bparty") String bparty,@RequestParam("lang") String ivrLang,
			@RequestParam("action") String action,@RequestParam("serviceId") String serviceId,HttpServletRequest req,
			HttpServletResponse res) {
		
		logger.info("ivrLangChange|aparty="+aparty+"|bparty="+bparty+"|lang="+ivrLang+"|action="+action+"|serviceId="+serviceId);
		logger.trace("Query=" + env.getProperty("SQL27_INSERT_IVR_LANGUAGE"));
		//System.out.println("Öperator=" + env.getProperty("OPERATOR_NAME"));
		//System.out.println("Default_Tone_Id=" + env.getProperty("DEFAULT_TONE_ID"));
		
				
		// Replace Table Index & bparty
		if(action.equalsIgnoreCase("insert"))
		{
			String insertQuery = ChatUtils.getIvrLangInsertQuery(env.getProperty("SQL27_INSERT_IVR_LANGUAGE"), aparty,bparty,ivrLang);
			logger.info("final SQL Query="+insertQuery);
			try {
				int insertQueryResult= jdbcTemplate.update(insertQuery);
				if(insertQueryResult <= 0)
				{
					//System.out.println("Failed to insert into accout table");
					logger.error("Failed to insert|query="+insertQuery);
				}else {
					System.out.println("Successfully to insert|SQL27_INSERT_IVR_LANGUAGE|resultChangesRow="+insertQueryResult);
					logger.info("Successfully to insert|SQL27_INSERT_IVR_LANGUAGE|resultChangesRow="+insertQueryResult);
				}				
			} catch (Exception e) {
				System.out.println("SQL Exception" + e + "Query=" + insertQuery);
				System.out.println("No Row Insert into  SQL27_INSERT_IVR_LANGUAGE");
				e.printStackTrace();
			}
		}else if(action.equalsIgnoreCase("update"))
		{
			String updateQuery = ChatUtils.getIvrLangInsertQuery(env.getProperty("SQL29_UPDATE_IVR_LANGUAGE"), aparty,bparty,ivrLang);
			logger.info("final SQL Query="+updateQuery);
			try {
				int updateQueryResult= jdbcTemplate.update(updateQuery);
				if(updateQueryResult <= 0)
				{
					//System.out.println("Failed to insert into account table");
					logger.error("Failed to udate|query="+updateQuery);
				}else {
					logger.info("Successfully to insert|SQL27_UPDATE_IVR_LANGUAGE|resultChangesRow="+updateQueryResult);
				}				
			} catch (Exception e) {
				System.out.println("SQL Exception" + e + "Query=" + updateQuery);
				System.out.println("No Row Insert into  SQL27_INSERT_IVR_LANGUAGE");
				e.printStackTrace();
			}
		}
		else {
			logger.error("ivrLangChange wrong action="+action+"|aparty="+aparty+"|bparty="+bparty);
		}
		
		
			
		//String responseString = new String("DTFM_Res.result=\'Ok Accepted\';");
		String responseString = new String("Response.result=\'Ok Accepted\';");
		
	
		return responseString;
	}
}
