package com.santosh.greenzone.wapchatezee.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

import com.santosh.greenzone.services.impl.PostClientCrbtServiceImpl;
import com.santosh.greenzone.utils.ChatUtils;


@CrossOrigin
@RestController
public class UserDtmfHandler {
	private static final Logger logger = LogManager.getLogger(UserDtmfHandler.class);
	@Autowired
	Environment env;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	@RequestMapping(value = "/userDtmfHandler",method = RequestMethod.GET)
	@ResponseBody
	public String getToneIdInforXML(@RequestParam("serviceId") String serviceId,@RequestParam("productId") String productId,@RequestParam("aparty") String aparty, @RequestParam("bparty") String bparty,@RequestParam("toneId") String toneId,
			@RequestParam("callStartTime") String callStartTime,@RequestParam("callEndTime") String callEndTime,@RequestParam("digits") String digits,HttpServletRequest req,
			HttpServletResponse res) {
		
		logger.info("userDtmfHandler|aparty=" + aparty+"|bparty="+bparty+"|toneId="+toneId+"|callStartTime="+callStartTime+"|callEndTime="+callEndTime+"|digits="+digits+"|");
		logger.trace("Query=" + env.getProperty("SQL23_USER_TONE_INFO"));
		logger.trace("Öperator=" + env.getProperty("OPERATOR_NAME"));
		logger.trace("Default_Tone_Id=" + env.getProperty("DEFAULT_TONE_ID"));
		String starToCopy="N";
		String  interfaceName="star2copy";
		String postClientRes ="";
		String coreEngineSubBillingUrl = env.getProperty("CORE_ENGINE_SUBS_BILL_URL");
		logger.trace("subscriptionBillingReq|coreEngine SubBillingUrl="+coreEngineSubBillingUrl);
		if(digits.contains("*")) {
			System.out.println("userDtmfHandler|Star is present in digits");
			starToCopy="Y";
			
			UUID transactionId = UUID.randomUUID();
			SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMddHHmmss");
			Date now = new Date();
			String strDate= sdfDate.format(now);
			
			String jsonCrbtBodyData = "{\r\n" +
					" \"cpid\": \"CRBT\",\r\n"+
	                " \"msisdn\": \"" +aparty +"\",\r\n" +
					" \"tid\": \"" +transactionId+ "\",\r\n"+
	                " \"action\" :\"SUBSCRIPTION\",\r\n"+
	                " \"serviceid\" :\"SUBS_RENTAL\",\r\n"+
	                " \"productid\" :\""+productId+"\",\r\n"+
	                " \"langid\" :\"en\",\r\n"+
	                " \"interfacename\" :\""+interfaceName+"\",\r\n"+
	                " \"timestamp\" :\"" +strDate+ "\",\r\n"+
	                " \"issubcharge\" :\"Y\",\r\n"+
	                " \"toneid\" :\"" +toneId+"\",\r\n"+
	                " \"tonetype\" :\"0\",\r\n"+
	                " \"tonetypeidx\" :\"1\",\r\n"+
	                " \"tonename\" :\"" +env.getProperty("DEFAULT_TONE_NAME")+"\",\r\n"+
	                " \"precrbtflag\" :\"\",\r\n"+
	                " \"callingpartynumber\" :\"D\",\r\n"+
	                " \"toneserviceid\" :\"TONE_RENTAL\",\r\n"+
	                " \"toneproductid\" :\"TONE_LIFETIME\",\r\n"+
	                " \"istonecharge\" :\"N\"\r\n"+
	                "}";
			logger.info("jsonCrbtBodyData="+jsonCrbtBodyData);
			logger.trace("CRBT Case: We are not hitting third party URL");
			PostClientCrbtServiceImpl postClientCrbtService = new PostClientCrbtServiceImpl();
			postClientRes = postClientCrbtService.sendPostClientCrbtReq(coreEngineSubBillingUrl, jsonCrbtBodyData, "subscribe");
			logger.info("third Party Profile Response="+postClientRes);
			
			
			
		}
		
		// Replace Table Index & bparty 
		String insertQuery = ChatUtils.getTonePlayerDtmfInsertQuery(env.getProperty("SQL24_TONE_PLAYER_DTMF"), aparty,bparty,toneId,digits,callStartTime,callEndTime,starToCopy);
		
		//System.out.println("final SQL Query="+insertQuery);
				
		try {
			int insertQueryResult= jdbcTemplate.update(insertQuery);
			if(insertQueryResult <= 0)
			{
				logger.error("Failed to insert into accout table");
			}else {
				logger.info("Successfully to insert into CRBT_SUBS_TONE_DTMF_INFO |resultChangesRow="+insertQueryResult);
			}
			
		} catch (Exception e) {
			logger.error("SQL Exception" + e + "Query=" + insertQuery);
			logger.error("No Row Insert into  CRBT_SUBS_TONE_DTMF_INFO");
			e.printStackTrace();
		}
			
	
		String responseString = new String("DTFM_Res.result=\'Ok Accepted\';");
		
	
		return responseString;
	}
}
