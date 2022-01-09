package com.santosh.greenzone.wapchatezee.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.PostRemove;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import com.santosh.greenzone.wapchatezee.model.MyCategorySubCategory;
import com.santosh.greenzone.wapchatezee.model.ResponseDTO;



@CrossOrigin
@RestController
public class CrbtCatSubCatDetails {

	private static final Logger logger = LogManager.getLogger(CrbtCatSubCatDetails.class);
	@Autowired
	Environment env;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	@RequestMapping(value = "/crbtCatSubCatDetails",method = RequestMethod.GET)
	@ResponseBody
	public String crbtCatSubCatInfo(@RequestParam("aparty") String aparty,@RequestParam("bparty") String bparty,@RequestParam("categoryId") String categoryId,@RequestParam("subCategoryId") String subCategoryId,@RequestParam("contentCount") String contentCount,HttpServletRequest req,
			HttpServletResponse res) {
		
		    
			logger.info("crbtCatSubCatInfo|aparty="+aparty+"|bparty="+bparty+"|categoryId="+categoryId+"|subCategoryId="+subCategoryId+"|contentCount="+contentCount);
			String responseString = new String();
			int counter=0;
			String dbError ="N";
			if(contentCount==null)
			{
				contentCount="10";
			}
			
			String selectQuery = ChatUtils.getCatSubCatQuery(env.getProperty("SQL36_SELECT_CAT_SUBCAT_DETAILS"), categoryId,subCategoryId,contentCount);
			logger.info("final Query="+selectQuery);
			List<MyCategorySubCategory> contents = new ArrayList<MyCategorySubCategory>();
			try {
				List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(selectQuery);
				if(queryForList.isEmpty())
				{
						/**No record found. Error handling here*/
					responseString = responseString.concat("CRBT_CAT_SUBCAT_RES.songCount=\'"+"0"+"\';");
				}
				else 
				{
					for (Map<String, Object> row : queryForList) {
						Integer intCounterLocal = new Integer(counter);
						
					    if(row.get("songid")==null||row.get("songname")== null||row.get("songid").toString().isEmpty()||row.get("songname").toString().isEmpty())
						{
							logger.error("Category & sub category songs are null in database|aparty="+aparty);
							continue;
						}else
						{	
							String songPath=row.get("songid").toString();
							songPath=songPath.substring(0,2)+"/"+songPath.substring(2,4)+"/"+songPath.substring(4,7)+"/"+songPath+"/"+songPath+"_audio.wav";
							logger.info("songid="+row.get("songid")+"|songname="+row.get("songname")+"|songpath="+songPath);
							responseString = responseString.concat("CRBT_CAT_SUBCAT_RES.songId["+intCounterLocal.toString()+"]"+"=\'"+row.get("songid")+"\';");
							responseString = responseString.concat("CRBT_CAT_SUBCAT_RES.songName["+intCounterLocal.toString()+"]"+"=\'"+row.get("songname")+"\';");
							responseString = responseString.concat("CRBT_CAT_SUBCAT_RES.songPath["+intCounterLocal.toString()+"]"+"=\'"+songPath+"\';");
							
						}
					    counter++;
					   }
						Integer intCounter = new Integer(counter);
						responseString = responseString.concat("CRBT_CAT_SUBCAT_RES.songCount=\'"+intCounter.toString()+"\';");
				}
				
			}catch(Exception e) {
				
				logger.error("SQL Exception" + e +"Query="+selectQuery);
				logger.error("No Row Found");
				responseString = responseString.concat("CRBT_CAT_SUBCAT_RES.songCount=\'"+"0"+"\';");
				dbError="Y";
				e.printStackTrace();
			}
			
			responseString = responseString.concat("CRBT_CAT_SUBCAT_RES.dbError=\'"+dbError+"\';");
			return responseString;
		
	}
	
	
}
