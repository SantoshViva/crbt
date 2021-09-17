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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.santosh.greenzone.utils.ChatUtils;
import com.santosh.greenzone.wapchatezee.model.ResponseDTO;
import com.santosh.greenzone.wapchatezee.model.SearchList;

@CrossOrigin
//@RestController
@Controller
public class SearchController {

	@Autowired
	Environment env;

	@Autowired
	JdbcTemplate jdbcTemplate;

	@RequestMapping(value = "/getSearchResults", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> chatTodayMatch(@RequestParam("msisdn") String msisdn,
			@RequestParam("searchText") String searchText, HttpServletRequest req, HttpServletResponse res)
			throws SQLException {
		System.out.println("Search Text=" + searchText);
		
		System.out.println("Öperator=" + env.getProperty("OPERATOR_NAME"));

		List<SearchList> customers = new ArrayList<SearchList>();
		String finalSearchResultQuery = "";
		// Check searchText is Number or String
		if (ChatUtils.isNumber(searchText)) {
			// If searchText is Number Then
			finalSearchResultQuery = ChatUtils.getQuery(env.getProperty("SQL11_GET_USER_ID"), searchText);

		} else {
			// If searchText is String
			// Find table index on the basis of User Id
			String queryIndex = ChatUtils.getQuery(env.getProperty("SQL12_GET_TABLE_INDEX"), searchText);
			String tableIndex = "";
			try {
				List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(queryIndex);
				for (Map<String, Object> row : queryForList) {
					tableIndex = row.get("table_index").toString();
				}

			} catch (Exception e) {
				System.out.println("SQL Exception" + e + "Query=" + queryIndex);
				e.printStackTrace();
			}
			if (tableIndex.isEmpty()) {
				// No Record Found in the Index Table
				tableIndex = String.valueOf(msisdn.charAt(msisdn.length() - 1));
				finalSearchResultQuery = ChatUtils.getQuery(env.getProperty("SQL13_SEARCH_USER_ID"), msisdn, tableIndex, searchText);

			} else {
				// Record Found in the Index Table
				finalSearchResultQuery = ChatUtils.getQuery(env.getProperty("SQL13_SEARCH_USER_ID"), msisdn, tableIndex, searchText);
			}
		}

		try {
			List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(finalSearchResultQuery);
			for (Map<String, Object> row : queryForList) {
				SearchList customer = new SearchList();
				customer.setFriendMsisdn(row.get("friendMsisdn").toString());
				customer.setFriendUserId(row.get("friendUserId").toString());
				customer.setGender(row.get("gender").toString());

				customers.add(customer);
			}

		} catch (Exception e) {
			System.out.println("SQL Exception" + e + "Query=" + finalSearchResultQuery);
			e.printStackTrace();
		}

		ResponseDTO<List<SearchList>> response = new ResponseDTO<>();
		response.setBody(customers);

		res.addHeader("Access-Control-Allow-Methods", "PUT, GET, POST, DELETE, OPTIONS");
		res.addHeader("Access-Control-Allow-Origin", "http://localhost");
		res.setHeader("Access-Control-Allow-Origin", "*");
		System.out.println("Response Header="+res.getHeaderNames());
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
