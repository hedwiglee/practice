package com.practice;

import com.baidu.mapapi.search.MKSearch;
import com.baidu.platform.comapi.basestruct.GeoPoint;  
import java.io.File;
  
/** 
 * @描述 在Fragment中要使用ListView，就要用ListFragment 
 * */  
public class Test {  
	private MKSearch mSearch = null;
	private int changeLocFormat(GeoPoint point){
 		mSearch = new MKSearch();
 		return mSearch.reverseGeocode(point); 		
 	}
	
	private void main(){
		GeoPoint geoPoint=new GeoPoint(39000000, 116000000);
		
		System.out.println(changeLocFormat(geoPoint));
	}
}  