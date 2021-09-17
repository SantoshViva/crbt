package com.santosh.greenzone.wapchatezee.controller;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import com.santosh.greenzone.wapchatezee.model.ResponseDTO;
import com.santosh.greenzone.wapchatezee.model.UserProfileInfo;




@CrossOrigin
@RestController
public class ChatMessageSave {
	

	@Autowired
	Environment env;

	@Autowired
	JdbcTemplate jdbcTemplate;

	@RequestMapping(value = "/saveChatMessage", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> chatTodayMatch(@RequestParam("msisdn") String msisdn, @RequestParam("userId") String userId, @RequestParam("friendMsisdn") String friendMsisdn,@RequestParam("friendUserId") String friendUserId,@RequestParam("chatMessage") String chatMessage,@RequestParam("readMessageStatus") String readMessageStatus,HttpServletRequest req,
			HttpServletResponse res) throws SQLException {
		System.out.println("Msisdn=" + msisdn+"|userId="+userId+"|friendMsisdn="+friendMsisdn+"|friendUserId="+friendUserId+"|chatMessage="+chatMessage+"|readMessageStatus="+readMessageStatus);
		System.out.println("Öperator=" + env.getProperty("OPERATOR_NAME"));
		String insertQuery ="";
		if(readMessageStatus.equalsIgnoreCase("false"))
		{
			insertQuery = env.getProperty("SQL21_INSERT_CHAT_MESSAGE_INBOX");
			insertQuery = insertQuery.replace("{msisdn}", msisdn);
			insertQuery = insertQuery.replace("{userId}", userId);
			insertQuery = insertQuery.replace("{friendMsisdn}", friendMsisdn);
			insertQuery = insertQuery.replace("{friendUserId}", friendUserId);
			insertQuery = insertQuery.replace("{chatMessage}", chatMessage);
			insertQuery = insertQuery.replace("{operator}", env.getProperty("OPERATOR_NAME").toString());
		}
		else
		{
			insertQuery= env.getProperty("SQL22_INSERT_CHAT_MESSAGE_HISTORY");
			insertQuery = insertQuery.replace("{table_index}", String.valueOf(friendMsisdn.charAt(friendMsisdn.length() - 1)));
			insertQuery = insertQuery.replace("{msisdn}", friendMsisdn);
			insertQuery = insertQuery.replace("{userId}", friendUserId);
			insertQuery = insertQuery.replace("{friendMsisdn}", msisdn);
			insertQuery = insertQuery.replace("{friendUserId}", userId);
			insertQuery = insertQuery.replace("{chatMessage}", chatMessage);
			
			
			SimpleDateFormat format = new SimpleDateFormat("yyyy-dd-mm hh:mm:ss");
			String currentDate = format.format(new Date());
			
			insertQuery = insertQuery.replace("{chatDate}", currentDate );
			insertQuery = insertQuery.replace("{operator}", env.getProperty("OPERATOR_NAME").toString());
		}	
		System.out.println("finalQuery=" + insertQuery);
		
		try {
			int insertQueryResult= jdbcTemplate.update(insertQuery);
			if(insertQueryResult <= 0)
			{
				System.out.println("Failed to insert into accout table");	
			}
			else 
			{
				System.out.println("Successfully to insert into account table|resultChangesRow"+insertQueryResult);
			}
		} catch (Exception e) {
			System.out.println("SQL Exception" + e +"Query="+insertQuery);
			System.out.println("No Row Found");
			e.printStackTrace();
			}
		
		
		ResponseDTO<List<UserProfileInfo>> response = new ResponseDTO<>();
		response.setBody(null);

		res.addHeader("Access-Control-Allow-Methods", "PUT, GET, POST, DELETE, OPTIONS");
		res.addHeader("Access-Control-Allow-Origin", "http://localhost");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
