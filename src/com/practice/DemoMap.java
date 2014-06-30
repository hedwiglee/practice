package com.practice;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;

public class DemoMap extends Application {
	
    private static DemoMap mInstance = null;
    public boolean m_bKeyRight = true;
    BMapManager mBMapManager = null;

    public static final String strKey = "crC3IFDwWPU7K44QphzZmWoN";
	
	@Override
    public void onCreate() {
	    super.onCreate();
		mInstance = this;
		initEngineManager(this);
		System.out.println("=====demo oncreate");
	}
	
	public void initEngineManager(Context context) {
        if (mBMapManager == null) {
            mBMapManager = new BMapManager(context);
        }

        if (!mBMapManager.init(strKey,new MyGeneralListener())) {
            Toast.makeText(DemoMap.getInstance().getApplicationContext(), 
                    "BMapManager  初始化错误!", Toast.LENGTH_LONG).show();
        }
		System.out.println("=====demo init");
	}
	
	public static DemoMap getInstance() {
		return mInstance;
	}
	
	
	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
    static class MyGeneralListener implements MKGeneralListener {
        
        @Override
        public void onGetNetworkState(int iError) {
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
                Toast.makeText(DemoMap.getInstance().getApplicationContext(), "您的网络出错啦！",
                    Toast.LENGTH_LONG).show();
            }
            else if (iError == MKEvent.ERROR_NETWORK_DATA) {
                Toast.makeText(DemoMap.getInstance().getApplicationContext(), "输入正确的检索条件！",
                        Toast.LENGTH_LONG).show();
            }
            // ...
        }

        @Override
        public void onGetPermissionState(int iError) {
        	//非零值表示key验证未通过
            if (iError != 0) {
                //授权Key错误：
                Toast.makeText(DemoMap.getInstance().getApplicationContext(), 
                        "请在 DemoMap.java文件输入正确的授权Key,并检查您的网络连接是否正常！error: "+iError, Toast.LENGTH_LONG).show();
                DemoMap.getInstance().m_bKeyRight = false;
            }
            else{
            	DemoMap.getInstance().m_bKeyRight = true;
            	Toast.makeText(DemoMap.getInstance().getApplicationContext(), 
                        "key认证成功", Toast.LENGTH_LONG).show();
            }
        }
    }
}