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
import com.santosh.greenzone.wapchatezee.model.VoiceSmsMessageDetails;



@CrossOrigin
@RestController
public class DeleteVoiceSms {

	private static final Logger logger = LogManager.getLogger(DeleteVoiceSms.class);
	@Autowired
	Environment env;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	@RequestMapping(value = "/deleteVoiceSms",method = RequestMethod.GET)
	@ResponseBody
	public String deleteVoiceSMS(@RequestParam("aparty") String aparty,@RequestParam("senderMsisdn") String senderMsisdn,@RequestParam("voiceMessageId") String voiceMessageId,@RequestParam("recordingFilePath") String recordingFile, HttpServletRequest req,
			HttpServletResponse res) {
		
		logger.info("deleteVoiceSms|aparty="+aparty+"|senderMsidn="+senderMsisdn+"|voiceMessageId="+voiceMessageId+"|recordingFile="+recordingFile);
		/**More data from Voice SMS table to Backup table*/
		String vSMSMesageBackupQuery=ChatUtils.getQuery(env.getProperty("SQL40_SELECT_BACKUP_VSMS_MESSAGE"), voiceMessageId);
		
		/***/
		String responseString = new String();
		String result="Y";
		logger.info("final deleteVoiceSms Query="+vSMSMesageBackupQuery);
		
		VoiceSmsMessageDetails data = new VoiceSmsMessageDetails();
		
		
		try {
			List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(vSMSMesageBackupQuery);
			
			if(queryForList.isEmpty())
			{
				logger.info("No Record Found in SQL|aparty="+aparty);
				result="N";
							
																
			}else 
			{
				for (Map<String, Object> row : queryForList) {
					logger.info("for loop");
					data.setId(Integer.parseInt(row.get("id").toString()));
					data.setSubscriber_id(row.get("subscriber_id").toString());
					data.setV_msisdn(row.get("v_msisdn").toString());
					data.setInterface_id(row.get("interface_id").toString());
					data.setStatus(row.get("status").toString());
					data.setDuration(row.get("duration").toString());
					data.setSend_date(row.get("send_date").toString());
					if(row.get("listening_date")!=null)
						data.setListening_date(row.get("listening_date").toString());
					data.setRecording_path(row.get("recording_path").toString());
					
					result=saveVoiceSMSMessage(data,voiceMessageId);
					logger.info("aparty="+aparty+"|result="+result);			
				}
				
			}
		}catch(NullPointerException en) {
			logger.error("Null Pointer Exception="+en);
			result="N";
		}catch (Exception e) {
			logger.error("SQL Exception" + e +"Query="+vSMSMesageBackupQuery);
			logger.error("No Row Found");
			
			e.printStackTrace();
			result="N";
		} 
		responseString = responseString.concat("DELETE_VSMS_RES.result=\'"+result+"\';");
		return responseString;
	}
	
	private String saveVoiceSMSMessage(VoiceSmsMessageDetails saveData, String voiceMessageId)
	{
		String vSMSMesageBackupInsertQuery=env.getProperty("SQL41_INSERT_BACKUP_VSMS_MESSAGE");
		String result="Y";
		vSMSMesageBackupInsertQuery =vSMSMesageBackupInsertQuery.replace("{transactionId}", Integer.toString(saveData.getId()));
		vSMSMesageBackupInsertQuery =vSMSMesageBackupInsertQuery.replace("{subscriberId}", saveData.getSubscriber_id());
		vSMSMesageBackupInsertQuery =vSMSMesageBackupInsertQuery.replace("{vMsisdn}", saveData.getV_msisdn());
		vSMSMesageBackupInsertQuery =vSMSMesageBackupInsertQuery.replace("{interfaceId}", saveData.getInterface_id());
		vSMSMesageBackupInsertQuery =vSMSMesageBackupInsertQuery.replace("{status}", saveData.getStatus());
		vSMSMesageBackupInsertQuery =vSMSMesageBackupInsertQuery.replace("{duration}", saveData.getDuration());
		vSMSMesageBackupInsertQuery =vSMSMesageBackupInsertQuery.replace("{sendDate}", saveData.getSend_date());
		//vSMSMesageBackupInsertQuery =vSMSMesageBackupInsertQuery.replace("{listeningDate}", saveData.getListening_date());
		vSMSMesageBackupInsertQuery =vSMSMesageBackupInsertQuery.replace("{recordingPath}", saveData.getRecording_path());
		logger.info("final Query="+vSMSMesageBackupInsertQuery);
		try {
			int insertQueryResult = jdbcTemplate.update(vSMSMesageBackupInsertQuery);
			if (insertQueryResult <= 0) {
				logger.error("Failed to insert into VSMS_MESSAGE_BACKUP table");
			} else {
				logger.info("Successfully to insert into VSMS_MESSAGE_BACKUP table|resultChangesRow" + insertQueryResult);
				/**delete from VSMS_MESSAGE_DETAILS table**/
				String deleteQuery = env.getProperty("SQL42_DELETE_VSMS_MESSAGE_DETAILS");
				deleteQuery=deleteQuery.replace("{voiceMessageId}", voiceMessageId);
				logger.info("deleteQuery="+deleteQuery);
				int deleteQueryResult = jdbcTemplate.update(deleteQuery);
				if (deleteQueryResult <= 0) {
					logger.error("Failed to delete into Inbox table");
				} else {
					logger.info("Successfully to  delete into VSMS_MESSAGE_DETAILS table|resultChangesRow" + deleteQueryResult);
				}
				
			}
			
		}catch(Exception e) {
			logger.error("Exception e="+e);
			result="N";
		}
		return result;
	}
}
	
	

