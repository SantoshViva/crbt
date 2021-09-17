package com.santosh.greenzone.wapchatezee.controller;



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

import com.santosh.greenzone.services.impl.OBDServiceImpl;
import com.santosh.greenzone.utils.ChatUtils;
import com.santosh.greenzone.wapchatezee.model.OBDSchedulerResponse;

import java.net.URLDecoder;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;



/**
 * This controller is use for OBD scheduler & hit to Telephony platform 
 * */
@CrossOrigin
@RestController
public class OBDController {

	
	private static final Logger logger = LogManager.getLogger(OBDController.class);
	
//	@Autowired
//	private OBDService obdService;
	@Autowired
	Environment env;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	@RequestMapping(value = "/obdController",method = RequestMethod.GET)
	@ResponseBody
	public OBDSchedulerResponse obdController(@RequestParam("aparty") String aparty, @RequestParam("bparty") String bparty,@RequestParam("flowId") String flowId,
			@RequestParam("campId") String campId,@RequestParam("promptPath") String promptPath,HttpServletRequest req,
			HttpServletResponse res) {
		
		System.out.println("OBDController--|aparty=" + aparty+"|bparty="+bparty+"|flowId="+flowId+"|campId="+campId+"|promptPath="+promptPath+"|");
		
		
		logger.info("Request Received from CM|aparty="+ aparty+"|bparty="+bparty+"|flowId="+flowId+"|campId="+campId+"|promptPath="+promptPath);	
		
		String mscIpPort = env.getProperty("MSC_IP_PORT");
		String promptBasePath= env.getProperty("PROMPT_BASE_PATH");
		String fullPromptPath = promptBasePath + URLDecoder.decode(promptPath);
		logger.info("Request Received from CM|aparty="+ aparty+"|bparty="+bparty+"|flowId="+flowId+"|campId="+campId+"|promptPath="+promptPath+"|fullPromptPath="+fullPromptPath);
		String returnLinuxCommandResult="-1";
		String linuxCommand ="";
		boolean  audioFormatChange=false;
		/**Check Codec information*/
			linuxCommand = "file " + fullPromptPath;
		    logger.info("linuxCommand="+linuxCommand);
		    try {
		    	Process childResult = Runtime.getRuntime().exec(linuxCommand);
		    	BufferedReader input = new BufferedReader(new InputStreamReader(childResult.getInputStream()));
		    	String line = null;
		    	while((line = input.readLine())!=null)
		    	{
		    		logger.info("linuxCommand output="+line);
		    		if(line.contains("ITU G.711") && line.contains("A-law") && line.contains("WAVE audio") && line.contains("mono 8000 Hz")) {
		    			audioFormatChange=false;
		    			logger.info("audioFormatChange="+audioFormatChange);
		    		}
		    		else {
		    			audioFormatChange=true;
		    			logger.info("audioFormatChange="+audioFormatChange);
		    		}
		    	}
		    }catch(Exception e) {
		    	logger.error("Exception="+e);
		    }
		/**End Codec information */
		
		
		
		if(audioFormatChange & (fullPromptPath.contains(".wav") || (fullPromptPath.contains(".m4a")) || fullPromptPath.contains(".mp3") ))
		{	
			
			
			String	outputPromptFile;
			if(fullPromptPath.contains(".wav"))
				outputPromptFile= fullPromptPath.replace(".wav", "_output.wav");
			else if(fullPromptPath.contains(".mp3"))
				outputPromptFile= fullPromptPath.replace(".mp3", "_output.wav");
			else
				outputPromptFile= fullPromptPath.replace(".m4a", "_output.wav");
			
			
			logger.info("OutputPromptFileName="+outputPromptFile);

			/**  Start delete _output.wav file*/
			if(outputPromptFile.contains("_output")) 
			{
				String deleteOutputFile = "rm -rf " + outputPromptFile;
				logger.info("deleteOutputFile Command="+deleteOutputFile);
				returnLinuxCommandResult=ChatUtils.runLinuxCommand(deleteOutputFile);
				logger.info("deleteOutputFile Command="+deleteOutputFile+"|returnLinuxCommandResult="+returnLinuxCommandResult);
			}
			/**End delete _output.wav file*/
			
			/**Start codec File Convert*/
			String formatConvertCommand ="ffmpeg -i " + fullPromptPath + " -c pcm_alaw -ac 1 -ar 8000 " + outputPromptFile;
			returnLinuxCommandResult=ChatUtils.runLinuxCommand(formatConvertCommand);
			logger.info("returnFormatConvertCommand="+formatConvertCommand+"|return="+returnLinuxCommandResult);
			
			try {
				logger.info("go for sleep 10");
				Thread.sleep(10);
				logger.info("exit from sleep 10");
			}catch(Exception e) {
				logger.error("Exception Occurred="+e);
			}
			
			/**End Codec file Convert command*/
			/**Thread Sleep 5 seconds*/
			linuxCommand = "chmod -R 755 " + outputPromptFile;
			returnLinuxCommandResult=ChatUtils.runLinuxCommand(linuxCommand);
			logger.info("linuxCommand="+linuxCommand+"|return="+returnLinuxCommandResult);
			try {
				logger.info("go for sleep 10");
				Thread.sleep(100);
				logger.info("exit from sleep 10");
			}catch(Exception e) {
				logger.error("Exception Occurred="+e);
			}
			/**move file from Original*/
			if(promptPath.contains(".m4a"))
			{
				fullPromptPath=fullPromptPath.replace(".m4a", ".wav");
				promptPath= promptPath.replace(".m4a", ".wav");
			}else if(promptPath.contains(".mp3")) {
				fullPromptPath=fullPromptPath.replace(".mp3", ".wav");
				promptPath= promptPath.replace(".mp3", ".wav");
			}
			
			linuxCommand = "mv -f " + outputPromptFile + " " + fullPromptPath;
			returnLinuxCommandResult=ChatUtils.runLinuxCommand(linuxCommand);
			logger.info("linuxCommand="+linuxCommand+"|return="+returnLinuxCommandResult);
		}	
		/**Change chmod in current Prompt File*/
		
		String changModeCommand = "chmod -R 755 " +fullPromptPath ;
		String returnChangeModeCommand=ChatUtils.runLinuxCommand(changModeCommand);
		logger.info("Change Mode|aparty="+ aparty+"|bparty="+bparty+"|flowId="+flowId+"|campId="+campId+"|promptPath="+promptPath+"|"+"|changModeCommand="+changModeCommand);	
		OBDSchedulerResponse obdSchedulerResp = new OBDSchedulerResponse();
		String errorMsg="success";
		String responseCode="0";
		String tempFlowId=","+flowId+",";
		String tempStdCode="";
		if(bparty.contains("+")) 
		{
			System.out.println("+++++");
			tempStdCode= ","+bparty.substring(1,3)+",";
		}else 
		{
			System.out.println("-----="+bparty.substring(0,1));
			tempStdCode= ","+bparty.substring(0,3)+",";
		}
		if(promptPath.isEmpty()) {
			System.out.println("OBDController|prompt Path  is null");
			errorMsg="prompt Path is blank";
			responseCode="1";
		}else if(aparty.isEmpty())
		{
			System.out.println("OBDController|Aparty  is null");
			errorMsg="aparty is blank";
			responseCode="2";
		}else if(bparty.isEmpty())
		{
			System.out.println("OBDController|Aparty  is null");
			errorMsg="bparty is blank";
			responseCode="3";
		}else if((env.getProperty("OBD_MSISDN_LEN")).isEmpty()==false && bparty.length() != Integer.parseInt(env.getProperty("OBD_MSISDN_LEN"))) 
		{	
			System.out.println("OBDController|bparty  length is not match as per configuration|bparty="+bparty+"|configLength="+env.getProperty("OBD_MSISDN_LEN"));
			errorMsg="bparty length is not match";
			responseCode="4";
		}else if(flowId.isEmpty())
		{
			System.out.println("OBDController|Aparty  is null");
			errorMsg="flowId is blank";
			responseCode="5";
		}else if(env.getProperty("OBD_FLOW_ID_LIST").indexOf(tempFlowId)== -1) {
			System.out.println("OBDController|FlowId   is not configured|configFlowId="+env.getProperty("OBD_FLOW_ID_LIST")+"|tempFlowId="+tempFlowId);
			errorMsg="flowId is not configured";
			responseCode="6";
		}else if((env.getProperty("OBD_STD_CODE")).isEmpty()==false && env.getProperty("OBD_STD_CODE").indexOf(tempStdCode)==-1) {
			System.out.println("OBDController|STD Code is not correct|bparty="+bparty+"|configStdCode="+env.getProperty("OBD_STD_CODE")+"|tempStdCode="+tempStdCode);
			errorMsg="STD Code is not correct";
			responseCode="7";
		}
			
		logger.info("All validation has successfully");
		//ResponseDTO response = new ResponseDTO();
		OBDServiceImpl obdService = new OBDServiceImpl();
		try {
			System.out.println("Go to Hit CCXMl URL");
			System.out.println("Make URL");
			String sessionUrl = env.getProperty("CCXML_SESSION_URL");
			String ccxmlUrl = env.getProperty("CCXML_URL");
			
			String ccxmlName = env.getProperty("CCXML_FILE_NAME");
			
			String url = sessionUrl + "?uri=" + ccxmlUrl+ccxmlName;
			
			System.out.println("sessionUrl="+ sessionUrl+"|ccxmlURL="+ccxmlUrl+"|ccxmlName="+ccxmlName+"|url="+url);
			String flowName="";
			if(flowId == "1")
			{
				flowName="welcome.vxml";
			}else {
				flowName="welcome.vxml";
			}
			//url= url +"&vxmlscript=" + flowName +"&dest="+bparty+"&callerid="+aparty+"&mscIpPort="+"172.31.24.137:6050"+"&promptPath="+promptPath;
			url= url +"&vxmlscript=" + flowName +"&dest="+bparty+"&callerid="+aparty+"&mscIpPort="+mscIpPort+"&promptPath="+promptPath;
			System.out.println("Platfomr URL="+url);
			logger.info("Platform URL="+url);
			String sessionId=obdService.getCCXMLResponse(url,"Platform");
			if(sessionId == "-1")
			{
				System.out.println("failed="+sessionId);
				errorMsg="platform error";
				responseCode="-1";
			}
			obdSchedulerResp.setErrorMsg(errorMsg);
			obdSchedulerResp.setResponseCode(responseCode);
			obdSchedulerResp.setSessionId(sessionId);
			System.out.println("Send Response to Scheduler="+obdSchedulerResp.toString());
			logger.info("Send Response to Scheduler="+obdSchedulerResp.toString());
		} catch (Exception e) {
			System.out.println("OBD Controller" + e );
			e.printStackTrace();
		}
			
			
	
		return obdSchedulerResp;
	}
}
