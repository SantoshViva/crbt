package com.santosh.greenzone.services.impl;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.santosh.greenzone.services.PostClientCrbtService;
import com.santosh.greenzone.wapchatezee.model.ProfileResponseDetails;
import com.santosh.greenzone.wapchatezee.model.ResponseDTO;
import com.santosh.greenzone.wapchatezee.model.UserBalanceInfoDetailModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.json.*;

@Service
public class PostClientCrbtServiceImpl implements PostClientCrbtService{

	
	private static final Logger logger = LogManager.getLogger(PostClientCrbtServiceImpl.class);
	
	@Autowired
	Environment env;

	@Autowired
	private RestTemplate restTemplateClient = new RestTemplate(getClientHttpRequestFactory());
	
	
	
	//Override timeouts in request factory
	private SimpleClientHttpRequestFactory getClientHttpRequestFactory() 
	{
	    SimpleClientHttpRequestFactory clientHttpRequestFactory
	                      = new SimpleClientHttpRequestFactory();
	    //Connect timeout
	    clientHttpRequestFactory.setConnectTimeout(10_000);
	     
	    //Read timeout
	    clientHttpRequestFactory.setReadTimeout(10_000);
	    return clientHttpRequestFactory;
	}
	
	
	@Override
	public String sendPostClientCrbtReq(String url,String bodyData, String reqType ) {
		
		
		
		logger.info("POST Core Engine URL="+url+"|reqType="+reqType);
		//ResponseDTO<?> response = new ResponseDTO<>();
		String returnValue="-1";
		try {
			//Set Header value
			HttpHeaders headers = new HttpHeaders();
			String authHeader= "Basic " + "aXZydGVzdDIwOml2cnRlc3QyMA==";
			headers.set("Authorization", authHeader);
			headers.set("Content-Type", "application/json");
			
			HttpEntity<String> requestBody = new HttpEntity<>(bodyData,headers);
			ResponseEntity<?> response= restTemplateClient.postForEntity(url, requestBody, String.class);
			//response = restTemplateClient.getForObject(url, ResponseDTO.class );
			if(response.getStatusCode() == HttpStatus.CREATED) {
				logger.info("response created="+response.getBody());
			}
			else if(response.getStatusCode() == HttpStatus.ACCEPTED){
				logger.info("response http accepted="+response.getBody());
			}
			else {
				//logger.info("response from1="+response);
				logger.info("response header from2="+response.getHeaders());
				logger.info("response status code from3="+response.getStatusCode());
				logger.info("response body from4="+response.getBody());
				String resBodyData = response.getBody().toString();
				logger.trace("response from5="+resBodyData);
				JSONObject json = new JSONObject(resBodyData);
				logger.info("response json="+json.toString());
				String result=json.getString("result");
				logger.info("result="+result);
				if(reqType.equalsIgnoreCase("balance-query")==true)
				{
					String sufficientBalance="N";
					logger.info("balance-query-----------");
					String results = json.getString("result");
					logger.info("results="+results);
					int resultCode = json.getInt("resultCode");
					logger.info("resultCode="+resultCode);
					if(result.equalsIgnoreCase("ERROR"))
					{
						if(resultCode==5030)
						{
							sufficientBalance="Y";
							logger.info("Number is postpaid");
						}
						return sufficientBalance;
					}	
					
					JSONArray jsonArr = json.getJSONArray("accountsInfo");
					logger.info("jsonArrLength="+jsonArr.length());
					
					for(int i=0;i<jsonArr.length();i++)
					{
						String accountId= jsonArr.getJSONObject(i).getString("accountId");
						int balance = jsonArr.getJSONObject(i).getInt("balance");
						int accountType = jsonArr.getJSONObject(i).getInt("accountType");
						logger.info("count1="+i+"|AccountId="+accountId+"|balance="+balance+"|accountType="+accountType);
						if(accountType == 2000)
						{
							logger.info("Main account Balance="+balance);
							if(balance >=300)
							{
								sufficientBalance="Y";
								return sufficientBalance;
							}
						}
					}
					return sufficientBalance;
				}
				int errorCode=-1;
				//errorCode=json.getInt("errorCode");
				logger.error("Response="+result+"|reqTpe="+reqType+"|errorCode="+json.getString("errorCode")+"|errorMsg="+json.getString("errorMsg"));
				if(result.equalsIgnoreCase("ok")|| result.equalsIgnoreCase("SUCCESS")) 
				{
					switch(reqType)
					{
						case "subscribe" :
						case "event-billing":	
							//errorCode=json.getInt("errorCode");
							logger.info(reqType+" response|result="+result);
							//returnValue="Y";
							//if(errorCode == 0)
							{
								returnValue="Y";
							}
							break;
						case "check-subscription":
																			
							if(json.getString("errorCode").equalsIgnoreCase("0"))
							{
								returnValue = "Y";
								
							}else {
								
								returnValue = "N";
							}
							logger.info("response result|returnValue="+returnValue);
							break;
						case "unsubscribe":
							//errorCode=json.getInt("errorCode");
							logger.info("unsubscribe response|result="+result);
							if(result == "ok")
							{
								returnValue="Y";
							}
							break;
						case "tone":
							logger.info("Tone Changes");
							break;
						default:
							logger.error("default case");
							break;
					}
				}
				else if(result.equalsIgnoreCase("error") || result.equalsIgnoreCase("FAIL"))
				{
					
					switch(reqType)
					{
						case "subscribe" :
							
							if(json.getString("errorCode").equalsIgnoreCase("0"))
							{
								returnValue="A";
							}
							else {
								returnValue="F";
							}
							break;
						case "event-billing":
							if(json.getString("errorCode").equalsIgnoreCase("0"))
							{
								returnValue="-1";
							}else {
								returnValue=json.getString("errorCode");
							}
							break;
						case "check-subscription":
							String errorCodeValue=json.getString("errorCode");
							logger.info("errorCode="+errorCodeValue+"|errorMessage="+json.getString("errorMsg"));
							
							//logger.info("check-subscription response|result="+result+"|data="+data+"|errorCode="+errorCode);
							if(errorCodeValue.equalsIgnoreCase("1"))
							{
								returnValue = "N";
														
							}else {
								returnValue = "N";
								logger.info("response result|data="+errorCode+"|returnValue="+returnValue);
								
							}
							break;
						case "unsubscribe":
							logger.info("ubsubscribe response");
							break;
						case "tone":
							logger.info("Tone Changes");
							break;
						default:
							logger.error("default case");
							break;
					}
	
				}else {
					
					logger.error("default result case");
				}
				
//				String result=json.getString("result");
//				boolean data=json.getBoolean("data");
//				logger.info("response result="+result+"|data="+data);
//				logger.info("profile query response result="+result);
//				if(result.equalsIgnoreCase("ok"))
//				{
//					logger.info("response result ok");
//					if(data== true)
//					{
//						logger.info("response result|data="+data+"|returnValue=-1");
//						returnValue = "Y";
//					}else {
//						logger.info("response result|data="+data+"|returnValue=-2");
//						returnValue = "N";
//					}
//				}else {
//					logger.info("response result else case");
//				}
//							
			}
		}catch (HttpStatusCodeException ex) {
			logger.error("Exception Http Error Code = "+ex.getRawStatusCode());
			logger.error("response Http Error StatusCode="+ex.getStatusCode().toString());
			logger.error("response Http Body="+ex.getResponseBodyAsString());
			HttpHeaders headers = ex.getResponseHeaders();
			logger.error("response Content-Type="+headers.get("Content-Type"));
			logger.error("response Server="+headers.get("Server"));
			logger.error("Exception from "+bodyData+"|Http Error Code="+ex.getRawStatusCode()+"|Status Code="+ex.getStatusCode().toString()+"|Response Body="+ex.getResponseBodyAsString());
		}catch(ResourceAccessException ex) {
			logger.error("Reosource Access Exception="+ ex.getMessage());
			logger.error("Reosource Access Exception="+ ex.getCause());
			returnValue="E";
		}catch(Exception ex) {
			System.out.println("Exception="+ ex.getMessage());
		}
		logger.info("response result|returnValue="+returnValue);
		return returnValue;	
	}
}
