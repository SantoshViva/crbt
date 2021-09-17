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
public class SdpToneChangeController {

	private static final Logger logger = LogManager.getLogger(SdpToneChangeController.class);
	@Autowired
	Environment env;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	@RequestMapping(value = "/sdpToneChangeReq",method = RequestMethod.GET)
	@ResponseBody
	public String sdpToneChangeHandler(@RequestParam("msisdn") String msisdn,@RequestParam("callingParty") String callingParty,@RequestParam("toneId") String toneId,@RequestParam("action") String action,@RequestParam("toneType") String toneType,@RequestParam("toneTypeIdx") String toneTypeIdx,@RequestParam("songName") String songName,@RequestParam("serviceCode") String serviceCode, HttpServletRequest req,
			HttpServletResponse res) {
		
		
		    String returnValue="success";
			logger.info("sdpToneChangeHandler|msisdn="+msisdn+"|callingParty="+callingParty+"|toneId="+toneId+"|action="+action+"|toneType="+toneType+"|toneTypeIdx="+toneTypeIdx+"|songName="+songName+"|serviceCode="+serviceCode);
			String insertQueryFleg="true";
			if(songName == null)
			{		
				String selectQuery = ChatUtils.getQuery(env.getProperty("SQL35_SELECT_SONG_NAME_DETAIL"), toneId);
			
				logger.info("final Query="+selectQuery);
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
			}	
			/**End songName**/
			
				
			
			if(action.compareToIgnoreCase("insert") == 0 || action.compareToIgnoreCase("add") == 0)
			{
				String status="A";
				String insertToneInfoQuery = ChatUtils.insertToneInfoQuery(env.getProperty("SQL32_INSERT_TONE_PROV_INFO"),msisdn,toneType,toneTypeIdx,callingParty,toneId,status,songName);
				logger.info("final tone insert query|SQL32_INSERT_TONE_PROV_INFO="+insertToneInfoQuery);
				try {
					int insertQueryResult= jdbcTemplate.update(insertToneInfoQuery);
					if(insertQueryResult <= 0)
					{
						logger.error("Failed to insert|query="+insertQueryResult);
					}else {
						
						logger.info("Successfully to insert |resultChangesRow="+insertQueryResult);
					}
					logger.info("insert new tone successfully");
				}catch(Exception e)
				{
					logger.error("Exception occurred insert case|SQL exception="+e);
					//e.printStackTrace();
					insertQueryFleg="false";
					logger.info("insert new tone faild");
				}
				
				
			}
			if (action.compareToIgnoreCase("change") == 0 || action.compareToIgnoreCase("update") == 0 ||insertQueryFleg.compareToIgnoreCase("false")==0)
			{
				logger.info("update existing toneId");
				String status="A";
				String updateToneInfoQuery=ChatUtils.insertToneInfoQuery(env.getProperty("SQL32_UPDATE_TONE_PRO_INFO"),msisdn,toneType,toneTypeIdx,callingParty,toneId,status,songName);
				logger.info("SQL32_UPDATE_TONE_PRO_INFO="+updateToneInfoQuery);
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
			}else if(action.compareToIgnoreCase("delete") == 0 || action.compareToIgnoreCase("remove") == 0) {
				/**delete a particular tone*/
			}else if(action.compareToIgnoreCase("grace") == 0) {
				/**move toneId to grace*/
			}else if(action.compareToIgnoreCase("suspend") == 0) {
				/**move toneId to suspend*/
			}else {
				logger.info("default case");
			}
			
			return returnValue;
		
	}
	
	
}
