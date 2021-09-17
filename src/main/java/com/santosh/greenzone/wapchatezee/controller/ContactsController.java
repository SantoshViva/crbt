package com.santosh.greenzone.wapchatezee.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.santosh.greenzone.wapchatezee.model.MyContacts;
import com.santosh.greenzone.wapchatezee.model.ResponseDTO;


@CrossOrigin
@RestController
public class ContactsController {

	@Autowired
	Environment env;

	@Autowired
	JdbcTemplate jdbcTemplate;

	@RequestMapping(value = "/getAllMyContacts", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> chatTodayMatch(@RequestParam("msisdn") String msisdn, HttpServletRequest req,
			HttpServletResponse res) throws SQLException {
		System.out.println("Msisdn=" + msisdn);
		System.out.println("Query=" + env.getProperty("SQL10_CONTACTS_DETAILS"));
		System.out.println("Öperator=" + env.getProperty("OPERATOR_NAME"));

		String query = ChatUtils.getQuery(env.getProperty("SQL10_CONTACTS_DETAILS"), msisdn);
		List<MyContacts> customers = new ArrayList<MyContacts>();
		Set<String> temMsidnSet = new HashSet<String>();
		// Data fetch from All Contacts
		
		try {
			List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(query);
						
			for (Map<String, Object> row : queryForList) {
				MyContacts customer = new MyContacts();

				if (!temMsidnSet.add(row.get("friendMsisdn").toString())) {
					continue;
				}

				customer.setFriendMsisdn(row.get("friendMsisdn").toString());
				customer.setFriendUserId(row.get("friendUserId").toString());
				customer.setAddFriendDate(row.get("AddFriendDate").toString());
				customer.setAddFriendTime(row.get("AddFriendTime").toString());
	
				
				customer.setGender(ChatUtils.getGender(env.getProperty("SQL6_SELECT_GENDER"), msisdn, jdbcTemplate));
				
				customers.add(customer);
			}
		} catch (Exception e) {
			System.out.println("SQL Exception" + e +"Query="+query);
			e.printStackTrace();
		}

		Collections.sort(customers, new Comparator<MyContacts>() {

			@Override
			public int compare(MyContacts msg1, MyContacts msg2) {
				return msg2.getAddFriendDate().compareTo(msg1.getAddFriendDate());
			}
		});
		
		ResponseDTO<List<MyContacts>> response = new ResponseDTO<>();
		response.setBody(customers);

		res.addHeader("Access-Control-Allow-Methods", "PUT, GET, POST, DELETE, OPTIONS");
		res.addHeader("Access-Control-Allow-Origin", "http://localhost");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}



}
