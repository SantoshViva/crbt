package com.santosh.greenzone.utils;



import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.santosh.greenzone.wapchatezee.controller.IvrLanguageSet;
import java.io.InputStream;

public class ChatUtils {

	private static final Logger logger = LogManager.getLogger(ChatUtils.class);
	
	public static String getQuery(String query, String msisdn) {
		String lastDigit = String.valueOf(msisdn.charAt(msisdn.length() - 1));
		String finalQuery = query.replace("{msisdn}", msisdn);
		finalQuery = finalQuery.replace("{table_index}", lastDigit);
		finalQuery = finalQuery.replace("{userId}", msisdn);
		finalQuery = finalQuery.replace("{vMsisdn}", msisdn);
		finalQuery = finalQuery.replace("{voiceMessageId}", msisdn);
		System.out.println("Final Query="+finalQuery);
		return finalQuery;
	}
	public static String getVoiceSmsQuery(String query, String vMsisdn,String zvMsisdn) {
		String finalQuery = query.replace("{vMsisdn}", vMsisdn);
		finalQuery = finalQuery.replace("{zvMsisdn}", zvMsisdn);
		return finalQuery;
	}
	
	public static String getSMSText(String baseSmsText,String msisdn,String currentDate, String currentTime) {
		String finalSmsText= baseSmsText.replace("{msisdn}", msisdn);
		finalSmsText=finalSmsText.replace("{currentDate}", currentDate);
		finalSmsText=finalSmsText.replace("{currentTime}", currentTime);
		
		return finalSmsText;
	}
	
	public static String getCatSubCatQuery(String query, String categoryId, String subCategoryId) {
		String finalQuery = query.replace("{categoryId}", categoryId);
		finalQuery = finalQuery.replace("{subCategoryId}", subCategoryId);
		return finalQuery;
	}
	
	public static String getAccountInsertQuery(String query, String msisdn,String userId,String gender,String ageGroup,String operator, String defaultNotificationOpt) {
		String finalQuery = query.replace("{msisdn}", msisdn);
		finalQuery = finalQuery.replace("{table_index}", String.valueOf(msisdn.charAt(msisdn.length() - 1)));
		finalQuery = finalQuery.replace("{userId}", userId);
		finalQuery = finalQuery.replace("{gender}", gender);
		finalQuery = finalQuery.replace("{ageGroup}", ageGroup);
		finalQuery = finalQuery.replace("{operator}", operator);
		finalQuery = finalQuery.replace("{defaultNotificationOpt}", defaultNotificationOpt);
		return finalQuery;
	}
	
	public static String getTonePlayerDtmfInsertQuery(String query, String aparty, String bparty, String toneId, String digits, String startTime,
			String endTime, String starToCopy) {
		String finalQuery = query.replace("{aparty}", aparty);
		finalQuery = finalQuery.replace("{bparty}", bparty);
		finalQuery = finalQuery.replace("{tone_id}", toneId);
		finalQuery = finalQuery.replace("{digits}", digits);
		finalQuery = finalQuery.replace("{start_time}",startTime );
		finalQuery = finalQuery.replace("{end_time}",endTime );
		finalQuery = finalQuery.replace("{star_to_copy}",starToCopy );
		return finalQuery;
	}
	public static String getTonePlayerCDRQuery(String query, String aparty, String bparty,String playStatus,String toneType, String toneId, String playStartTime,
			String playEndTime) {
		String finalQuery = query.replace("{aparty}", aparty);
		finalQuery = finalQuery.replace("{bparty}", bparty);
		finalQuery = finalQuery.replace("{playStatus}", playStatus);
		finalQuery = finalQuery.replace("{reason}", playStatus);
		finalQuery = finalQuery.replace("{toneType}", toneType);
		finalQuery = finalQuery.replace("{duration}", toneType);
		finalQuery = finalQuery.replace("{toneId}", toneId);
		finalQuery = finalQuery.replace("{lastNode}", toneId);
		finalQuery = finalQuery.replace("{playStartTime}", playStartTime);
		finalQuery = finalQuery.replace("{callStartTime}", playStartTime);
		finalQuery = finalQuery.replace("{playEndTime}",playEndTime );
		finalQuery = finalQuery.replace("{callEndTime}",playEndTime );
		return finalQuery;
	}
	public static String getIvrCDRQuery(String query, String aparty, String bparty,String reason,String duration, String lastNode, String callStartTime,
			String callEndTime) {
		String finalQuery = query.replace("{aparty}", aparty);
		finalQuery = finalQuery.replace("{bparty}", bparty);
		
		finalQuery = finalQuery.replace("{reason}", reason);
		
		finalQuery = finalQuery.replace("{duration}", duration);
		
		finalQuery = finalQuery.replace("{lastNode}", lastNode);
		
		finalQuery = finalQuery.replace("{callStartTime}", callStartTime);
		
		finalQuery = finalQuery.replace("{callEndTime}",callEndTime );
		return finalQuery;
	}
	public static String getIvrLangInsertQuery(String query, String aparty, String bparty, String ivrLang)
	{
		String finalQuery = query.replace("{aparty}", aparty);
		finalQuery = finalQuery.replace("{ivrLang}", ivrLang);
		return finalQuery;
		
	}
	public static String insertToneInfoQuery(String query, String aparty, String toneType, String toneTypeIdx,String callingParty, String toneId,String status,String songName)
	{
		String finalQuery = query.replace("{aparty}", aparty);
		finalQuery = finalQuery.replace("{toneType}", toneType);
		finalQuery = finalQuery.replace("{toneTypeIdx}", toneTypeIdx);
		finalQuery = finalQuery.replace("{callingParty}", callingParty);
		finalQuery = finalQuery.replace("{toneId}", toneId);
		finalQuery = finalQuery.replace("{status}", status);
		finalQuery = finalQuery.replace("{songName}", songName);
		return finalQuery;
	}
	public static String insertEventBaseChargingQuery(String query,String transactionId,String aparty,String vMsisdn,String interfaceId, String action,String serviceId, String productId, String isCharging, String result, String errorCode)
	{
		
		String finalQuery = query.replace("{transactionId}", transactionId);
		finalQuery = finalQuery.replace("{aparty}", aparty);
		finalQuery = finalQuery.replace("{vMsisdn}", vMsisdn);
		finalQuery = finalQuery.replace("{interfaceId}", interfaceId);
		finalQuery = finalQuery.replace("{action}", action);
		finalQuery = finalQuery.replace("{serviceId}", serviceId);
		finalQuery = finalQuery.replace("{productId}", productId);
		finalQuery = finalQuery.replace("{isCharging}", isCharging);
		finalQuery = finalQuery.replace("{result}", result);
		finalQuery = finalQuery.replace("{errorCode}", errorCode);
		
		return finalQuery;
	}
	public static String insertVsmsMessageDetailsQuery(String query,String transactionId,String subscriberId,String vMsisdn,String interfaceId,String status,String duration,String recordingPath)
	{
		
		String finalQuery = query.replace("{transactionId}", transactionId);
		finalQuery = finalQuery.replace("{subscriberId}", subscriberId);
		finalQuery = finalQuery.replace("{vMsisdn}", vMsisdn);
		finalQuery = finalQuery.replace("{interfaceId}", interfaceId);
		finalQuery = finalQuery.replace("{status}", status);
		finalQuery = finalQuery.replace("{duration}", duration);
		finalQuery = finalQuery.replace("{recordingPath}", recordingPath);
		
		
		return finalQuery;
	}
	
	public static String updateSubProfileQuery(String query, String aparty, String bparty, String sub_status, String offer)
	{
		String finalQuery = query.replace("{aparty}", aparty);
		finalQuery = finalQuery.replace("{sub_status}", sub_status);
		finalQuery = finalQuery.replace("{offer}", offer);
		return finalQuery;
		
	}
	
	
	public static String getQuery(String query, String msisdn,String tableIndex, String searchText) {
		String finalQuery = query.replace("{msisdn}", msisdn);
		finalQuery = finalQuery.replace("{searchText}", searchText);
		finalQuery = finalQuery.replace("{get_table_index}", tableIndex);
		System.out.println("Final Query="+finalQuery);
		return finalQuery;
	}
	public static String getCatSubCatQuery(String query, String categoryId,String subCategoryId,String contentCount) {
		String finalQuery = query.replace("{categoryId}", categoryId);
		finalQuery = finalQuery.replace("{subCategoryId}", subCategoryId);
		finalQuery = finalQuery.replace("{contentCount}", contentCount);
		System.out.println("Final Query="+finalQuery);
		return finalQuery;
	}
	
	
	public static String getAllTableQuery(String query, String index) {
		String finalQuery = query.replace("{all_table_index}", String.valueOf(index));
		return finalQuery;
	}
	
	public static String getGender(String query, String msisdn,JdbcTemplate jdbcTemplate) {
		String genderQuery = query.replace("{msisdn}",msisdn);
		genderQuery = genderQuery.replace("{table_index}", String.valueOf(msisdn.charAt(msisdn.length() - 1)));
		try {
			return jdbcTemplate.queryForObject(genderQuery, String.class);
		} catch (Exception e) {
			System.out.println("SQL Exception" + e +"Query="+query);
			e.printStackTrace();
			return "";
		}
		
	}
	public static String runLinuxCommand(String command) {
		
		try {
			Process child = Runtime.getRuntime().exec(command);
		}catch(Exception e)
		{
			return "exception occurred";
		}
//		InputStream in = child.getInputStream();
//		int c;
//		while ((c = in.read()) != -1) {
//		      System.out.println((char) c);
//		    }
//		in.close();

		 return "";
	}
	
	public static boolean isNumber(String input) {
		for(int i=0; i<input.length();i++) {
			if(Character.isDigit(input.charAt(i)) == false) {
				return false;
			}
		}
		return true;
	}
	
	
}
