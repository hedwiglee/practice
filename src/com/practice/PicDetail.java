package com.practice;

import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.map.MyLocationOverlay.LocationMode;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

/*
 * 为所拍摄的照片添加详情
 * */
public class PicDetail extends Activity implements OnTouchListener,RecognitionListener{    
	//照片相关
	private ImageView photoview;
	SQLiteDatabase db;
	Button saveBtn;
	String photopath;//图片路径
	String picname;//图片名称
	String filepath;//文件路径
	//语音识别相关
	private static final String KWS_SEARCH = "火车";
    private static final String FORECAST_SEARCH = "forecast";
    private static final String DIGITS_SEARCH = "digits";
    private SpeechRecognizer recognizer;
    private HashMap<String, Integer> captions;
    private Button startButton;
    
    //地图相关
	private E_BUTTON_TYPE mCurBtnType;
	private enum E_BUTTON_TYPE {
		LOC,
		COMPASS,
		FOLLOW
	}
	private String loclati=null;	
	private String loclong=null;
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
	private MKSearch mSearch = null;	// 搜索模块
	private MySearchListener searchListener;
	private String locationString=null;
	//UI相关
	OnCheckedChangeListener radioButtonListener = null;
	Button requestLocButton = null;
	boolean isRequest = false;//是否手动触发请求定位
	boolean isFirstLoc = true;//是否首次定位
	
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DemoMap app = (DemoMap)this.getApplication();
        if (app.mBMapManager == null) {
            app.mBMapManager = new BMapManager(getApplicationContext());
            /**
             * 如果BMapManager没有初始化则初始化BMapManager
             */
            app.mBMapManager.init(DemoMap.strKey,new DemoMap.MyGeneralListener());
        }
		setContentView(R.layout.pic_detail);
		System.out.println("=====on create");
		//***********************显示图片****************************
		photoview = (ImageView) findViewById(R.id.thumbnail);
		Intent camIntent=this.getIntent();
		photopath=camIntent.getStringExtra("picPath");
		picname=camIntent.getStringExtra("picName");
		filepath=camIntent.getStringExtra("filePath");
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        Bitmap bm = BitmapFactory.decodeFile(photopath, options);
        
        File file=new File(filepath,picname);
		FileOutputStream outStream=null;
		try {
			outStream=new FileOutputStream(file);
			bm.compress(CompressFormat.JPEG, 100, outStream);
			outStream.close();						
		}
		catch (IOException e){
			e.printStackTrace();
		}
			
        System.out.println("========pic detail photopath:"+photopath);
        photoview.setImageBitmap(bm);
        //***********************数据库操作**********************
        File filepath=new File(Environment.getExternalStorageDirectory().toString()+"/CameraPractice/database");
		if (!filepath.exists()){
			filepath.mkdir();
		}
		db=SQLiteDatabase.openOrCreateDatabase(filepath+"/travelbook.db3", null);
		saveBtn=(Button)findViewById(R.id.save_detail);
		saveBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View source) {
				// 获取用户输入
				String description=((EditText)findViewById(R.id.photo_description)).getText().toString();
				try {
					SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd   hh:mm:ss");   
				    String date = sDateFormat.format(new java.util.Date());
				    System.out.println("==========time:"+date);
					insertData(db, description, photopath, loclati, loclong,locationString,date);
					System.out.println("======picdetail try");
					//启动新activity
	    			Intent intent = new Intent();
	    			intent.setClass(PicDetail.this, Main.class);
	    			startActivity(intent);
				}
				catch(SQLException e) {
					System.out.println("======picdetail catch");
					SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
				    String date = sDateFormat.format(new java.util.Date());					
					db.execSQL("create table pic_info(_id integer primary key autoincrement,tour_id integer," +
								"photo_time date," +
								"pic_description varchar(255),photo_keyword varchar(255),photo_loclati int," +
								"photo_loclongi int,photo_place varchar(100)," +
								"photo_path varchar(150),pic_type varchar(30))");
					insertData(db, description, photopath, loclati, loclong,locationString,date);
					//启动新activity
	    			Intent intent = new Intent();
	    			intent.setClass(PicDetail.this, PhotoList.class);
	    			startActivity(intent);
				}
			}
		});        
		
		//****************地图相关**************************
        app.mBMapManager.init("crC3IFDwWPU7K44QphzZmWoN", null); 
                
        
        //地图初始化
        mMapView = (MapView)findViewById(R.id.map_picdetail);
        mMapController = mMapView.getController();
        mMapView.getController().setZoom(14);
        mMapView.getController().enableClick(true);
        mMapView.setBuiltInZoomControls(true);

        //定位初始化
        mLocClient = new LocationClient(this.getApplicationContext());
        locData = new LocationData();
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);//打开gps
        option.setCoorType("bd09ll");     //设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
        mLocClient.requestLocation();
        
        //定位图层初始化
  		myLocationOverlay = new locationOverlay(mMapView);
  		//设置定位数据
  	    myLocationOverlay.setData(locData);
  	    //添加定位图层
  		mMapView.getOverlays().add(myLocationOverlay);
  		myLocationOverlay.enableCompass();
  		//修改定位数据后刷新图层生效
  		mMapView.refresh();
		
		//***********************语音识别部分***********************
		captions = new HashMap<String, Integer>();
        captions.put(KWS_SEARCH, R.string.psa_caption_key);
        startButton=(Button)findViewById(R.id.speak_start);
        // Recognizer initialization is a time-consuming and it involves IO,
        // so we execute it in async task	        	 
        startButton.setOnTouchListener(this);
		new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                	System.out.println("=========进入异步语音初始化任务");
                    Assets assets = new Assets(PicDetail.this);
                    File assetDir = assets.syncAssets();        
                	System.out.println("=========初始化assets");         
                    setupRecognizer(assetDir);
                } catch (IOException e) {
                	System.out.println("=========不能准备语音素材 "+e);
                    return e;
                }
                return null;
            }

            //后台进程完成后调用，原来默认的是加载完资源启动，这里试试onclick放到里面
            @Override
            protected void onPostExecute(Exception result) {
                /*if (result != null) {
                    //((TextView) findViewById(R.id.caption_text)).setText("Failed to init recognizer " + result);
                } else {
                    switchSearch(KWS_SEARCH);
                }*/
            }
        }.execute();		          
	}	

	
	@Override
	public void onDestroy() {		
		if (mLocClient != null)
            mLocClient.stop();
        mMapView.destroy();
		super.onDestroy();
		if (db!=null&&db.isOpen()) {
			db.close();
		}    
	}	
	
	//数据库模块=======================================================================

	private void insertData(SQLiteDatabase db,String description,String path,String lati,String longi,String place,String date){
		Cursor cur;
		cur=db.rawQuery("select * from trip_list order by _id desc limit 0,1", null);
		System.out.println("=========cur:"+cur);
		cur.moveToPosition(0);
		String index=cur.getString(0);//选择最后一个旅程的旅程id
		System.out.println("=========indexxxxx:"+index);
		db.execSQL("insert into pic_info values (null,"+index+",?,?,null,?,?,?,?,null)",new String[] {date,description,lati,longi,place,path});
	}
	
	//语音识别模块======================================================================
	@Override
    public void onPartialResult(Hypothesis hypothesis) {
        String text = hypothesis.getHypstr();
        ((TextView) findViewById(R.id.photo_description)).setText(text);
        System.out.println("============onPartialResult");
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        if (hypothesis != null) {
            String text = hypothesis.getHypstr();
            //makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            ((TextView) findViewById(R.id.photo_description)).setText(text);
            System.out.println("============onResult");
        }
    }

    @Override
    public void onBeginningOfSpeech() {
    }

    @Override
    public void onEndOfSpeech() {
        if (DIGITS_SEARCH.equals(recognizer.getSearchName())
                || FORECAST_SEARCH.equals(recognizer.getSearchName()))
            switchSearch(KWS_SEARCH);
    }

    private void switchSearch(String searchName) {
    	System.out.println("======start switch search");
        recognizer.stop();
        recognizer.startListening(searchName);
        getResources().getString(captions.get(searchName));
    }

    private void setupRecognizer(File assetsDir) {
    	System.out.println("=========setupRecognizer");
        File modelsDir = new File(assetsDir, "models");
        recognizer = defaultSetup()
                .setAcousticModel(new File(modelsDir, "hmm/tdt_sc_8k"))
                .setDictionary(new File(modelsDir, "lm/50_regular_words.dic"))
                .setRawLogDir(assetsDir).setKeywordThreshold(1e-5f)
                .getRecognizer();
        recognizer.addListener(this);

        File kwsFile=new File(modelsDir,"kws/50_regular_kws.txt");
        recognizer.addKeywordSearch(KWS_SEARCH, kwsFile);
    }      
    
    //长按事件
    public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			onBeginningOfSpeech();
			startButton.setText("结束识别");
			switchSearch(KWS_SEARCH);			
			System.out.println("无法开始识别");
			break;
		case MotionEvent.ACTION_UP:
			onEndOfSpeech();
			startButton.setText("开始识别");
			recognizer.stop();			
			System.out.println("无法结束识别");
			break;
		default:
			;
		}
		/* Let the button handle its own state */
		System.out.println("=========tttttttttttttttouch:"+event.getAction());
		return false;
	}
    
    //定位相关===================================================================
    /**
     * 手动触发一次定位请求
     */
    public void requestLocClick(){
    	isRequest = true;
        mLocClient.requestLocation();
        Toast.makeText(this, "正在定位……", Toast.LENGTH_SHORT).show();
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
                System.out.println("======enter if isrequest");
            	//移动地图到定位点
            	Log.d("LocationOverlay", "receive location, animate to it");
                mMapController.animateTo(new GeoPoint((int)(locData.latitude* 1e6), (int)(locData.longitude *  1e6)));
                loclati=(int)(locData.latitude* 1e6)+"";
                loclong=(int)(locData.longitude*1e6)+"";
                isRequest = false;
                myLocationOverlay.setLocationMode(LocationMode.FOLLOWING);
                changeLocFormat(new GeoPoint((int)(locData.latitude* 1e6),(int)(locData.longitude*1e6)));
				//requestLocButton.setText("跟随");
                //mCurBtnType = E_BUTTON_TYPE.FOLLOW;
            }
            //首次定位完成
            isFirstLoc = false;
        }
        
        public void onReceivePoi(BDLocation poiLocation) {
            if (poiLocation == null){
                return ;
            }
            System.out.println("=========onreceive poi");
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
					new GeoPoint((int)(locData.latitude*1e6), (int)(locData.longitude*1e6)),8);
  			return true;
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
     
     //将坐标转换为位置===========================================================
     /*转换地理位置相关 * 
 	 */
 	private void changeLocFormat(GeoPoint point){
 		mSearch = new MKSearch();
 		searchListener=new MySearchListener();
 		DemoMap app = (DemoMap)this.getApplication();
 		mSearch.init(app.mBMapManager, new MySearchListener());
 		mSearch.reverseGeocode(point);	
 	}
 	
 	public class MySearchListener implements MKSearchListener {

        // MKAddrInfo 地址信息类
        @Override
        public void onGetAddrResult(MKAddrInfo res, int error) {
			System.out.println("************getresult:");
            if (error != 0) {
                String msg = String.format("错误号：%d ", error);
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT)
                        .show();
                return;
            }            
            if (res.type == MKAddrInfo.MK_REVERSEGEOCODE){
				//反地理编码：通过坐标点检索详细地址及周边poi
				String strInfo = res.strAddr;
				locationString=strInfo;		
				System.out.println("************locationstring:"+locationString);
			}
        }

        @Override
        public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
        }

        @Override
        public void onGetDrivingRouteResult(MKDrivingRouteResult result,
                int arg1) {
        }

        @Override
        public void onGetPoiDetailSearchResult(int arg0, int arg1) {

        }

        @Override
        public void onGetPoiResult(MKPoiResult result, int type, int error) {
        }

        public void onGetRGCShareUrlResult(String arg0, int arg1) {
        }

        @Override
        public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
        }

        @Override
        public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {
        }

        @Override
        public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {
        }

		@Override
		public void onGetShareUrlResult(MKShareUrlResult arg0, int arg1,
				int arg2) {
			// TODO Auto-generated method stub
			
		}

    }
 	

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		System.out.println("=======loc onstart");
	}
	
    @Override
	public void onPause() {
        mMapView.onPause();
        super.onPause();
        System.out.println("======onPause");
    }
    
    @Override
	public void onResume() {
       mMapView.onResume();
       super.onResume();
       System.out.println("======onResume");
    }
        
    @Override
	public void onSaveInstanceState(Bundle outState) {
        System.out.println("======enter instance");
    	super.onSaveInstanceState(outState);
    	mMapView.onSaveInstanceState(outState);
        System.out.println("======onsave instance");    	
    }
}