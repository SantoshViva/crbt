package com.santosh.greenzone.wapchatezee.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.santosh.greenzone.wapchatezee.model.MyChat;
import com.santosh.greenzone.wapchatezee.model.ResponseDTO;

@RestController
public class TestController {
	
	
	@Autowired
	Environment env;
	
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@RequestMapping(value="/myChat")
	@ResponseBody
	public ResponseEntity<?> test(@RequestParam("msisdn") String msisdn) throws SQLException {
		System.out.println("Msisdn="+msisdn);
		System.out.println("Query="+env.getProperty("QUERY_INBOX_USER_ID"));
		System.out.println("Öperator="+env.getProperty("OPERATOR_NAME"));
		MyChat responseObject = new MyChat();
		
		String chatMsisdn="9810594996";
		String chatUserId="test";
		responseObject.setChatMsisdn(chatMsisdn);
		responseObject.setChatUserId(chatUserId);
		
		
		
		
		List<MyChat> customers = new ArrayList<MyChat>();
		String query = env.getProperty("QUERY_INBOX_USER_ID").replace("{msisdn}", msisdn);
	 List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(query);
		for (Map row : queryForList) {
			MyChat customer = new MyChat();

			customer.setChatMsisdn(row.get("msisdn").toString());
			customer.setChatUserId(row.get("UserId").toString());
			customer.setChatDate(row.get("chat_date").toString());
			
			customers.add(customer);
		}	
		
		
		ResponseDTO<List<MyChat>> response = new ResponseDTO<>();
		response.setBody(customers);
		
		return new ResponseEntity<>(response , HttpStatus.OK);
	}
	
	

}
