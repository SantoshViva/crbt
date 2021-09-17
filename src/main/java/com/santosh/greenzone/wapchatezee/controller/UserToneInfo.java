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

import com.santosh.greenzone.utils.ChatUtils;
import com.santosh.greenzone.wapchatezee.model.ResponseDTO;
import com.santosh.greenzone.wapchatezee.model.ToneInfo;
import com.santosh.greenzone.wapchatezee.model.UserProfileInfo;

@CrossOrigin
@RestController
public class UserToneInfo {
	@Autowired
	Environment env;

	@Autowired
	JdbcTemplate jdbcTemplate;
	@RequestMapping(value = "/getUserToneInfo", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> chatTodayMatch(@RequestParam("msisdn") String msisdn, HttpServletRequest req,
			HttpServletResponse res) throws SQLException {
		System.out.println("UserToneInfo|Msisdn=" + msisdn);
		System.out.println("Query=" + env.getProperty("SQL23_USER_TONE_INFO"));
		System.out.println("Öperator=" + env.getProperty("OPERATOR_NAME"));
		
		// Replace Table Index & Msisdn 
		String query = ChatUtils.getQuery(env.getProperty("SQL23_USER_TONE_INFO"), msisdn);
		List<ToneInfo> customers = new ArrayList<ToneInfo>();
		
		
		System.out.println("final SQL Query="+query);
		// Data fetch from All Contacts
		
		try {
			List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(query);
			if(queryForList.isEmpty())
			{
				ToneInfo customer = new ToneInfo();
				customer.setStatus("N");
				customers.add(customer);	
			}
			else 
			{
				for (Map<String, Object> row : queryForList) {
					ToneInfo customer = new ToneInfo();
				
					System.out.println("User Status ="+row.get("tone_id").toString() );
					customer.setStatus("Y");
					customer.setToneId(row.get("tone_id").toString());
					customer.setSubscriberId(msisdn);
					customers.add(customer);
				}
			}
		} catch (Exception e) {
			System.out.println("SQL Exception" + e +"Query="+query);
			System.out.println("No Row Found");
			e.printStackTrace();
		}
		
		
		ResponseDTO<List<ToneInfo>> response = new ResponseDTO<>();
		response.setBody(customers);

		res.addHeader("Access-Control-Allow-Methods", "PUT, GET, POST, DELETE, OPTIONS");
		res.addHeader("Access-Control-Allow-Origin", "http://localhost");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
