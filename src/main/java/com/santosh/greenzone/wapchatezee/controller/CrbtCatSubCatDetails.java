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
	public ResponseEntity<?> crbtCatSubCatInfo(@RequestParam("categoryId") String categoryId,@RequestParam("subCategoryId") String subCategoryId,@RequestParam("contentCount") String contentCount,HttpServletRequest req,
			HttpServletResponse res) {
		
		    
			logger.info("crbtCatSubCatInfo|categoryId="+categoryId+"|subCategoryId="+subCategoryId+"|contentCount="+contentCount);
		    
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
				}
				else 
				{
					for (Map<String, Object> row : queryForList) {
						MyCategorySubCategory  content = new MyCategorySubCategory();
						content.setSongId(row.get("songid").toString());
						content.setSongName(row.get("songname").toString());
					    contents.add(content);
					}
				}
				
			}catch(Exception e) {
				e.printStackTrace();
			}
			
			ResponseDTO<List<MyCategorySubCategory>> response = new ResponseDTO<>();
			response.setBody(contents);
			
			return new ResponseEntity<>(response, HttpStatus.OK);
		
	}
	
	
}
