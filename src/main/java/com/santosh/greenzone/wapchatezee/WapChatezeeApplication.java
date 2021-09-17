package com.santosh.greenzone.wapchatezee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@SpringBootApplication



//  @PropertySources({
//  @PropertySource("file:///${CHAT_CONFIG_PATH}//application.properties"),
//  @PropertySource("file:///${CHAT_CONFIG_PATH}//chatezee.properties"),
//  @PropertySource("file:///${CHAT_CONFIG_PATH}//dbquery.properties")})
 
@PropertySources({
	@PropertySource("classpath:/application.properties"),
	@PropertySource("classpath:/chatezee.properties"),
	@PropertySource("classpath:/dbquery.properties")})

public class WapChatezeeApplication {

	public static void main(String[] args) {
		SpringApplication.run(WapChatezeeApplication.class, args);
		String strClassPath = System.getProperty("java.class.path");
		System.out.println("Classpath is =" + strClassPath);
	}

}
