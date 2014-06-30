package com.practice;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.platform.comapi.basestruct.GeoPoint;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.app.Activity;

/*
 * 主页
 * */
public class MapMain extends Fragment {

	//BMapManager app.mBMapManager = null;
	MapView mMapView = null;
	//跳转到拍照界面的按钮
	Button photoBtn;
	//跳转到游记列表的按钮
	Button tripListBtn;
	//跳转到新建游记的按钮
	Button newTripButton;	
	View v;
	DemoMap app;

	@Override  
    public void onAttach(Activity activity) {  
        super.onAttach(activity); 
        app = (DemoMap)activity.getApplication();
		app.mBMapManager=new BMapManager(activity.getApplication());
    }  
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app.mBMapManager.init("crC3IFDwWPU7K44QphzZmWoN", null);  
		//注意：请在试用setContentView前初始化BMapManager对象，否则会报错
		LayoutInflater inflater = getActivity().getLayoutInflater();
        v = inflater.inflate(R.layout.map_main, (ViewGroup) getActivity().findViewById(R.id.container), false);
		mMapView=(MapView)v.findViewById(R.id.bmapsView);
		
		mMapView.setBuiltInZoomControls(true);
		//设置启用内置的缩放控件
		MapController mMapController=mMapView.getController();
		// 得到mMapView的控制权,可以用它控制和驱动平移和缩放
		GeoPoint point =new GeoPoint((int)(39.915* 1E6),(int)(116.404* 1E6));
		//用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)
		mMapController.setCenter(point);//设置地图中心点
		mMapController.setZoom(12);//设置地图zoom级别		
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	 {
		return v;
	 }

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stubmMapView.destroy();
        if(app.mBMapManager!=null){
            app.mBMapManager.destroy();
            app.mBMapManager=null;
        }
		super.onDestroyView();
		
	}

	@Override
	public void onPause(){
	        mMapView.onPause();
	        if(app.mBMapManager!=null){
	               app.mBMapManager.stop();
	        }
	        super.onPause();
	}
	@Override
	public void onResume(){
	        mMapView.onResume();
	        if(app.mBMapManager!=null){
	                app.mBMapManager.start();
	        }
	       super.onResume();
	}	
}