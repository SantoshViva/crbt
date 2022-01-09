package com.santosh.greenzone.wapchatezee.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.springframework.core.env.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.santosh.greenzone.utils.ChatUtils;
import com.santosh.greenzone.wapchatezee.model.ToneInfo;


@CrossOrigin
@RestController
public class UserToneInfoXML {
	private static final Logger logger = LogManager.getLogger(UserToneInfoXML.class);
	
	@Autowired
	Environment env;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	@RequestMapping(value = "/getToneIdInfoXml",method = RequestMethod.GET)
	@ResponseBody
	public String getToneIdInforXML(@RequestParam("aparty") String aparty, @RequestParam("bparty") String bparty, HttpServletRequest req,
			HttpServletResponse res) {
		
		logger.info("getToneIdInforXML|aparty=" + aparty+"|bparty="+bparty);
		//logger.info("Query=" + env.getProperty("SQL23_USER_TONE_INFO"));
		//logger.info("Operator=" + env.getProperty("OPERATOR_NAME"));
		
		if(bparty.length()>9)
		{
			bparty= bparty.substring(bparty.length()-9);
			logger.info("getToneIdInforXML|aparty="+aparty+"|modify bparty="+bparty);
		}
		logger.trace("Tone Id for No Record Found|Default_Tone_Id=" + env.getProperty("NOT_CRBT_DEFAULT_TONE_ID"));
		
		
		// Replace Table Index & bparty 
		String query = ChatUtils.getQuery(env.getProperty("SQL23_USER_TONE_INFO"), bparty);
		
		logger.trace("final SQL Query="+query);
		ToneInfo toneInfoDetails = new ToneInfo();
		toneInfoDetails.setToneId(env.getProperty("NOT_CRBT_DEFAULT_TONE_ID"));
		toneInfoDetails.setCallingParty("D");
		toneInfoDetails.setContentType("N");
		toneInfoDetails.setSongName("No_DB");
		toneInfoDetails.setSongPath("songPath");
		toneInfoDetails.setServiceId("default");
		try {
			List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(query);
			
			if(queryForList.isEmpty())
			{
				logger.error("No Record Found in SQL|aparty="+aparty+"|defaultTone="+env.getProperty("NOT_CRBT_DEFAULT_TONE_ID"));
				toneInfoDetails.setStatus("N");	
				
			}else 
			{
				for (Map<String, Object> row : queryForList) {
					
					toneInfoDetails.setStatus(row.get("status").toString());
					toneInfoDetails.setCallingParty(row.get("calling_party").toString());
					toneInfoDetails.setToneId(row.get("tone_id").toString());
					if((row.get("song_name") != null))
					{
						toneInfoDetails.setSongName(row.get("song_name").toString());
					}
					if(row.get("song_path")!= null)
					{
						toneInfoDetails.setSongPath(row.get("song_path").toString());
					}
					if(row.get("service_id")!=null)
					{
						toneInfoDetails.setServiceId(row.get("service_id").toString());
					}
					
					if(toneInfoDetails.getToneId().length()<10)
					{
						logger.trace("This is old 6d data migration|aparty="+aparty);
						toneInfoDetails.setContentType("O");
						
					}else
					{
						toneInfoDetails.setContentType("N");
					}
					
					logger.trace("QueryResult|msisdn="+bparty+"|status="+row.get("status").toString()+"|calling_party="+row.get("calling_party").toString()+"|tone_id="+row.get("tone_id").toString()+"|");
					if(toneInfoDetails.getStatus().equals("Y") && toneInfoDetails.getCallingParty().equals(aparty))
					{
						logger.info("Calling Party Matched");
						break;
					}
				}
			}
		} catch (Exception e) {
			logger.error("SQL Exception" + e +"|Query="+query);
			logger.error("No Row Found");
			toneInfoDetails.setStatus("D");	
			toneInfoDetails.setToneId(env.getProperty("NOT_CRBT_DEFAULT_TONE_ID"));
			e.printStackTrace();
		}
		
		
//		toneInfo.setSubscriberId("4444444444");
//		toneInfo.setToneId("1234567890");
		
		String responseString = new String();
		
		responseString = responseString.concat("RBT_RES.toneId=\'"+toneInfoDetails.getToneId()+"\';");
		responseString = responseString.concat("RBT_RES.setStatus=\'"+toneInfoDetails.getStatus()+"\';");
		responseString = responseString.concat("RBT_RES.callingParty=\'"+toneInfoDetails.getCallingParty()+"\';");
		responseString = responseString.concat("RBT_RES.status=\'"+toneInfoDetails.getStatus()+"\';");
		responseString = responseString.concat("RBT_RES.songName=\'"+toneInfoDetails.getSongName()+"\';");
		responseString = responseString.concat("RBT_RES.songPath=\'"+toneInfoDetails.getSongPath()+"\';");
		responseString = responseString.concat("RBT_RES.contentType=\'"+toneInfoDetails.getContentType()+"\';");
		Date date = new Date();
		SimpleDateFormat DateFor = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
		logger.info("TP_RES|date="+DateFor.format(date)+"|aparty="+aparty+"|bparty="+bparty+"|responseString="+responseString);
		return responseString;
		
	}
}
