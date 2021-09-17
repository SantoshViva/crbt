package com.santosh.greenzone.wapchatezee.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
public class UserGiftInboxDetails {

	@Autowired
	Environment env;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	@RequestMapping(value = "/getUserGiftInboxDetails",method = RequestMethod.GET)
	@ResponseBody
	public String getToneIdInforXML(@RequestParam("aparty") String aparty, @RequestParam("bparty") String bparty, @RequestParam("serviceId") String serviceId,HttpServletRequest req,
			HttpServletResponse res) {
		
		System.out.println("getUserProfileInfoDetails|aparty=" + aparty+"|bparty="+bparty+"|serviceId="+serviceId);
		System.out.println("Query=" + env.getProperty("SQL26_USER_GIFT_INBOX_CHECK"));
		
				
		// Replace Table Index & aparty 
		String giftInboxQuery = ChatUtils.getQuery(env.getProperty("SQL26_USER_GIFT_INBOX_CHECK"), aparty);
		
		
		System.out.println("final profileCheck Query="+giftInboxQuery);
		
		String responseString = new String();
		String dbError ="N";
		
		try {
			List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(giftInboxQuery);
			
			if(queryForList.isEmpty())
			{
				System.out.println("No Record Found in SQL");
				
				responseString = responseString.concat("GIFT_INBOX_RES.giftCount=\'"+"0"+"\';");
				
			}else 
			{
				int giftCount=0;
				String gifterName="GIFT_INBOX_RES.gifterId";
				String gifterId="";
				String giftToneId="toneId";
				String toneId="";
				for (Map<String, Object> row : queryForList) {
					giftCount++;
					gifterId=gifterName+String.valueOf(giftCount);
					toneId=giftToneId+String.valueOf(giftCount);
					responseString=responseString.concat(gifterId+"=\'"+row.get("gifter_id")+"\';");
					responseString=responseString.concat(toneId+"=\'"+row.get("tone_id")+"\';");
										
					System.out.println("QueryResult|subscriberId="+aparty+"|gifter_id="+row.get("gifter_id").toString()+"|tone_id="+row.get("tone_id"));
					
				}
				responseString = responseString.concat("GIFT_INBOX_RES.giftCount=\'"+giftCount+"\';");
			}
		} catch (Exception e) {
			System.out.println("SQL Exception" + e +"Query="+giftInboxQuery);
			System.out.println("No Row Found");
			responseString = responseString.concat("GIFT_INBOX_RES.giftStatus=\'"+"Y"+"\';");
			dbError="Y";
			e.printStackTrace();
		}
		responseString = responseString.concat("GIFT_INBOX_RES.dbError=\'"+dbError+"\';");
			
		Date date = new Date();
		SimpleDateFormat DateFor = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
		System.out.println("CDR Report|"+DateFor.format(date)+"|aparty="+aparty+"|bparty="+bparty+"|response="+responseString+"|");
		return responseString;
		
	}
	


}
