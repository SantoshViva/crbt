 package com.santosh.greenzone.wapchatezee.controller;

import java.util.List;
import java.util.Map;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


import com.santosh.greenzone.utils.ChatUtils;


@CrossOrigin
@RestController
public class CatSubCategorySongsDetails {

	private static final Logger logger = LogManager.getLogger(CatSubCategorySongsDetails.class);
	@Autowired
	Environment env;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	@RequestMapping(value = "/getCatSubCategorySongsDetails",method = RequestMethod.GET)
	@ResponseBody
	public String getToneIdInforXML(@RequestParam("aparty") String aparty, @RequestParam("bparty") String bparty,@RequestParam("serviceId") String serviceId,@RequestParam("subServiceId") String subServiceId,@RequestParam("categoryId") String categoryId,@RequestParam("subCategoryId") String subCategoryId, HttpServletRequest req,
			HttpServletResponse res) {
		
		logger.info("getUserProfileQuery|aparty="+aparty+"|bparty="+bparty+"|serviceId="+serviceId+"|subServiceId="+subServiceId+"|categoryId="+categoryId+"|subCategoryId="+subCategoryId);
		
		logger.info("Query=" + env.getProperty("SQL31_CAT_SUB_CAT_SONGS_DETAILS"));
		
				
		// Replace Table Index & aparty 
		String catSubCatQuery = ChatUtils.getCatSubCatQuery(env.getProperty("SQL31_CAT_SUB_CAT_SONGS_DETAILS"), categoryId,subCategoryId);
		
		
		logger.info("final profileCheck Query="+catSubCatQuery);
		
		String responseString = new String();
		String dbError ="N";
		//responseString = responseString.concat("CAT_SUB_CAT_RES.dbError=\'"+"N"+"\';");
		try {
			List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(catSubCatQuery);
			
			if(queryForList.isEmpty())
			{
				logger.info("No Record Found in SQL|aparty="+aparty);
				
							
			}else 
			{
				int toneCount =0;
				for (Map<String, Object> row : queryForList) {
					toneCount++;
					
					if(row.get("tone_id")== null)
					{
						responseString = responseString.concat("CAT_SUB_CAT_RES.toneId=\'"+env.getProperty("DEFAULT_TONE_ID")+"\';");
						
					}else
					{
						responseString = responseString.concat("CAT_SUB_CAT_RES.toneId_"+toneCount+"=\'"+row.get("tone_id")+"\';");
						
					}
					
					logger.info("QueryResult|subscriberId="+bparty+"|toneId="+row.get("tone_id").toString()+"|");
				}
				logger.info("Aparty="+aparty+"|categoryId="+categoryId+"|subCategoryId="+subCategoryId+"|count="+toneCount);
				responseString = responseString.concat("CAT_SUB_CAT_RES.toneCount=\'"+toneCount+"\';");
				
			}
		} catch (Exception e) {
			logger.error("SQL Exception" + e +"Query="+catSubCatQuery);
			logger.error("No Row Found");
			responseString = responseString.concat("CAT_SUB_CAT_RES.toneId=\'"+env.getProperty("DEFAULT_TONE_ID")+"\';");
			responseString = responseString.concat("CAT_SUB_CAT_RES.toneCount=\'"+"1"+"\';");
			dbError="Y";
			e.printStackTrace();
		}

		responseString = responseString.concat("CAT_SUB_CAT_RES.dbError=\'"+dbError+"\';");	
		
		return responseString;
		
	}
	
	
}
