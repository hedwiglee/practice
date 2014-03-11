package com.practice;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.platform.comapi.basestruct.GeoPoint;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.app.Activity;
import android.content.Intent;

/*
 * 主页
 * */
public class MapMain extends Activity {

	BMapManager mBMapMan = null;
	MapView mMapView = null;
	//跳转到拍照界面的按钮
	Button photoBtn;
	//跳转到游记列表的按钮
	Button tripListBtn;
	//跳转到新建游记的按钮
	Button newTripButton;	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBMapMan=new BMapManager(getApplication());
		mBMapMan.init("crC3IFDwWPU7K44QphzZmWoN", null);  
		//注意：请在试用setContentView前初始化BMapManager对象，否则会报错
		System.out.println("111");
		setContentView(R.layout.map_main);
		System.out.println("222");
		mMapView=(MapView)findViewById(R.id.bmapsView);
		System.out.println("33");
		mMapView.setBuiltInZoomControls(true);
		//设置启用内置的缩放控件
		MapController mMapController=mMapView.getController();
		// 得到mMapView的控制权,可以用它控制和驱动平移和缩放
		GeoPoint point =new GeoPoint((int)(39.915* 1E6),(int)(116.404* 1E6));
		//用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)
		mMapController.setCenter(point);//设置地图中心点
		mMapController.setZoom(12);//设置地图zoom级别
		
		//点击拍照按钮，跳转到TakePhoto界面
		photoBtn=(Button)findViewById(R.id.take_photo_btn);
		photoBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
    			intent.setClass(MapMain.this, TakePhoto.class);
    			startActivity(intent);
			}
		});
		
		//点击游记按钮，跳转到TripList界面
		tripListBtn=(Button)findViewById(R.id.to_trip_list_btn);
		tripListBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
    			intent.setClass(MapMain.this, TripList.class);
    			startActivity(intent);
			}
		});
		
		//点击游记按钮，跳转到TripList界面
		newTripButton=(Button)findViewById(R.id.new_trip_btn);
		newTripButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
    			intent.setClass(MapMain.this, NewTrip.class);
    			startActivity(intent);
			}
		});
	}

	@Override
	protected void onDestroy(){
	        mMapView.destroy();
	        if(mBMapMan!=null){
	                mBMapMan.destroy();
	                mBMapMan=null;
	        }
	        super.onDestroy();
	}
	@Override
	protected void onPause(){
	        mMapView.onPause();
	        if(mBMapMan!=null){
	               mBMapMan.stop();
	        }
	        super.onPause();
	}
	@Override
	protected void onResume(){
	        mMapView.onResume();
	        if(mBMapMan!=null){
	                mBMapMan.start();
	        }
	       super.onResume();
	}
}
