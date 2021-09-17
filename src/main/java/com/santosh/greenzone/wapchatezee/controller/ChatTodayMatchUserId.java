package com.santosh.greenzone.wapchatezee.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.client.RestTemplate;

import com.santosh.greenzone.utils.ChatUtils;
import com.santosh.greenzone.wapchatezee.model.MyChat;
import com.santosh.greenzone.wapchatezee.model.ResponseDTO;
import com.santosh.greenzone.wapchatezee.model.UserProfileInfo;

@CrossOrigin
@RestController
public class ChatTodayMatchUserId {
	
	
	@Autowired
	Environment env;
	
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
		
	@RequestMapping(value="/chatTodayMatchUserId" , method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> chatTodayMatch(@RequestParam("msisdn") String msisdn,HttpServletRequest req, HttpServletResponse res) throws SQLException {
		System.out.println("Msisdn="+msisdn);
		System.out.println("Query="+env.getProperty("SQL4_TODAY_USER_ID"));
		System.out.println("Öperator="+env.getProperty("OPERATOR_NAME"));
		
		String lastDigit = String.valueOf(msisdn.charAt(msisdn.length()-1));
		List<MyChat> customers = new ArrayList<MyChat>();
		String query = env.getProperty("SQL4_TODAY_USER_ID").replace("{msisdn}", msisdn);
		String gender ="Male";
		query = query.replace("{table_index}", lastDigit);
		System.out.println("finalQuery="+query);
		try{
			List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(query);
			System.out.println("SQL4_TODAY_USER_ID result set value="+queryForList.size());
			if(queryForList.size() == 0)
			{
				
				String accountQuery = ChatUtils.getQuery(env.getProperty("SQL14_USER_PROFILE_INFO"), msisdn);
				List<UserProfileInfo> accountInfo = new ArrayList<UserProfileInfo>();
				
				System.out.println("final SQL Query="+accountQuery);
				// Data fetch from All Contacts
								
				queryForList = jdbcTemplate.queryForList(accountQuery);
				if(queryForList.isEmpty())
				{
						gender="Male";	
				}
				else 
				{
					for (Map<String, Object> row : queryForList) {
							System.out.println("User Status ="+row.get("status").toString() );
							gender= (row.get("gender").toString());
					}
				}
				
				System.out.println("SQL4_TODAY_USER_ID result set 0 ");
				query = env.getProperty("SQL20_DEFAULT_TODAY_USERID").replace("{msisdn}", msisdn);
				query = query.replace("{table_index}", lastDigit);
				query = query.replace("{gender}", gender);
				System.out.println("finalQuery="+query);
				queryForList = jdbcTemplate.queryForList(query);
				System.out.println("SQL20_DEFAULT_TODAY_USERID result set value="+queryForList.size());
			}
			
			for (Map row : queryForList) {
				MyChat customer = new MyChat();

				customer.setChatMsisdn(row.get("todayMsisdn").toString());
				customer.setChatUserId(row.get("todayUserId").toString());
				String genderQuery = env.getProperty("SQL6_SELECT_GENDER").replace("{msisdn}",customer.getChatMsisdn());
				genderQuery = genderQuery.replace("{table_index}", String.valueOf(customer.getChatMsisdn().charAt(customer.getChatMsisdn().length()-1)));
				try {
					customer.setGender(jdbcTemplate.queryForObject(genderQuery, String.class));
				}catch(Exception e) {
					System.out.println("SQL Exception"+e);
				}
				customers.add(customer);
			}	
		} catch(Exception e) {
			System.out.println("SQL Exception"+e);
		}
		ResponseDTO<List<MyChat>> response = new ResponseDTO<>();
		response.setBody(customers);
		
		res.addHeader("Access-Control-Allow-Methods", "PUT, GET, POST, DELETE, OPTIONS");
		res.addHeader("Access-Control-Allow-Origin", "http://localhost");
		return new ResponseEntity<>(response , HttpStatus.OK);
	}

	
}
