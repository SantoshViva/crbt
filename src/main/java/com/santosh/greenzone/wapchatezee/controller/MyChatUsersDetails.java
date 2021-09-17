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

import com.santosh.greenzone.wapchatezee.model.ChatMessageDetailModel;
import com.santosh.greenzone.wapchatezee.model.MyChatUsers;
import com.santosh.greenzone.wapchatezee.model.ResponseDTO;

@CrossOrigin
@RestController
public class MyChatUsersDetails {

	@Autowired
	Environment env;

	@Autowired
	JdbcTemplate jdbcTemplate;

	@RequestMapping(value = "/getAllMyChatUserId", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> chatTodayMatch(@RequestParam("msisdn") String msisdn, HttpServletRequest req,
			HttpServletResponse res) throws SQLException {
		System.out.println("Msisdn=" + msisdn);
		System.out.println("Query=" + env.getProperty("SQL7_MYCHAT_USER_ID_INBOX"));
		System.out.println("Öperator=" + env.getProperty("OPERATOR_NAME"));

		String lastDigit = String.valueOf(msisdn.charAt(msisdn.length() - 1));
		List<MyChatUsers> customers = new ArrayList<MyChatUsers>();
		String query = env.getProperty("SQL7_MYCHAT_USER_ID_INBOX").replace("{msisdn}", msisdn);
		query = query.replace("{table_index}", lastDigit);
		System.out.println("finalQuery=" + query);
		Set<String> temMsidnSet = new HashSet<String>();
		// Data fetch from inbox
		try {
			List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(query);
			String tempMsisdn = "";
			
			for (Map<String, Object> row : queryForList) {
				MyChatUsers customer = new MyChatUsers();

				if (tempMsisdn == (row.get("fMsisdn").toString())) {
					continue;
				}
				tempMsisdn = (row.get("fMsisdn").toString());
				if (!temMsidnSet.add(row.get("fMsisdn").toString())) {
					continue;
				}

				customer.setChatFriendMsisdn(row.get("fMsisdn").toString());
				customer.setChatFriendUserId(row.get("fUserId").toString());
				customer.setChatDate(row.get("chat_date").toString());
				customer.setChatMessage(row.get("message").toString());
				customer.setChatTime(row.get("chatTime").toString());
				
				String genderQuery = env.getProperty("SQL6_SELECT_GENDER").replace("{msisdn}",
						customer.getChatFriendMsisdn());
				genderQuery = genderQuery.replace("{table_index}", String
						.valueOf(customer.getChatFriendMsisdn().charAt(customer.getChatFriendMsisdn().length() - 1)));
				try {
					customer.setGender(jdbcTemplate.queryForObject(genderQuery, String.class));
				} catch (Exception e) {
					System.out.println("SQL Exception" + e +"Query="+query);
					e.printStackTrace();
				}
				customers.add(customer);
			}
		} catch (Exception e) {
			System.out.println("SQL Exception" + e +"Query="+query);
			e.printStackTrace();
		}

		// Data Fetch for other User Chat Messages from History table
		query = env.getProperty("SQL8_MYCHAT_USER_ID_FROM_HISTORY_TO_OTHERS").replace("{msisdn}", msisdn);
		query = query.replace("{table_index}", lastDigit);
		System.out.println("finalQuery=" + query);
		try {

			List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(query);
			String tempMsisdn = "";
			//Set<String> temMsidnSet = new HashSet<String>();
			for (Map<String, Object> row : queryForList) {
				MyChatUsers customer = new MyChatUsers();

				if (tempMsisdn == (row.get("friendMsisdn").toString())) {
					continue;
				}
				tempMsisdn = (row.get("friendMsisdn").toString());
				if (!temMsidnSet.add(row.get("friendMsisdn").toString())) {
					continue;
				}

				customer.setChatFriendMsisdn(row.get("friendMsisdn").toString());
				customer.setChatFriendUserId(row.get("friendUserId").toString());
				customer.setChatDate(row.get("chat_read_date").toString());
				customer.setChatMessage(row.get("message").toString());
				customer.setChatTime(row.get("chatTime").toString());
				String genderQuery = env.getProperty("SQL6_SELECT_GENDER").replace("{msisdn}",
						customer.getChatFriendMsisdn());
				genderQuery = genderQuery.replace("{table_index}", String
						.valueOf(customer.getChatFriendMsisdn().charAt(customer.getChatFriendMsisdn().length() - 1)));
				try {
					customer.setGender(jdbcTemplate.queryForObject(genderQuery, String.class));
				} catch (Exception e) {
					System.out.println("SQL Exception" + e +"Query="+query);
					e.printStackTrace();
				}
				customers.add(customer);
			}
		} catch (Exception e) {
			System.out.println("SQL Exception" + e);
		}

		// Data Fetch for User Chat Messages from All History table's
		query = env.getProperty("SQL9_MYCHAT_USER_ID_FROM_HISTORY_TO_SELF").replace("{msisdn}", msisdn);

		System.out.println("finalQuery=" + query);
		String originalQuery = query;
		for (int tableLastDigit = 0; tableLastDigit < 10; tableLastDigit++) {
			
			query = originalQuery.replace("{all_table_index}", String.valueOf(tableLastDigit));
			System.out.println("SQL9_MYCHAT_USER_ID_FROM_HISTORY_TO_SELF|finalQuery=" + query+"|lastDigit="+tableLastDigit);

			try {
				List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(query);
				String tempMsisdn = "";
				//Set<String> temMsidnSet = new HashSet<String>();
				for (Map<String, Object> row : queryForList) {
					MyChatUsers customer = new MyChatUsers();

					if (tempMsisdn == (row.get("fMsisdn").toString())) {
						continue;
					}
					tempMsisdn = (row.get("fMsisdn").toString());
					if (!temMsidnSet.add(row.get("fMsisdn").toString())) {
						continue;
					}

					customer.setChatFriendMsisdn(row.get("fMsisdn").toString());
					customer.setChatFriendUserId(row.get("fUserId").toString());
					customer.setChatDate(row.get("chat_read_date").toString());
					customer.setChatMessage(row.get("message").toString());
					customer.setChatTime(row.get("chatTime").toString());
					
					String genderQuery = env.getProperty("SQL6_SELECT_GENDER").replace("{msisdn}",
							customer.getChatFriendMsisdn());
					genderQuery = genderQuery.replace("{table_index}", String.valueOf(
							customer.getChatFriendMsisdn().charAt(customer.getChatFriendMsisdn().length() - 1)));
					try {
						customer.setGender(jdbcTemplate.queryForObject(genderQuery, String.class));
					} catch (Exception e) {
						System.out.println("SQL Exception" + e +"Query="+query);
						e.printStackTrace();
					}
					customers.add(customer);
				}
			} catch (Exception e) {
				System.out.println("SQL Exception" + e +"Query="+query);
				e.printStackTrace();
			}

		}

		Collections.sort(customers, new Comparator<MyChatUsers>() {

			@Override
			public int compare(MyChatUsers msg1, MyChatUsers msg2) {
				return msg2.getChatDate().compareTo(msg1.getChatDate());
			}
		});
		
		ResponseDTO<List<MyChatUsers>> response = new ResponseDTO<>();
		response.setBody(customers);

		res.addHeader("Access-Control-Allow-Methods", "PUT, GET, POST, DELETE, OPTIONS");
		res.addHeader("Access-Control-Allow-Origin", "http://localhost");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
