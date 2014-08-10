package com.practice;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.Ground;
import com.baidu.mapapi.map.GroundOverlay;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.MyLocationOverlay.LocationMode;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
/**
 * 此demo用来展示如何结合定位SDK实现定位，并使用MyLocationOverlay绘制定位位置
 * 同时展示如何使用自定义图标绘制并点击时弹出泡泡
 *
 */
public class LocationOverlayDemo extends Fragment {
	View v;
	DemoMap app;
	private enum E_BUTTON_TYPE {
		LOC,
		COMPASS,
		FOLLOW
	}
	
	private E_BUTTON_TYPE mCurBtnType;

	//覆盖物相关
	double mLon1 = 116.364921 ;
	double mLat1 = 39.967079 ;
	private MyOverlay mOverlay = null;
	private OverlayItem mCurItem = null;
	private ArrayList<OverlayItem>  mItems = null; 
	private View viewCache = null;
	private View popupInfo = null;
	private View popupLeft = null;
	private View popupRight = null;
	private Button button = null;
	private MapView.LayoutParams layoutParam = null;
	// 定位相关
	LocationClient mLocClient;
	LocationData locData = null;
	public MyLocationListenner myListener = new MyLocationListenner();
	
	//定位图层
	locationOverlay myLocationOverlay = null;
	//弹出泡泡图层
	private PopupOverlay   pop  = null;//弹出泡泡图层，浏览节点时使用
	private TextView  popupText = null;//泡泡view
	
	//地图相关，使用继承MapView的MapView目的是重写touch事件实现泡泡处理
	//如果不处理touch事件，则无需继承，直接使用MapView即可
	MapView mMapView = null;	// 地图View
	private MapController mMapController = null;

	//UI相关
	OnCheckedChangeListener radioButtonListener = null;
	Button requestLocButton = null;
	boolean isRequest = false;//是否手动触发请求定位
	boolean isFirstLoc = true;//是否首次定位
	
	//数据库相关
	private Cursor cursor;
	private SQLiteDatabase db;
	private ArrayList<Double> latiArray;
	private ArrayList<Double> longiArray;
	private double loclati;
	private double loclongi;
	
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
        /*CharSequence titleLable="定位功能";
        /**
         * 使用地图sdk前需先初始化BMapManager.
         * BMapManager是全局的，可为多个MapView共用，它需要地图模块创建前创建，
         * 并在地图地图模块销毁后销毁，只要还有地图模块在使用，BMapManager就不应该销毁
         */
        LayoutInflater inflater = getActivity().getLayoutInflater();
        v = inflater.inflate(R.layout.activity_locationoverlay, (ViewGroup) getActivity().findViewById(R.id.container), false);
        /*CharSequence titleLable="定位功能";
        setTitle(titleLable);*/
        requestLocButton = (Button)v.findViewById(R.id.button1);
        mCurBtnType = E_BUTTON_TYPE.LOC;
        OnClickListener btnClickListener = new OnClickListener() {
        	public void onClick(View v) {
				switch (mCurBtnType) {
				case LOC:
					//手动定位请求
					requestLocClick();
					break;
				case COMPASS:
					myLocationOverlay.setLocationMode(LocationMode.NORMAL);
					requestLocButton.setText("定位");
					mCurBtnType = E_BUTTON_TYPE.LOC;
					break;
				case FOLLOW:
					myLocationOverlay.setLocationMode(LocationMode.COMPASS);
					requestLocButton.setText("罗盘");
					mCurBtnType = E_BUTTON_TYPE.COMPASS;
					break;
				}
			}
		};
	    requestLocButton.setOnClickListener(btnClickListener);
	    
        RadioGroup group = (RadioGroup)this.v.findViewById(R.id.radioGroup);
        radioButtonListener = new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.defaulticon){
					//传入null则，恢复默认图标
					modifyLocationOverlayIcon(null);
				}
				if (checkedId == R.id.customicon){
					//修改为自定义marker
					modifyLocationOverlayIcon(getResources().getDrawable(R.drawable.icon_geo));
				}
			}
		};
		group.setOnCheckedChangeListener(radioButtonListener);
		
        
        //定位初始化
        mLocClient = new LocationClient(getActivity().getApplicationContext());
        locData = new LocationData();
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);//打开gps
        option.setCoorType("bd09ll");     //设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
        mLocClient.requestLocation();	
        
        try {
			cursor=db.rawQuery("select * from pic_info", null);
		}
		catch (Exception e) {
			db.execSQL("create table pic_info(_id integer primary key autoincrement,integer tour_id," +
					"photo_time date," +
					"pic_description varchar(255),photo_keyword varchar(255),photo_loclati int," +
					"photo_loclongi int,photo_place varchar(100)," +
					"photo_path varchar(150))");
			cursor=db.rawQuery("select * from pic_info", null);
		}
        
        mOverlay = new MyOverlay(getResources().getDrawable(R.drawable.icon_marka),mMapView);	
        while(cursor.moveToNext()){
        	//循环读取数据库中地址，显示关键词在地图上
        	if (cursor.getString(5)!=null&&cursor.getString(6)!=null){
	        	/*latiArray.add(Double.parseDouble(cursor.getString(5)));
	        	longiArray.add(Double.parseDouble(cursor.getString(6)));*/
        		loclati=Double.parseDouble(cursor.getString(5));
        		loclongi=Double.parseDouble(cursor.getString(6));
            	GeoPoint p1 = new GeoPoint ((int)(loclati*1E6),(int)(loclongi*1E6));
                OverlayItem item1 = new OverlayItem(p1,"覆盖物1","");
                item1.setMarker(getResources().getDrawable(R.drawable.icon_marka));        		
        	}
        }
    }
    
	/**
     * 手动触发一次定位请求
     */
    public void requestLocClick(){
    	isRequest = true;
        mLocClient.requestLocation();
        Toast.makeText(getActivity(), "正在定位……", Toast.LENGTH_SHORT).show();
    }
   
	/**
     * 修改位置图标
     * @param marker
     */
    public void modifyLocationOverlayIcon(Drawable marker){
    	//当传入marker为null时，使用默认图标绘制
    	myLocationOverlay.setMarker(marker);
    	//修改图层，需要刷新MapView生效
    	mMapView.refresh();
    }
	/**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {
    	
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return ;
            
            locData.latitude = location.getLatitude();
            locData.longitude = location.getLongitude();
            //如果不显示定位精度圈，将accuracy赋值为0即可
            locData.accuracy = location.getRadius();
            // 此处可以设置 locData的方向信息, 如果定位 SDK 未返回方向信息，用户可以自己实现罗盘功能添加方向信息。
            //locData.direction = location.getDerect();
            //更新定位数据
            myLocationOverlay.setData(locData);
            //更新图层数据执行刷新后生效
            mMapView.refresh();
            //是手动触发请求或首次定位时，移动到定位点
            if (isRequest || isFirstLoc){
            	//移动地图到定位点
            	Log.d("LocationOverlay", "receive location, animate to it");
                mMapController.animateTo(new GeoPoint((int)(locData.latitude* 1e6), (int)(locData.longitude *  1e6)));
                isRequest = false;
                myLocationOverlay.setLocationMode(LocationMode.FOLLOWING);
				requestLocButton.setText("跟随");
                mCurBtnType = E_BUTTON_TYPE.FOLLOW;
            }
            //首次定位完成
            isFirstLoc = false;
        }
        
        public void onReceivePoi(BDLocation poiLocation) {
            if (poiLocation == null){
                return ;
            }
        }
    }
    
    //继承MyLocationOverlay重写dispatchTap实现点击处理
  	public class locationOverlay extends MyLocationOverlay{
 
  		public locationOverlay(MapView mapView) {
  			super(mapView);
  			// TODO Auto-generated constructor stub
  		}
  		@Override
  		protected boolean dispatchTap() {
  			// TODO Auto-generated method stub
  			//处理点击事件,弹出泡泡
  			popupText.setBackgroundResource(R.drawable.popup);
			popupText.setText("我的位置");
			pop.showPopup(BMapUtil.getBitmapFromView(popupText),
					new GeoPoint((int)(locData.latitude*1e6), (int)(locData.longitude*1e6)),
					8);
  			return true;
  		}
  		
  	}

  	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	 {
  		//地图初始化
        mMapView = (MapView)v.findViewById(R.id.bmapView);
        mMapController = mMapView.getController();
        mMapView.getController().setZoom(14);
        mMapView.getController().enableClick(true);
        mMapView.setBuiltInZoomControls(true);
        //创建 弹出泡泡图层
        //createPaopao();
      //定位图层初始化
  		myLocationOverlay = new locationOverlay(mMapView);
  		//设置定位数据
  	    myLocationOverlay.setData(locData);
  	    //添加定位图层
  		mMapView.getOverlays().add(myLocationOverlay);
  		myLocationOverlay.enableCompass();
  		//修改定位数据后刷新图层生效
  		mMapView.refresh();
        initOverlay();
		return v;
	 }
  	
  	 @Override
     public void setUserVisibleHint(boolean isVisibleToUser) {        //核心方法，避免因Fragment跳转导致地图崩溃
         super.setUserVisibleHint(isVisibleToUser);
         if (isVisibleToUser == true) {
             // if this view is visible to user, start to request user location
             startRequestLocation();
         } else if (isVisibleToUser == false) {
             // if this view is not visible to user, stop to request user
             // location
             stopRequestLocation();
         }
     }

     private void stopRequestLocation() {
         if (mLocClient != null) {
             mLocClient.unRegisterLocationListener(myListener);
             mLocClient.stop();
         }
     }

     private void startRequestLocation() {
         // this nullpoint check is necessary
         if (mLocClient != null) {
             mLocClient.registerLocationListener(myListener);
             mLocClient.start();
             mLocClient.requestLocation();
         }
     }
  	
     //创建覆盖物
     public void initOverlay(){
    	 System.out.println("=========enter init overlay");
     	/**
     	 * 创建自定义overlay
     	 */
         mOverlay = new MyOverlay(getActivity().getResources().getDrawable(R.drawable.icon_marka),mMapView);	
     	 System.out.println("=========create overlay");
          /**
           * 准备overlay 数据
           */
          GeoPoint p1 = new GeoPoint ((int)(mLat1*1E6),(int)(mLon1*1E6));
          OverlayItem item1 = new OverlayItem(p1,"覆盖物1","");
          /**
           * 设置overlay图标，如不设置，则使用创建ItemizedOverlay时的默认图标.
           */
          item1.setMarker(getResources().getDrawable(R.drawable.icon_marka));
     	 System.out.println("=========set marker");
          /**
           * 将item 添加到overlay中
           * 注意： 同一个itme只能add一次
           */
          mOverlay.addItem(item1);
          /**
           * 保存所有item，以便overlay在reset后重新添加
           */
          mItems = new ArrayList<OverlayItem>();
          mItems.addAll(mOverlay.getAllItem());

          /**
           * 将overlay 添加至MapView中
           */
          mMapView.getOverlays().add(mOverlay);
     	 System.out.println("=========add overlay");
          /**
           * 刷新地图
           */
          mMapView.refresh();
          /**
           * 向地图添加自定义View.
           */
          //viewCache = getActivity().getLayoutInflater().inflate(R.layout.custom_text_view, null);
          /*popupInfo = (View) viewCache.findViewById(R.id.popinfo);
          popupLeft = (View) viewCache.findViewById(R.id.popleft);
          popupRight = (View) viewCache.findViewById(R.id.popright);
          popupText =(TextView) viewCache.findViewById(R.id.textcache);*/
          
          /*button = new Button(this);
          button.setBackgroundResource(R.drawable.popup);*/
          
          /**
           * 创建一个popupoverlay
           */
          /*PopupClickListener popListener = new PopupClickListener(){
 			@Override
 			public void onClickedPopup(int index) {
 				if ( index == 0){
 					//更新item位置
 				      pop.hidePop();
 				      GeoPoint p = new GeoPoint(mCurItem.getPoint().getLatitudeE6()+5000,
 				    		  mCurItem.getPoint().getLongitudeE6()+5000);
 				      mCurItem.setGeoPoint(p);
 				      mOverlay.updateItem(mCurItem);
 				      mMapView.refresh();
 				}
 				else if(index == 2){
 					//更新图标
 					mCurItem.setMarker(getResources().getDrawable(R.drawable.nav_turn_via_1));
 					mOverlay.updateItem(mCurItem);
 				    mMapView.refresh();
 				}
 			}
          };
          */
          //pop = new PopupOverlay(mMapView,popListener);        
          System.out.println("========end initoverlay");
     }
     
     public class MyOverlay extends ItemizedOverlay{

 		public MyOverlay(Drawable defaultMarker, MapView mapView) {
 			super(defaultMarker, mapView);
 		}		

 		@Override
 		public boolean onTap(int index){
 			OverlayItem item = getItem(index);
 			mCurItem = item ;
 			if (index == 3){
 				button.setText("这是一个系统控件");
 				/*GeoPoint pt = new GeoPoint((int) (mLat4 * 1E6),
 						(int) (mLon4 * 1E6));*/
 				// 弹出自定义View
 				//pop.showPopup(button, pt, 32);
 			}
 			else{
 			   popupText.setText(getItem(index).getTitle());
 			   Bitmap[] bitMaps={
 				    BMapUtil.getBitmapFromView(popupLeft), 		
 				    BMapUtil.getBitmapFromView(popupInfo), 		
 				    BMapUtil.getBitmapFromView(popupRight) 		
 			    };
 			    pop.showPopup(bitMaps,item.getPoint(),32);
 			}
 			return true;
 		}
 		
 		@Override
 		public boolean onTap(GeoPoint pt , MapView mMapView){
 			if (pop != null){
                 pop.hidePop();
                 mMapView.removeView(button);
 			}
 			return false;
 		}
     	
     }
     
  	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	
    @Override
	public void onPause() {
        mMapView.onPause();
        if(app.mBMapManager!=null){
               app.mBMapManager.stop();
        }
        super.onPause();
    }
    
    @Override
	public void onResume() {
        /*mMapView.onResume();
        super.onResume();
        System.out.println("======onResume");*/
        
        mMapView.onResume();
        if(app.mBMapManager!=null){
                app.mBMapManager.start();
        }
       super.onResume();
    }
    
    @Override
	public void onDestroy() {
    	//退出时销毁定位
        /*if (mLocClient != null)
            mLocClient.stop();*/
        if(app.mBMapManager!=null){
            app.mBMapManager.destroy();
            app.mBMapManager=null;
        }
        mMapView.destroy();        
		super.onDestroy();        
    }

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}
    
    @Override
	public void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	mMapView.onSaveInstanceState(outState);    	
    }  
}