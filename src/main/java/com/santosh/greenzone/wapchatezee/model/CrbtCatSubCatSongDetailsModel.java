package com.santosh.greenzone.wapchatezee.model;

import com.fasterxml.jackson.annotation.JsonInclude;


public class CrbtCatSubCatSongDetailsModel {
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String songname;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String cpcategory1;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String songid;
	
	
	public String getSongname() {
		return songname;
	}
	public void setSongname(String songname) {
		this.songname = songname;
	}
	public String getCpcategory1() {
		return cpcategory1;
	}
	public void setCpcategory1(String cpcategory1) {
		this.cpcategory1 = cpcategory1;
	}
	public String getSongid() {
		return songid;
	}
	public void setSongid(String songid) {
		this.songid = songid;
	}
	
	
}
