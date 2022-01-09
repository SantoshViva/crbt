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

import org.apache.catalina.connector.Response;
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
import com.santosh.greenzone.wapchatezee.model.CrbtTpSongInfoDetails;
import com.santosh.greenzone.wapchatezee.model.MyCategorySubCategory;
import com.santosh.greenzone.wapchatezee.model.ResponseDTO;
import com.santosh.greenzone.wapchatezee.model.ResponseHeader;



@CrossOrigin
@RestController
public class CheckTpSongDetails {

	private static final Logger logger = LogManager.getLogger(CheckTpSongDetails.class);
	@Autowired
	Environment env;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	@RequestMapping(value = "/checkTpSongDetails",method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> checkTpSongDetails(@RequestParam("subscriberId") String subscriberId,HttpServletRequest req,
			HttpServletResponse res) {
		logger.info("checkTpSongDetails|subscriberId="+subscriberId);
	    
		String selectQuery = ChatUtils.getQuery(env.getProperty("SQL23_USER_TONE_INFO_DETAILS"), subscriberId);
		logger.info("final Query="+selectQuery);
		List<CrbtTpSongInfoDetails> contents = new ArrayList<CrbtTpSongInfoDetails>();
		ResponseDTO<List<CrbtTpSongInfoDetails>> response = new ResponseDTO<>();
		ResponseHeader addHeader = new ResponseHeader();
		try {
			List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(selectQuery);
			if(queryForList.isEmpty())
			{
					/**No record found. Error handling here*/
				addHeader.setCode(1);
				addHeader.setMessage("Subscriber does not exist");			
					
			}
			else 
			{
				addHeader.setCode(0);
				addHeader.setMessage("Subscriber exist");
				for (Map<String, Object> row : queryForList) {
					CrbtTpSongInfoDetails  content = new CrbtTpSongInfoDetails();
					if(row.get("tone_id")!=null )
						content.setSongId(row.get("tone_id").toString());
					if(row.get("song_name")!=null)
						content.setSongName(row.get("song_name").toString());
					if(row.get("song_path")!=null)
						content.setSongPath(row.get("song_path").toString());
				    contents.add(content);
				}
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			logger.error("Exception="+e);
		}
		
		response.setHeader(addHeader);
		response.setBody(contents);

		return new ResponseEntity<>(response, HttpStatus.OK);
	
		
	}
	
	
}
