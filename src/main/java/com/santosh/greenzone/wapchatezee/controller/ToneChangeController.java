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
import com.santosh.greenzone.wapchatezee.model.UserProfileInfo;

@CrossOrigin
@RestController
public class ToneChangeController {

	private static final Logger logger = LogManager.getLogger(ToneChangeController.class);
	@Autowired
	Environment env;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	@RequestMapping(value = "/toneChangeReq",method = RequestMethod.GET)
	@ResponseBody
	public String toneChangeHandler(@RequestParam("aparty") String aparty,@RequestParam("bparty") String bparty,@RequestParam("serviceId") String serviceId,@RequestParam("toneId") String toneId,@RequestParam("action") String action,@RequestParam("toneType") String toneType,@RequestParam("toneTypeIdx") String toneTypeIdx,@RequestParam("toneServiceId") String toneServiceId,@RequestParam("toneProductId") String toneProductId,@RequestParam("isToneCharge") String isToneChange,@RequestParam("callingParty") String callingParty,HttpServletRequest req,
			HttpServletResponse res) {
		
		    toneId=toneId.replace(".wav", "");
			logger.info("toneChangeReq|aparty="+aparty+"|bparty="+bparty+"|serviceId="+serviceId+"|toneId="+toneId+"|action="+action+"|toneType="+toneType+"|toneTypeIdx="+toneTypeIdx+"|toneServiceId="+toneServiceId+"|toneProductId="+toneProductId+"|isToneChange="+isToneChange+"|callingParty="+callingParty);
			
			/**Create Unique Transaction Id**/
			UUID transactionId = UUID.randomUUID(); 
			/**Find the current System date time**/
			SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMddHHmmss");
			Date now = new Date();
			String strDate= sdfDate.format(now);
			/* System.out.print("Date="+strDate); */
			
			/**Start Find the songName **/
			logger.trace("toneId="+toneId);
			String selectQuery = ChatUtils.getQuery(env.getProperty("SQL35_SELECT_SONG_NAME_DETAIL"), toneId);
			String songName="";
			logger.trace("final Query="+selectQuery);
			try {
				List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(selectQuery);
				if(queryForList.isEmpty())
				{
						songName="mySong";
						logger.error("No song Name is available in the database|songName="+songName);
				}
				else 
				{
					for (Map<String, Object> row : queryForList) {
						
					    songName=row.get("songname").toString();
						logger.info("User Status ="+row.get("songName").toString() );
						break;
					}
				}
				
			}catch(Exception e) {
				e.printStackTrace();
			}
			/**End songName**/
			
			
			
			//System.out.print("unique Id="+transactionId);
			/**HTTP URL Hit for Third Party**/
			
			String jsonCrbtBodyData = "{\r\n" +
					" \"cpid\": \"CRBT\",\r\n"+
	                " \"msisdn\": \"" +aparty +"\",\r\n" +
					" \"tid\": \""+transactionId+"\",\r\n"+
	                " \"action\" :\"" +action +"\",\r\n"+
	                " \"langid\" :\"en\",\r\n"+
	                " \"interfacename\" :\"IVR\",\r\n"+
	                " \"toneid\" :\"" +toneId +"\",\r\n"+
	                " \"timestamp\" :\""+strDate+"\",\r\n"+
	                " \"oldtonetype\" :\""+toneType+"\",\r\n"+
	                " \"oldtonetypeidx\" :\""+toneTypeIdx+"\",\r\n"+
	                " \"tonetype\" :\"" +toneType +"\",\r\n"+
	                " \"tonetypeidx\" :\"" +toneTypeIdx +"\",\r\n"+
	                " \"tonename\" :\"" +songName +"\",\r\n"+
	                " \"callingpartynumber\" :\"" +callingParty +"\",\r\n"+
	                " \"toneserviceid\" :\"" +toneServiceId +"\",\r\n"+
	                " \"toneproductid\" :\""+toneProductId+"\",\r\n"+                                   	                
	               " \"istonecharge\" :\"" +isToneChange +"\"\r\n"+
	                "}";
			logger.info("jsonCrbtBodyData="+jsonCrbtBodyData);
			String coreEngineToneChangeUrl = env.getProperty("CORE_ENGINE_TONE_CHANGE_URL");
			logger.trace("toneChangeReq|coreEngineToneChangeUrl"+coreEngineToneChangeUrl);
			PostClientCrbtServiceImpl postClientCrbtService = new PostClientCrbtServiceImpl();
			String postClientRes = postClientCrbtService.sendPostClientCrbtReq(coreEngineToneChangeUrl, jsonCrbtBodyData, "tone");
			logger.info("aparty="+aparty+"|postClientRes="+postClientRes);
			/**End CoreEngine URL Hit*/
			String songPath="songPath";
			if(action.compareToIgnoreCase("insert") == 0)
			{
				logger.trace("insert new tone");
			}else {
				logger.trace("update existing toneId");
				String status="A";
				String updateToneInfoQuery=ChatUtils.insertToneInfoQuery(env.getProperty("SQL32_UPDATE_TONE_PRO_INFO"),aparty,toneType,toneTypeIdx,callingParty,toneId,status,songName,toneServiceId,songPath);
				logger.trace("Query="+updateToneInfoQuery);
				try
				{
					int updateResult=jdbcTemplate.update(updateToneInfoQuery);
					if(updateResult<=0)
					{
						logger.info("Fail to update subscriber profile|result="+updateResult);
					}else {
						logger.info("Successful update subscriber profile|result="+updateResult);
					}
				}catch(Exception e) {
					logger.error("Exception occurred="+e);
				}
			}
			String responseString = new String();
			String responseResult="OK";
			responseString = responseString.concat("RESPONSE.result=\'"+responseResult+"\';");
			return responseString;
		
	}
	
	
}
