package com.santosh.greenzone.services.impl;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.santosh.greenzone.services.OBDService;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


@Service
public class OBDServiceImpl implements OBDService{

	
	private static final Logger logger = LogManager.getLogger(OBDServiceImpl.class);
	
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
	public String getCCXMLResponse(String url,String appName ) {
		System.out.println("Make URL");
		
		System.out.println("url="+url);
		logger.info("Platform URL="+url);
		//ResponseDTO<?> response = new ResponseDTO<>();
		String sessionId="-1";
		try {
			String response = restTemplateClient.getForObject(url, String.class );
			//response = restTemplateClient.getForObject(url, ResponseDTO.class );
			if(appName != "Scheduler") {
				Document html = Jsoup.parse(response);
				String body = html.body().text();
				if(body!=null || body !="" ) {
					sessionId= body.substring(body.indexOf("=")+1);
				}
		    	logger.info("response from "+appName+"|"+response.toString()+"|body="+body+"|sessionId="+sessionId);
			}
			else {
				logger.info("response from="+response);
			}
		}catch (HttpStatusCodeException ex) {
			System.out.println("Exception Http Error Code = "+ex.getRawStatusCode());
			System.out.println("response Http Error StatusCode="+ex.getStatusCode().toString());
			System.out.println("response Http Body="+ex.getResponseBodyAsString());
			HttpHeaders headers = ex.getResponseHeaders();
			System.out.println("response Content-Type="+headers.get("Content-Type"));
			System.out.println("response Server="+headers.get("Server"));
			logger.error("Exception from "+appName+"|Http Error Code="+ex.getRawStatusCode()+"|Status Code="+ex.getStatusCode().toString()+"|Response Body="+ex.getResponseBodyAsString());
		}catch(ResourceAccessException ex) {
			System.out.println("Reosource Access Exception="+ ex.getMessage());
			System.out.println("Reosource Access Exception="+ ex.getCause());
		}catch(Exception ex) {
			System.out.println("Exception="+ ex.getMessage());
		}
		
		return sessionId;	
	}
}
