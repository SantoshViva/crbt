package com.santosh.greenzone.wapchatezee.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.santosh.greenzone.wapchatezee.model.ChatMessageDetailModel;
import com.santosh.greenzone.wapchatezee.model.ResponseDTO;

@RestController
public class ChatMessageDetails {

	@Autowired
	Environment env;

	@Autowired
	JdbcTemplate jdbcTemplate;

	@RequestMapping(value = "/myChatMessageDetails")
	@ResponseBody
	public ResponseEntity<?> chatMessageDetails(@RequestParam("msisdn") String msisdn,
			@RequestParam("friendMsisdn") String friendMsisdn, HttpServletRequest req, HttpServletResponse res)
			throws SQLException {
		System.out.println("Msisdn=" + msisdn + "|URL=" + req.getRequestURI());
//		System.out.println("Query=" + env.getProperty("SQL2_MYCHAT_MESSAGE_DETAILS"));
		System.out.println("Öperator=" + env.getProperty("OPERATOR_NAME"));

		String lastDigit = "";

		// String friendMsisdn= "912222222220";
		lastDigit = String.valueOf(msisdn.charAt(msisdn.length() - 1));

		List<ChatMessageDetailModel> customers = new ArrayList<ChatMessageDetailModel>();

		//Data fetch for Msisdn 
		String query = env.getProperty("SQL2_MYCHAT_MESSAGE_DETAILS");

		query = query.replace("{msisdn}", msisdn);
		System.out.println("lastDigit=" + lastDigit);
		query = query.replace("{table_index}", lastDigit);
		query = query.replace("{friendMsisdn}", friendMsisdn);
		System.out.println("SQL2_MYCHAT_MESSAGE_DETAILS finalQuery=" + query);
		List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(query);
		for (Map<String, Object> row : queryForList) {
			ChatMessageDetailModel customer = new ChatMessageDetailModel();

			customer.setChatMsisdn(row.get("msisdn").toString());
			customer.setChatUserId(row.get("UserId").toString());
			customer.setFriendMsisdn(row.get("friendMsisdn").toString());
			customer.setFriendUserId(row.get("friendUserId").toString());
			customer.setChatMessage(row.get("message").toString());
			if (row.get("chat_date") != null)
				customer.setChatDate(row.get("chat_date").toString());
			else
				customer.setChatDate(row.get("chat_read_date").toString());
			customer.setChat_read_date(row.get("chat_read_date").toString());
			customer.setChatAlignment("R");
			customers.add(customer);
		}
		// data fetch for friend msisdn
		String friendLastDigit = String.valueOf(friendMsisdn.charAt(friendMsisdn.length() - 1));
		String friendQuery = env.getProperty("SQL3_MYFRIENDCHAT_MESSAGE_DETAILS");
		friendQuery = friendQuery.replace("{msisdn}", msisdn);
		System.out.println("lastDigit=" + friendLastDigit);
		friendQuery = friendQuery.replace("{table_index}", friendLastDigit);
		friendQuery = friendQuery.replace("{friendMsisdn}", friendMsisdn);
		System.out.println("SQL3_MYFRIENDCHAT_MESSAGE_DETAILS finalFriendQuery=" + friendQuery);

		List<Map<String, Object>> friendQueryForList = jdbcTemplate.queryForList(friendQuery);
		for (Map<String, Object> row : friendQueryForList) {
			ChatMessageDetailModel customer = new ChatMessageDetailModel();

			customer.setChatMsisdn(row.get("msisdn").toString());
			customer.setChatUserId(row.get("UserId").toString());
			customer.setFriendMsisdn(row.get("friendMsisdn").toString());
			customer.setFriendUserId(row.get("friendUserId").toString());
			customer.setChatMessage(row.get("message").toString());
			if (row.get("chat_date") != null)
				customer.setChatDate(row.get("chat_date").toString());
			else
				customer.setChatDate(row.get("chat_read_date").toString());
			customer.setChat_read_date(row.get("chat_read_date").toString());
			customer.setChatAlignment("L");
			customers.add(customer);
		}
		
		// Data fetch from Message Inbox for msisdn
		String inboxQuery = env.getProperty("SQL5_INBOX_MESSAGE_DETAILS").replace("{msisdn}", msisdn);
		inboxQuery = inboxQuery.replace("{friendMsisdn}", friendMsisdn);
		System.out.println("SQL5_INBOX_MESSAGE_DETAILS msisdn finalFriendQuery=" + inboxQuery);
		List<Map<String, Object>> inboxQueryList = jdbcTemplate.queryForList(inboxQuery);
		for (Map<String, Object> row : inboxQueryList) {
			ChatMessageDetailModel customer = new ChatMessageDetailModel();
//			String id = row.get("id").toString();
			customer.setChatMsisdn(row.get("msisdn").toString());
			customer.setChatUserId(row.get("UserId").toString());
			customer.setFriendUserId(row.get("friendUserId").toString());
			customer.setFriendMsisdn(row.get("friendMsisdn").toString());
			customer.setChatMessage(row.get("message").toString());
			if (row.get("chat_date") != null)
				customer.setChatDate(row.get("chat_date").toString());

			customer.setChatAlignment("L");
			customers.add(customer);
			// Insert Data in History Table for Friend Msisdn
			{
//				System.out.println("Data save from History Table");
//				saveChatHistoryFriendMsisdn(customer, id);
			}
		}

		// Data fetch from Message Inbox for friendMsisdn
		String inboxFriendQuery = env.getProperty("SQL5_INBOX_MESSAGE_DETAILS").replace("{msisdn}", friendMsisdn);
		inboxFriendQuery = inboxFriendQuery.replace("{friendMsisdn}", msisdn);

		System.out.println("SQL5_INBOX_MESSAGE_DETAILS friendMsisdn finalFriendQuery=" + inboxFriendQuery);
		List<Map<String, Object>> inboxFriendQueryList = jdbcTemplate.queryForList(inboxFriendQuery);
		for (Map<String, Object> row : inboxFriendQueryList) {
			ChatMessageDetailModel customer = new ChatMessageDetailModel();
			String id = row.get("id").toString();
			customer.setChatMsisdn(row.get("msisdn").toString());
			customer.setChatUserId(row.get("UserId").toString());
			customer.setFriendUserId(row.get("friendUserId").toString());
			customer.setFriendMsisdn(row.get("friendMsisdn").toString());
			customer.setChatMessage(row.get("message").toString());
			if (row.get("chat_date") != null)
				customer.setChatDate(row.get("chat_date").toString());
			customer.setChatAlignment("R");
			customers.add(customer);
			// Insert Data in History Table for Msisdn
			{
				System.out.println("Data save from History Table");
				saveChatHistoryMsisdn(customer, id);

			}
		}

		Collections.sort(customers, new Comparator<ChatMessageDetailModel>() {

			@Override
			public int compare(ChatMessageDetailModel msg1, ChatMessageDetailModel msg2) {
				return msg1.getChatDate().compareTo(msg2.getChatDate());
			}
		});

		ResponseDTO<List<ChatMessageDetailModel>> response = new ResponseDTO<>();
		response.setBody(customers);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	private void saveChatHistoryMsisdn(ChatMessageDetailModel customer, String id) {
		String insertQuery = env.getProperty("SQL22_INSERT_CHAT_MESSAGE_HISTORY");
		insertQuery = insertQuery.replace("{table_index}",
				String.valueOf(customer.getFriendMsisdn().charAt(customer.getFriendMsisdn().length() - 1)));
		insertQuery = insertQuery.replace("{msisdn}", customer.getFriendMsisdn());
		insertQuery = insertQuery.replace("{userId}", customer.getFriendUserId());
		insertQuery = insertQuery.replace("{friendMsisdn}", customer.getChatMsisdn());
		insertQuery = insertQuery.replace("{friendUserId}", customer.getChatUserId());
		insertQuery = insertQuery.replace("{chatMessage}", customer.getChatMessage());
		insertQuery = insertQuery.replace("{chatDate}", customer.getChatDate());
		insertQuery = insertQuery.replace("{operator}", env.getProperty("OPERATOR_NAME").toString());
		System.out.println("Msisdn finalQuery=" + insertQuery);
		try {
			int insertQueryResult = jdbcTemplate.update(insertQuery);
			if (insertQueryResult <= 0) {
				System.out.println("Failed to insert into History table");
			} else {
				System.out.println("Successfully to insert into History table|resultChangesRow" + insertQueryResult);
				String deleteQuery = env.getProperty("SQL23_DELETE_FROM_INBOX");
				deleteQuery = deleteQuery.replace("{id}", id);
				System.out.println("final Delete Query" + deleteQuery);
				int deleteQueryResult = jdbcTemplate.update(deleteQuery);
				if (deleteQueryResult <= 0) {
					System.out.println("Failed to delete into Inbox table");
				} else {
					System.out.println("Successfully to  delete into inbox table|resultChangesRow" + deleteQueryResult);
				}
			}
		} catch (Exception e) {
			System.out.println("SQL Exception" + e + "Query=" + insertQuery);
			System.out.println("No Row Found");
			e.printStackTrace();
		}
	}

	private void saveChatHistoryFriendMsisdn(ChatMessageDetailModel customer, String id) {
		String insertQuery = env.getProperty("SQL22_INSERT_CHAT_MESSAGE_HISTORY");
		insertQuery = insertQuery.replace("{table_index}",
				String.valueOf(customer.getFriendMsisdn().charAt(customer.getFriendMsisdn().length() - 1)));
		insertQuery = insertQuery.replace("{msisdn}", customer.getFriendMsisdn());
		insertQuery = insertQuery.replace("{userId}", customer.getFriendUserId());
		insertQuery = insertQuery.replace("{friendMsisdn}", customer.getChatMsisdn());
		insertQuery = insertQuery.replace("{friendUserId}", customer.getChatUserId());
		insertQuery = insertQuery.replace("{chatMessage}", customer.getChatMessage());
		insertQuery = insertQuery.replace("{chatDate}", customer.getChatDate());
		insertQuery = insertQuery.replace("{operator}", env.getProperty("OPERATOR_NAME").toString());
		System.out.println("FriendMsisdnfinalQuery=" + insertQuery);
		try {
			int insertQueryResult = jdbcTemplate.update(insertQuery);
			if (insertQueryResult <= 0) {
				System.out.println("Failed to insert into History table");
			} else {
				System.out.println("Successfully to insert into History table|resultChangesRow" + insertQueryResult);
				String deleteQuery = env.getProperty("SQL23_DELETE_FROM_INBOX");
				deleteQuery = deleteQuery.replace("{id}", id);
				System.out.println("final Delete Query" + deleteQuery);
				int deleteQueryResult = jdbcTemplate.update(deleteQuery);
				if (deleteQueryResult <= 0) {
					System.out.println("Failed to delete into Inbox table");
				} else {
					System.out.println("Successfully to  delete into inbox table|resultChangesRow" + deleteQueryResult);
				}
			}
		} catch (Exception e) {
			System.out.println("SQL Exception" + e + "Query=" + insertQuery);
			System.out.println("No Row Found");
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
}
