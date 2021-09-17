 package com.santosh.greenzone.wapchatezee.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.PostRemove;
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

import com.santosh.greenzone.services.impl.PostClientCrbtServiceImpl;


@CrossOrigin
@RestController
public class BalanceQuery {

	private static final Logger logger = LogManager.getLogger(BalanceQuery.class);
	@Autowired
	Environment env;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	@RequestMapping(value = "/getUserBalance",method = RequestMethod.GET)
	@ResponseBody
	public String getUserBalanceController(@RequestParam("aparty") String aparty, @RequestParam("bparty") String bparty,@RequestParam("langId") String langId,@RequestParam("serviceId") String serviceId, HttpServletRequest req,
			HttpServletResponse res) {
		
		logger.info("getUserBalanceController|aparty="+aparty+"|bparty="+bparty+"|serviceId="+serviceId+"|langId="+langId);
		String responseString = new String();
		
		String balanceQueryUrl=env.getProperty("BALANCE_QUERY_URL");
		logger.info("getUserBalanceController|BALANCE_QUERY_URL="+balanceQueryUrl);
		String jsonBodyData="{"+" \"msisdn\": \"" +aparty + "\"}";
		logger.info("jsonBodyData="+jsonBodyData);
		PostClientCrbtServiceImpl postClientCrbtService = new PostClientCrbtServiceImpl();
		String postClientRes = postClientCrbtService.sendPostClientCrbtReq(balanceQueryUrl, jsonBodyData, "balance-query");
		logger.info("sufficientBalance="+postClientRes+"|aparty="+aparty);
		responseString = responseString.concat("BALANCE_QUERY_RES.sufficientBalance=\'"+postClientRes+"\';");
		return responseString;
		
	}
	
	
}
