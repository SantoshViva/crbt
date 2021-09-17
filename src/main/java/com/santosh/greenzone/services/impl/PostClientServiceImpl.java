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

import com.santosh.greenzone.services.PostClientService;
import com.santosh.greenzone.wapchatezee.model.ProfileResponseDetails;
import com.santosh.greenzone.wapchatezee.model.ResponseDTO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.json.*;

@Service
public class PostClientServiceImpl implements PostClientService{

	
	private static final Logger logger = LogManager.getLogger(PostClientServiceImpl.class);
	
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
	public String sendPostClientReq(String url,String bodyData, String reqType ) {
		
		
		
		logger.info("POST Client URL="+url+"|reqType="+reqType);
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
				logger.info("response from2="+response.getHeaders());
				logger.info("response from3="+response.getStatusCode());
				logger.info("response from4="+response.getBody());
				String resBodyData = response.getBody().toString();
				logger.trace("response from5="+resBodyData);
				JSONObject json = new JSONObject(resBodyData);
				logger.info("response json="+json.toString());
				String result=json.getString("result");
				int errorCode=-1;
				//errorCode=json.getInt("errorCode");
				if(result.equalsIgnoreCase("ok")) 
				{
					switch(reqType)
					{
						case "subscribe" :
							//errorCode=json.getInt("errorCode");
							logger.info("subscribe response|result="+result);
							//returnValue="Y";
							//if(errorCode == 0)
							{
								returnValue="Y";
							}
							break;
						case "check-subscription":
							boolean data=json.getBoolean("data");
//							int errorCode=json.getInt("errorCode");
							logger.info("check-subscription response|result="+result+"|data="+data+"|errorCode="+errorCode);
							if(data== true)
							{
								logger.info("response result|data="+data+"|returnValue=-1");
								returnValue = "Y";
							}else {
								logger.info("response result|data="+data+"|returnValue=-2");
								returnValue = "N";
							}
							break;
						case "unsubscribe":
							//errorCode=json.getInt("errorCode");
							logger.info("unsubscribe response|result="+result);
							//if(result == "ok")
							//{
								returnValue="Y";
							//}
							break;
						default:
							logger.error("default case");
							break;
					}
				}
				else if(result.equalsIgnoreCase("error"))
				{
					switch(reqType)
					{
						case "subscribe" :
							String message =json.getString("message");
							logger.info("subscribe response|result="+result+"|errorCode="+errorCode+"|message="+message);
							if(errorCode == 0)
							{
								returnValue="N";
							}
							break;
						case "check-subscription":
							boolean data=json.getBoolean("data");
//							int errorCode=json.getInt("errorCode");
							logger.info("check-subscription response|result="+result+"|data="+data+"|errorCode="+errorCode);
							if(data== true)
							{
								returnValue = "Y";
								logger.info("response result|data="+data+"|returnValue="+returnValue);
								
							}else {
								returnValue = "N";
								logger.info("response result|data="+data+"|returnValue="+returnValue);
								
							}
							break;
						case "unsubscribe":
							logger.info("ubsubscribe response");
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
			System.out.println("Exception Http Error Code = "+ex.getRawStatusCode());
			System.out.println("response Http Error StatusCode="+ex.getStatusCode().toString());
			System.out.println("response Http Body="+ex.getResponseBodyAsString());
			HttpHeaders headers = ex.getResponseHeaders();
			System.out.println("response Content-Type="+headers.get("Content-Type"));
			System.out.println("response Server="+headers.get("Server"));
			logger.error("Exception from "+bodyData+"|Http Error Code="+ex.getRawStatusCode()+"|Status Code="+ex.getStatusCode().toString()+"|Response Body="+ex.getResponseBodyAsString());
		}catch(ResourceAccessException ex) {
			System.out.println("Reosource Access Exception="+ ex.getMessage());
			System.out.println("Reosource Access Exception="+ ex.getCause());
		}catch(Exception ex) {
			System.out.println("Exception="+ ex.getMessage());
		}
		logger.info("response result|returnValue="+returnValue);
		return returnValue;	
	}
}
