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


@CrossOrigin
@RestController
public class UnSubscriptionController {

	private static final Logger logger = LogManager.getLogger(UnSubscriptionController.class);
	@Autowired
	Environment env;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	@RequestMapping(value = "/unSubscriptionReq",method = RequestMethod.GET)
	@ResponseBody
	public String getToneIdInforXML(@RequestParam("serviceId") String serviceId,@RequestParam("subServiceId") String subServiceId,@RequestParam("offer") String offer,@RequestParam("aparty") String aparty, @RequestParam("bparty") String bparty, HttpServletRequest req,
			HttpServletResponse res) {
		
			logger.info("unSubscriptionReq|aparty="+aparty+"|bparty="+bparty+"|offer="+offer+"|serviceId="+serviceId+"|subServiceId="+subServiceId);
			
			/**HTTP URL Hit for Third Party**/
			String thirdPartyUnSubUrl = env.getProperty("THIRD_PARTY_UNSUBS_URL");
			logger.trace("unSubscriptionReq|thirdPartySubBillingUrl"+thirdPartyUnSubUrl);
			/**HTTP URL Hit for Core Engine**/
			String coreEngineUnSubUrl = env.getProperty("CORE_ENGINE_UNSUBS_URL");
			logger.info("unSubscriptionReq|coreEngineUnSubUrl"+coreEngineUnSubUrl);
			
			/**Create Unique Transaction Id**/
			UUID transactionId = UUID.randomUUID(); 
			/**Find the current System date time**/
			SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMddHHmmss");
			Date now = new Date();
			String strDate= sdfDate.format(now);
			/* System.out.print("Date="+strDate); */
			
			String jsonBodyData = "{\r\n" +
	                "  \"msisdn\": \"" +aparty +"\",\r\n" +
	                "  \"offer\": \"" +offer +"\"\r\n" +
	                "}";
			logger.info("jsonBodyData="+jsonBodyData);
			String postClientRes="";
			if(serviceId.compareToIgnoreCase("crbt") == 0)
			{
				String jsonBodyCrbtData = "{\r\n" +
						" \"msisdn\": \"" +aparty +"\",\r\n" +
						" \"tid\": \""+transactionId+"\",\r\n"+
		                " \"action\" :\"UNSUBSCRIPTION\",\r\n"+
		                " \"serviceid\" :\"SUBS_RENTAL\",\r\n"+
		                " \"productid\" :\"CRBT_Weekly\",\r\n"+
		                " \"langid\" :\"en\",\r\n"+
		                " \"interfacename\" :\"IVR\",\r\n"+
		                " \"timestamp\" :\""+strDate+"\"\r\n"+
		                "}";
				
				logger.info("jsonBodyCrbtData="+jsonBodyCrbtData);
				logger.info("CRBT Case: We are not hitting third party URL");	
				PostClientCrbtServiceImpl postClientCrbtService = new PostClientCrbtServiceImpl();
				postClientRes = postClientCrbtService.sendPostClientCrbtReq(coreEngineUnSubUrl, jsonBodyCrbtData, "unsubscribe");
				logger.info("postClientRes="+postClientRes);
			}else
			{
				PostClientServiceImpl postClientService = new PostClientServiceImpl();
				postClientRes=postClientService.sendPostClientReq(thirdPartyUnSubUrl, jsonBodyData,"unsubscribe");
				logger.info("third Party Profile Response="+postClientRes);
			}	
			
			/**Update in local database for subscriber status*/
			String subStatus="N";
			String updateSubProfile=ChatUtils.updateSubProfileQuery(env.getProperty("SQL30_UPDATE_UNSUB_PROFILE"),aparty,bparty,subStatus,offer);
			logger.info("updateSubProfile="+updateSubProfile);
			try
			{
				int updateSubProfileResult=jdbcTemplate.update(updateSubProfile);
				if(updateSubProfileResult<=0)
				{
					logger.info("Fail to update subscriber profile|result="+updateSubProfileResult);
				}else {
					logger.info("Successful update subscriber profile|result="+updateSubProfileResult+"|aparty="+aparty+"|serviceId="+serviceId);
				}
			}catch(Exception e) {
				logger.error("Exception occurred="+e);
			}
			/**For CRBT service : Delete tone all information*/
			if(serviceId.compareToIgnoreCase("crbt") == 0)
			{
				String deleteToneInfo=ChatUtils.updateSubProfileQuery(env.getProperty("SQL33_DELETE_ALL_TONE_INFO"),aparty,bparty,"","");
				logger.info("deleteToneInfo="+deleteToneInfo);
				try
				{
					int deleteToneInfoResult=jdbcTemplate.update(deleteToneInfo);
					if(deleteToneInfoResult<=0)
					{
						logger.info("Fail to delete CRBT_SUBS_TONE_PROV_INFO table|result="+deleteToneInfoResult);
					}else {
						logger.info("Successful delete CRBT_SUBS_TONE_PROV_INFO table|result="+deleteToneInfoResult+"|aparty="+aparty+"|serviceId="+serviceId);
					}
				}catch(Exception e) {
					logger.error("Exception occurred="+e);
				}
			}
			
			/**Remove all tone information*/
			/**End database updatation*/
			
			String responseString = new String();
			String dbError ="N";
			responseString = responseString.concat("RESPONSE.dbError=\'"+"N"+"\';");
			responseString = responseString.concat("RESPONSE.result=\'"+postClientRes+"\';");
			
			logger.info("responseString="+responseString);
			return "";
		
	}
	
	
}
