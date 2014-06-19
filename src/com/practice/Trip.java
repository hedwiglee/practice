package com.practice;

public class Trip{
	/*private int id;
	private String name;
	private String time;*/
	private String keyword;
	private String imagepath;
	
	public Trip(String keyword,String imagepath){
	//public Trip(int id,String name,String time,String keyword,String imagepath){	
		super();
		/*this.id=id;
		this.name=name;
		this.time=time;*/
		this.keyword=keyword;
		this.imagepath=imagepath;
	}
	
	/*public int getId(){
		return this.id;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getTime(){
		return this.time;
	}*/
		
	public String getKeyword(){
		return this.keyword;
	}
	
	public String getImagepath(){
		return this.imagepath;
	}
}