package com.santosh.greenzone.wapchatezee.controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.santosh.greenzone.utils.ChatUtils;
import com.santosh.greenzone.wapchatezee.model.AccountInfo;
import com.santosh.greenzone.wapchatezee.model.ResponseDTO;

@CrossOrigin
@RestController
public class ChatAccountCreateController {

	@Autowired
	Environment env;

	@Autowired
	JdbcTemplate jdbcTemplate;

	@RequestMapping(value = { "/CreateUserAccount","/UpdateUserAccount" }, method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> chatTodayMatch(@RequestParam("msisdn") String msisdn, @RequestParam("gender") String gender,@RequestParam("ageGroup") String ageGroup,HttpServletRequest req,
			HttpServletResponse res) throws SQLException {
//		System.out.println("Msisdn=" + msisdn);
//		System.out.println("Query=" + env.getProperty("SQL14_USER_PROFILE_INFO"));
//		System.out.println("Öperator=" + env.getProperty("OPERATOR_NAME"));
		String operator = env.getProperty("OPERATOR_NAME");
		String defaultNotificationOpt = env.getProperty("DEFAULT_NOTIFICATION_OPT");
		
		// Replace Table Index & Msisdn
		ResponseDTO<AccountInfo> response = new ResponseDTO<>();
		String query = ChatUtils.getQuery(env.getProperty("SQL14_USER_PROFILE_INFO"), msisdn);
		String userId ="", prefixName="";
		AccountInfo customers = new AccountInfo();

		System.out.println("Request URI=" + req.getRequestURI());
		// Data fetch from All Contacts

		if(ChatUtils.isNumber(msisdn))
		{	
		try {
		
			AccountInfo userAccountInfo = jdbcTemplate.query(query, new ResultSetExtractor<AccountInfo>() {

				@Override
				public AccountInfo extractData(ResultSet rs) throws SQLException, DataAccessException {

					AccountInfo userAccountInfo = null;
					while(rs.next()) {
						userAccountInfo = new AccountInfo();
						userAccountInfo.setStatus(rs.getString("status"));
						userAccountInfo.setUserId(rs.getString("userId"));
						userAccountInfo.setGender(rs.getString("gender"));
					}
					return userAccountInfo;
				}

			});

			if (userAccountInfo == null && req.getRequestURI().equals("/CreateUserAccount") ) {
				//find userId for new user
				
				String selectQuery = ChatUtils.getAccountInsertQuery(env.getProperty("SQL17_CHECK_MAX_USER_ID"),msisdn,"",gender,"",operator,"");
				
				List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(selectQuery);
					
				for (Map<String,Object> row : queryForList) {
					prefixName = row.get("prefix_name").toString();
					String maxId = row.get("maxId").toString();
					userId = prefixName+maxId;
					break;
				}
				
				System.out.println("New User Id="+userId+"prefixName="+prefixName);
				// First Time User , Profile will create
				
				String insertQuery = ChatUtils.getAccountInsertQuery(env.getProperty("SQL16_USER_ACCOUNT_CREATE"), msisdn,userId,gender,ageGroup,operator,defaultNotificationOpt);
				try {
					int insertQueryResult= jdbcTemplate.update(insertQuery);
					if(insertQueryResult <= 0)
					{
						System.out.println("Failed to insert into accout table");
					}else {
						System.out.println("Successfully to insert into account table|resultChangesRow"+insertQueryResult);
						customers.setStatus("Y");
						customers.setGender(gender);
						customers.setUserId(userId);
						customers.setAgeGroup(ageGroup);
						customers.setMsisdn(msisdn);
						String insertMappingQuery = ChatUtils.getAccountInsertQuery(env.getProperty("SQL18_USER_ID_MAPPING"), msisdn, userId, gender, ageGroup, operator, defaultNotificationOpt);
						insertQueryResult= jdbcTemplate.update(insertMappingQuery);
						if(insertQueryResult <= 0) {
							System.out.println("Failed to insert into tb_anonymous_chat_username_table_index_mapping table");
						}else {
							System.out.println("Successfully to insert into tb_anonymous_chat_username_table_index_mapping table|resultChangesRow"+insertQueryResult);
							String updateUserMaxQuery = env.getProperty("SQL19_UPDATE_MAX_USER_ID");
							updateUserMaxQuery = updateUserMaxQuery.replace("{prefixName}", prefixName);
							updateUserMaxQuery = updateUserMaxQuery.replace("{operator}", operator);
							
							int updateUserMaxQueryResult= jdbcTemplate.update(updateUserMaxQuery);
							if(updateUserMaxQueryResult <=0) {
								System.out.println("Failed to update into tb_anonymous_chat_username_table_index_mapping table");
							}else {
								System.out.println("Successful to update into tb_anonymous_chat_username_table_index_mapping table");
								customers = userAccountInfo;
							}
						}
					}
					
				}catch(Exception e) {
					System.out.println("SQL Exception" + e + "Query=" + insertQuery);
					e.printStackTrace();
					
				}
				customers = userAccountInfo;

			} else if(userAccountInfo == null && req.getRequestURI() =="/UpdateUserAccount"){
				//Error Send Request for update Case
			}
			else {
				//Account is already Created
				if(customers.getStatus() == "N" && req.getRequestURI() =="/CreateUserAccount"){
					//Account will be updated 
				}
				else {
					
				}
				customers = userAccountInfo;
				System.out.println("selection tb_anonymous_chat_acc_details table|msisdn="+userAccountInfo.getMsisdn()+"status="+userAccountInfo.getStatus());
			}
		} catch (Exception e) {
			System.out.println("SQL Exception" + e + "Query=" + query);
			System.out.println("No Row Found");
			e.printStackTrace();
		}
		}
		else {
			System.out.println("MSISDN is  not digit");
			response.getHeader().setCode(500);
			response.getHeader().setMessage("MSISDN is not a digit");
		}
		
		response.setBody(customers);

		res.addHeader("Access-Control-Allow-Methods", "PUT, GET, POST, DELETE, OPTIONS");
		res.addHeader("Access-Control-Allow-Origin", "http://localhost");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
