package com.santosh.greenzone.wapchatezee.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.PostRemove;
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
public class CheckVoiceSmsDetails {

	private static final Logger logger = LogManager.getLogger(CheckVoiceSmsDetails.class);
	@Autowired
	Environment env;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	@RequestMapping(value = "/checkVoiceSmsDetails",method = RequestMethod.GET)
	@ResponseBody
	public String getToneIdInforXML(@RequestParam("aparty") String aparty,@RequestParam("bparty") String bparty, HttpServletRequest req,
			HttpServletResponse res) {
		
		logger.info("checkVoiceSmsDetails|aparty="+aparty+"|bparty="+bparty);
		
		String zvMsisdn="0"+aparty;
		logger.info("checkVoiceSmsDetails|aparty="+aparty+"|bparty="+bparty+"|zvMsisdn="+zvMsisdn);
		String vSMSMessageQuery= ChatUtils.getVoiceSmsQuery(env.getProperty("SQL39_SELECT_VSMS_MESSAGE_DETAILS"), aparty,zvMsisdn);
		logger.info("final check voice sms detail Query="+vSMSMessageQuery);
		String responseString = new String();
		String dbError ="N";
		int counter=0;
		
		try {
			List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(vSMSMessageQuery);
			
			if(queryForList.isEmpty())
			{
				logger.info("No Record Found in SQL|aparty="+aparty);
				
				responseString = responseString.concat("VSMS_MESSAGE_QUERY_RES.messageCount=\'"+"0"+"\';");
											
			}else 
			{
				for (Map<String, Object> row : queryForList) {
					Integer intCounterLocal = new Integer(counter);
					
					if(row.get("subscriber_id")==null||row.get("recording_path")== null||row.get("recording_path").toString().isEmpty()||row.get("subscriber_id").toString().isEmpty())
					{
						logger.error("subscriber_id or recording_path is null in database|aparty="+aparty);
						continue;
					}else
					{	
						logger.info("id="+Integer.parseInt(row.get("id").toString())+"|msisdn="+row.get("subscriber_id")+"|recordingPath="+row.get("recording_path"));
						responseString = responseString.concat("VSMS_MESSAGE_QUERY_RES.voiceMessageId["+intCounterLocal.toString()+"]"+"=\'"+Integer.parseInt(row.get("id").toString())+"\';");
						responseString = responseString.concat("VSMS_MESSAGE_QUERY_RES.msisdn["+intCounterLocal.toString()+"]"+"=\'"+row.get("subscriber_id")+"\';");
						responseString = responseString.concat("VSMS_MESSAGE_QUERY_RES.recordingPath["+intCounterLocal.toString()+"]"+"=\'"+row.get("recording_path")+"\';");
						
					}
					counter++;				
				}
				Integer intCounter = new Integer(counter);
				responseString = responseString.concat("VSMS_MESSAGE_QUERY_RES.messageCount=\'"+intCounter.toString()+"\';");
			}
		} catch (Exception e) {
			logger.error("SQL Exception" + e +"Query="+vSMSMessageQuery);
			logger.error("No Row Found");
			
			responseString = responseString.concat("VSMS_MESSAGE_QUERY_RES.messageCount=\'"+"0"+"\';");
			dbError="Y";
			e.printStackTrace();
		}
		responseString = responseString.concat("VSMS_MESSAGE_QUERY_RES.dbError=\'"+dbError+"\';");
		return responseString;
	}
}
	
	

