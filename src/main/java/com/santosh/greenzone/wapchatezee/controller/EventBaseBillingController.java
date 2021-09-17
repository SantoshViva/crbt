package com.santosh.greenzone.wapchatezee.controller;

import java.net.URI;
import java.text.DateFormat;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.santosh.greenzone.services.impl.PostClientCrbtServiceImpl;
import com.santosh.greenzone.utils.ChatUtils;







@CrossOrigin
@RestController
public class EventBaseBillingController {

	private static final Logger logger = LogManager.getLogger(EventBaseBillingController.class);
	@Autowired
	Environment env;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	@RequestMapping(value = "/eventBillingReq",method = RequestMethod.GET)
	@ResponseBody
	public String eventBaseBillingController(@RequestParam("aparty") String aparty,@RequestParam("bparty") String bparty,@RequestParam("vMsisdn") String vMsisdn,@RequestParam("interfaceId") String interfaceId,@RequestParam("action") String action,@RequestParam("serviceId") String serviceId,@RequestParam("productId") String productId,@RequestParam("recordingFilePath") String recordingFilePath,@RequestParam("duration") String duration, HttpServletRequest req,
			HttpServletResponse res) {
		
			logger.info("eventBillingReq|aparty="+aparty+"|bparty="+bparty+"|vMsisdn="+vMsisdn+"|interfaceId="+interfaceId+"|action="+action+"|serviceId="+serviceId+"|productId="+productId+"|duration="+duration+"|recordingFilePath="+recordingFilePath);
			
			
			/**Check recording file path*/
			String newRecordingFilePath="";
			String srcFilePath=recordingFilePath;
			srcFilePath=srcFilePath.replace(env.getProperty("TS_RECORDING_HTTP_PATH"), env.getProperty("TS_RECORDING_BASE_PATH"));
			
			Date currDate = new Date();
			String basePath=env.getProperty("RECORDING_BASE_PATH");
			DateFormat currYearFormat = new SimpleDateFormat("yy");
			String currYear = currYearFormat.format(currDate);
			DateFormat currMonthFormat = new SimpleDateFormat("MM");
			String currMonth= currMonthFormat.format(currDate);
			DateFormat currDateFormat = new SimpleDateFormat("dd");
			String todayDate= currDateFormat.format(currDate);
			
			DateFormat currDateTimeFormat= new SimpleDateFormat("ddHHmmss");
			String currDateTime = currDateTimeFormat.format(currDate);
			logger.info("currYear="+currYear+"|currMonth="+currMonth+"|currDate="+currDate+"|currDateTime="+currDateTime);
			String recordingFileName=aparty.substring(aparty.length()-4)+vMsisdn.substring(vMsisdn.length()-4)+currDateTime+".wav";
			logger.info("aa="+aparty.substring(aparty.length()-4)+"|vm="+vMsisdn.substring(vMsisdn.length()-4)+"|dd="+(Integer.parseInt(todayDate.toString())/10)+"|recordingFileName="+recordingFileName);
			String destFilePath= basePath+"/"+currYear+"/"+currMonth+"/"+(Integer.parseInt(todayDate.toString())/10)+"/"+recordingFileName;
			String command = "cp " + srcFilePath +" " + destFilePath;
			logger.info("srcFilePath="+srcFilePath+"|destFilePath="+destFilePath+"|command="+command);
			newRecordingFilePath= destFilePath;
			newRecordingFilePath=newRecordingFilePath.replace(basePath, env.getProperty("RECORDING_HTTP_PATH"));
			logger.info("newRecordingFilePath="+newRecordingFilePath);
			String returnLinuxCommandResult="-1";
			try {
				returnLinuxCommandResult=ChatUtils.runLinuxCommand(command);
				logger.info("command="+command+"|result="+returnLinuxCommandResult);
			}catch(Exception e) {
				logger.error("Error to execute linux command|Not Successfully copy recording file|Exception="+e);
			}
			
			
			/**End copy recording in local path*/	
												
			/**HTTP URL Hit for Third Party**/
			
			String coreEngineEventBillingUrl = env.getProperty("CORE_ENGINE_EVENT_BILL_URL");
			logger.info("subscriptionBillingReq|coreEngineSubBillingUrl"+coreEngineEventBillingUrl);
			String postClientRes ="";
			String dbError ="N";
			String responseString = new String();
				/**Create Unique Transaction Id**/
				UUID transactionId = UUID.randomUUID(); 
				/**Find the current System date time**/
				SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMddHHmmss");
				Date now = new Date();
				String chargingTime= sdfDate.format(now);
				String isCharging="Y";
				//String action="EVENT";
				String jsonCrbtBodyData = "{\r\n" +
						" \"cpid\": \"CRBT\",\r\n"+
		                " \"msisdn\": \"" +aparty +"\",\r\n" +
						" \"tid\": \"" +transactionId+ "\",\r\n"+
		                " \"action\" :\""+action+"\",\r\n"+
		                " \"serviceid\" :\""+serviceId+"\",\r\n"+
		                " \"productid\" :\""+productId+"\",\r\n"+
		                " \"langid\" :\"en\",\r\n"+
		                " \"interfacename\" :\"" +interfaceId+ "\",\r\n"+
		                " \"timestamp\" :\"" +chargingTime+ "\",\r\n"+
		                " \"issubcharge\" :\"" +isCharging+ "\",\r\n"+
		                " \"callingpartynumber\" :\"D\"\r\n"+
		                "}";
				
				logger.info("Event Billing jsonBodyData="+jsonCrbtBodyData);
				
				PostClientCrbtServiceImpl postClientCrbtService = new PostClientCrbtServiceImpl();
				postClientRes = postClientCrbtService.sendPostClientCrbtReq(coreEngineEventBillingUrl, jsonCrbtBodyData, "event-billing");
								
				logger.info("postClientRes="+postClientRes);
			/** ***/
				String result="SUCCESS";
				String errorCode=postClientRes;
				if(postClientRes.equalsIgnoreCase("0")||postClientRes.equalsIgnoreCase("Y"))
				{
					result="SUCCESS";
					errorCode="0";
				}
				else
				{
					result="FAIL";
					logger.info("postClientRes="+postClientRes+"|result="+result+"|errorCode="+errorCode);
					postClientRes="-1";
				}
				
			    
			    
				String insertEventBillingCdrQuery = ChatUtils.insertEventBaseChargingQuery(env.getProperty("SQL37_INSERT_VSMS_BILLING_CDR"),transactionId.toString(),aparty,vMsisdn,interfaceId, action,serviceId, productId, isCharging, result, errorCode);
				
				logger.info("final event billing insert query="+insertEventBillingCdrQuery);
				try {
					int insertQueryResult= jdbcTemplate.update(insertEventBillingCdrQuery);
					if(insertQueryResult <= 0)
					{
						logger.error("Failed to insert|query="+insertQueryResult);
					}else {
						
						logger.info("Successfully to data insert in  vms_billing_cdr |resultChangesRow="+insertQueryResult);
					}
				}catch(Exception e)
				{
					logger.error("Exception occurred|Query="+insertEventBillingCdrQuery+"|SQL exception="+e);
					e.printStackTrace();
					dbError="Y";
				}
				if(result.equalsIgnoreCase("FAIL"))
				{
					responseString = responseString.concat("EVENT_BILLING_RES.dbError=\'"+dbError+"\';");
					responseString = responseString.concat("EVENT_BILLING_RES.result=\'"+postClientRes+"\';");
					
					return responseString;
				}
				/**insert details for voice sms**/
				String messageStatus="A";
				String insertVoiceSMSInfoQuery = ChatUtils.insertVsmsMessageDetailsQuery(env.getProperty("SQL38_INSERT_VSMS_MESSAGE_DETAILS"),"23243432", aparty,vMsisdn,interfaceId,messageStatus,duration,newRecordingFilePath);
				logger.info("fine voice sms info query="+insertVoiceSMSInfoQuery);
				try {
					int insertQueryResult= jdbcTemplate.update(insertVoiceSMSInfoQuery);
					if(insertQueryResult <= 0)
					{
						logger.error("Failed to insert|query="+insertQueryResult);
					}else {
						
						logger.info("Successfully to data insert in  vsms_message_details |resultChangesRow="+insertQueryResult);
					}
					
				}catch(Exception e) {
					logger.error("Exception occurred|Query="+insertVoiceSMSInfoQuery+"|SQL exception="+e);
					e.printStackTrace();
					dbError="Y";
				}
			/**send SMS to User**/
				
				/**Find date & time*/
				Date date = new Date();
				String timeFormatString = "hh:mm:ss";
				String dateFormatString = "EEE, dd MMM yyyy";
				DateFormat timeFormat = new SimpleDateFormat(timeFormatString);
				String currentTime = timeFormat.format(date);
				DateFormat dateFormat = new SimpleDateFormat(dateFormatString);
				String currentDate = dateFormat.format(date);
				logger.info("currentDate="+currentDate+"|currentTime="+currentTime);
				String vsmsRecordingSMS= ChatUtils.getSMSText(env.getProperty("VSMS_RECORDING_SMS"), aparty,currentDate,currentTime);
				logger.info("final vsmsRecordingSMS="+vsmsRecordingSMS);
				vsmsRecordingSMS=vsmsRecordingSMS.replace(" ", "%20");
				logger.info("final vsmsRecordingSMS="+vsmsRecordingSMS);
				String smsUrl = env.getProperty("SMS_SEND_URL");
				logger.info("EventBaseBilling|smsUrl="+smsUrl);
				smsUrl = smsUrl +"&to=%2B252"+vMsisdn+"&text="+vsmsRecordingSMS ;
				logger.info("EventBaseBilling|smsUrl="+smsUrl);
				/**Hit RestFul Api*/
				try {
					URI smsUri = new URI(smsUrl);
					RestTemplate restTemplate = new RestTemplate();
					ResponseEntity<String> smsUrlResult = restTemplate.getForEntity(smsUri, String.class);
					HttpStatus statusCode= smsUrlResult.getStatusCode();
					logger.info("reuslt="+smsUrlResult);
					logger.info("statusCode="+statusCode+"|"+smsUrlResult.getStatusCodeValue());
					if(smsUrlResult.getStatusCodeValue()==202||smsUrlResult.getStatusCodeValue()==200) {
						logger.info("send SMS successfully|aparty="+aparty+"|vMsisdn="+vMsisdn);
					}
				}catch(Exception e) {
					logger.error("Exception="+e);
				}
			/****/
			
			
			responseString = responseString.concat("EVENT_BILLING_RES.dbError=\'"+dbError+"\';");
			responseString = responseString.concat("EVENT_BILLING_RES.result=\'"+postClientRes+"\';");
			
			return responseString;
		
	}
	
	
}
