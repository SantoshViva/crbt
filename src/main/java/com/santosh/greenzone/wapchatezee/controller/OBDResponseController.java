package com.santosh.greenzone.wapchatezee.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.santosh.greenzone.services.impl.OBDServiceImpl;
import com.santosh.greenzone.wapchatezee.model.ApplicationResponseCodes;
import com.santosh.greenzone.wapchatezee.model.OBDPlatformCallback;
import com.santosh.greenzone.wapchatezee.model.ResponseDTO;
import com.santosh.greenzone.wapchatezee.model.ResponseHeader;







@Controller
@CrossOrigin
public class OBDResponseController {

	private static final Logger logger = LogManager.getLogger(OBDController.class);
	@Autowired	
	private Environment env;
	
	@RequestMapping(method = RequestMethod.POST, value="/obdResponse")
	public ResponseEntity<?> obdResponse(@RequestParam("aparty") String aparty, @RequestParam("bparty") String bparty,
			@RequestParam("errorCode") String errorCode,
			 HttpServletRequest request, HttpServletResponse servletResponse) {
		
		System.out.println("aparty="+aparty+"|bparty="+bparty+"errorCode="+errorCode);
		ResponseDTO response = new ResponseDTO();
		ResponseHeader responseHeader = new ResponseHeader();
		
		responseHeader.setCode(ApplicationResponseCodes.SUCCESS.code);
		responseHeader.setMessage(ApplicationResponseCodes.SUCCESS.message);
		
		response.setHeader(responseHeader);
		return new ResponseEntity<>(response,HttpStatus.OK);
		
		
	}
	
	
	//@PostMapping("/obdResponseBody")
	@RequestMapping(method=RequestMethod.POST, value="/obdResponseBody",consumes = {"application/x-www-form-urlencoded"})
	public ResponseEntity<?> obdResponseBody(HttpServletRequest request) {
		
		System.out.println("Platform CallBack Response|sessionId="+request.getParameter("sessionid")+"|aParty="+request.getParameter("aParty")+"|bParty="+request.getParameter("bParty")+"|errorCode="+request.getParameter("errorCode")+"|playDuration="+request.getParameter("playDuration")+"|startTime="+request.getParameter("startTime")+"|endTime="
				+request.getParameter("endTime")+"|oldState="+request.getParameter("oldState")+"|currentState="+request.getParameter("currentState")+"|ccxmlEvent="+request.getParameter("ccxmlEvent")+"|trigger="+request.getParameter("trigger")+"|sipErrorCode="+request.getParameter("sipErrorCode")
				+"|sipErrorCodeDesc="+request.getParameter("sipErrorCodeDesc")+"|isupErrorCodeDesc="+request.getParameter("isupErrorCodeDesc")+"|");
		
		
		OBDPlatformCallback obdPlatformCallback= new OBDPlatformCallback();
		
		obdPlatformCallback.setaParty(request.getParameter("aParty"));
		obdPlatformCallback.setbParty(request.getParameter("bParty"));
		obdPlatformCallback.setErroCode(request.getParameter("errorCode"));
		obdPlatformCallback.setPlayDuration(request.getParameter("playDuration"));
		/**Encoded startTime value*/
		String encodedStartTime=request.getParameter("startTime");
		encodedStartTime=encodedStartTime.replace(" ", "%20");
		encodedStartTime=encodedStartTime.replace(":", "%3A");
		obdPlatformCallback.setStartTime(encodedStartTime);
		/**Encoded endTime value*/
		String encodedEndTime=request.getParameter("startTime");
		encodedEndTime=encodedEndTime.replace(" ", "%20");
		encodedEndTime=encodedEndTime.replace(":", "%3A");
		obdPlatformCallback.setEndTime(encodedEndTime);
		
		obdPlatformCallback.setOldState(request.getParameter("oldState"));
		obdPlatformCallback.setCurrentState(request.getParameter("currentState"));
		obdPlatformCallback.setCcxmlEvent(request.getParameter("ccxmlEvent"));
		obdPlatformCallback.setTrigger(request.getParameter("trigger"));
		obdPlatformCallback.setSipErrorCode(request.getParameter("sipErrorCode"));
		obdPlatformCallback.setSipErrorCodeDesc((request.getParameter("sipErrorCodeDesc")).replace(" ", "%20"));
		obdPlatformCallback.setIsupErrorCodeDesc((request.getParameter("sipErrorCodeDesc")).replace(" ","%20"));
		obdPlatformCallback.setSessionId(request.getParameter("sessionid"));
		
		
		logger.info("Platform CallBack Response|sessionId="+request.getParameter("sessionid")+"|aParty="+request.getParameter("aParty")+"|bParty="+request.getParameter("bParty")+"|errorCode="+request.getParameter("errorCode")+"|playDuration="+request.getParameter("playDuration")+"|startTime="+request.getParameter("startTime")+"|endTime="
		+request.getParameter("endTime")+"|oldState="+request.getParameter("oldState")+"|currentState="+request.getParameter("currentState")+"|ccxmlEvent="+request.getParameter("ccxmlEvent")+"|trigger="+request.getParameter("trigger")+"|sipErrorCode="+request.getParameter("sipErrorCode")+"|sipErrorCodeDesc="+request.getParameter("sipErrorCodeDesc")+"|isupErrorCodeDesc="+request.getParameter("isupErrorCodeDesc")+"|");
		// Hit Scheduler CallBack URL 
		//http://127.0.0.1:8080/obdCallBack?aparty=9810594997&bparty=12345&sessionId=234231&erroCode=0&playDuration=79.22&startTime=&endTime=
		String obdSchedulerCallback = env.getProperty("OBD_SCHEDULER_CALLBACK_URL");
		obdSchedulerCallback = obdSchedulerCallback +"?aparty="+obdPlatformCallback.getaParty()+"&bparty="+obdPlatformCallback.getbParty()+"&sessionId="+obdPlatformCallback.getSessionId()+"&errorCode="+obdPlatformCallback.getSipErrorCode()+"&playDuration="+obdPlatformCallback.getPlayDuration()+"&startTime="+obdPlatformCallback.getStartTime()+"&endTime="+obdPlatformCallback.getEndTime()+"&sipErrorCodeDesc="+obdPlatformCallback.getSipErrorCodeDesc()+"&isupErrorCode="+obdPlatformCallback.getErroCode()+"&isupErrorCodeDesc="+obdPlatformCallback.getIsupErrorCodeDesc();
		System.out.println("Scheduler Callback URL="+obdSchedulerCallback);
		logger.info("Scheduler Callback URL="+obdSchedulerCallback);
		OBDServiceImpl obdService = new OBDServiceImpl();
		String sessionId=obdService.getCCXMLResponse(obdSchedulerCallback,"Scheduler");
		System.out.println("response="+sessionId);
		
		ResponseDTO response = new ResponseDTO();
		ResponseHeader responseHeader = new ResponseHeader();
		
		responseHeader.setCode(ApplicationResponseCodes.SUCCESS.code);
		responseHeader.setMessage(ApplicationResponseCodes.SUCCESS.message);
		response.setHeader(responseHeader);
		return new ResponseEntity<>(response,HttpStatus.OK);
	}

	
}
