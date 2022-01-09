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

import com.santosh.greenzone.services.impl.PostClientCrbtServiceImpl;
import com.santosh.greenzone.services.impl.PostClientServiceImpl;
import com.santosh.greenzone.utils.ChatUtils;
import com.santosh.greenzone.wapchatezee.model.ToneInfo;

@CrossOrigin
@RestController
public class SubscriptionBillingController {

	private static final Logger logger = LogManager.getLogger(SubscriptionBillingController.class);
	@Autowired
	Environment env;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	@RequestMapping(value = "/subscriptionBillingReq",method = RequestMethod.GET)
	@ResponseBody
	public String getToneIdInforXML(@RequestParam("serviceId") String serviceId,@RequestParam("productId") String productId,@RequestParam("offer") String offer,@RequestParam("aparty") String aparty, @RequestParam("bparty") String bparty,@RequestParam("station") String station, HttpServletRequest req,
			HttpServletResponse res) {
		
			logger.info("subscriptionBillingReq|aparty="+aparty+"|bparty="+bparty+"|offer="+offer+"|station="+station+"|serviceId="+serviceId+"|productId="+productId);
			
			
			/**In Case of CRBT**/
			   if(serviceId.compareToIgnoreCase("crbt") == 0)
			   {
				   logger.info("CRBT Case");
				   
				   
			   }
			
			/*End**/
			
			
			
			/**HTTP URL Hit for Third Party**/
			String thirdPartySubBillingUrl = env.getProperty("THIRD_PARTY_SUBS_BILL_URL");
			logger.trace("subscriptionBillingReq|thirdPartySubBillingUrl"+thirdPartySubBillingUrl);
			String coreEngineSubBillingUrl = env.getProperty("CORE_ENGINE_SUBS_BILL_URL");
			logger.trace("subscriptionBillingReq|coreEngine SubBillingUrl="+coreEngineSubBillingUrl);
			
			String jsonBodyData = "{\r\n" +
	                "  \"msisdn\": \"" +aparty +"\",\r\n" +
	                "  \"offer\": \"" +offer +"\"\r\n" +
	                "}";
			logger.trace("jsonBodyData="+jsonBodyData);
			String songName="Default Tone";
			String postClientRes ="";
			if(serviceId.compareToIgnoreCase("crbt") == 0)
			{
				
				/**Create Unique Transaction Id**/
				UUID transactionId = UUID.randomUUID(); 
				/**Find the current System date time**/
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
		                " \"interfacename\" :\"IVR\",\r\n"+
		                " \"timestamp\" :\"" +strDate+ "\",\r\n"+
		                " \"issubcharge\" :\"Y\",\r\n"+
		                " \"toneid\" :\"" +env.getProperty("DEFAULT_TONE_ID")+"\",\r\n"+
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
			}
			else
			{
				PostClientServiceImpl postClientService = new PostClientServiceImpl();
				postClientRes=postClientService.sendPostClientReq(thirdPartySubBillingUrl, jsonBodyData,"subscribe");
				logger.info("third Party Profile Response="+postClientRes);
			}
			
			/**Update in local database for subscriber status & radio station*/
			String subStatus="Y";
			String updateSubProfile=ChatUtils.updateSubProfileQuery(env.getProperty("SQL28_UPDATE_SUB_PROFILE"),aparty,bparty,subStatus,offer);
			try
			{
				if(postClientRes.equalsIgnoreCase("A") || postClientRes.equalsIgnoreCase("F"))
				{
					logger.error("aparty="+aparty+"|Error Subscription API Failed|No Database update on local DB");
				}else{					
						int updateSubProfileResult=jdbcTemplate.update(updateSubProfile);
						if(updateSubProfileResult<=0)
						{		
							logger.info("Fail to update subscriber profile|result="+updateSubProfileResult);
						}else {
							logger.info("Successful update subscriber profile|result="+updateSubProfileResult);
						}
				}	
			}catch(Exception e) {
				logger.error("Exception occurred="+e);
			}
			
			/**insert default song ***/
			/*
			if(serviceId.compareToIgnoreCase("crbt") == 0 && !(postClientRes.equalsIgnoreCase("A") || postClientRes.equalsIgnoreCase("F")))
			{
				String toneType="0";
				String toneTypeIdx="1";
				String callingParty="D";
				String status="A";
				String insertToneInfoQuery = ChatUtils.insertToneInfoQuery(env.getProperty("SQL32_INSERT_TONE_PROV_INFO"),aparty,toneType,toneTypeIdx,callingParty,env.getProperty("DEFAULT_TONE_ID"),status,songName);
				logger.info("final tone insert query="+insertToneInfoQuery);
				try {
					int insertQueryResult= jdbcTemplate.update(insertToneInfoQuery);
					if(insertQueryResult <= 0)
					{
						logger.error("Failed to insert|query="+insertQueryResult);
					}else {
						
						logger.info("Successfully to insert |resultChangesRow="+insertQueryResult);
					}
				}catch(Exception e)
				{
					logger.error("Exception occurred|Query="+insertToneInfoQuery+"|SQL exception="+e);
					e.printStackTrace();
				}
			}
			*/	
			/****/
			String responseString = new String();
			String dbError ="N";
			responseString = responseString.concat("SUBSCRIPTION_BILLING_RES.dbError=\'"+"N"+"\';");
			responseString = responseString.concat("SUBSCRIPTION_BILLING_RES.subStatus=\'"+postClientRes+"\';");
			
			return responseString;
		
	}
	
	
}
