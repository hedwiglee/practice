package com.practice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.security.auth.callback.Callback;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Bitmap.CompressFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
//import android.graphics.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

public class Main extends Activity {

	SurfaceView sView;
	SurfaceHolder sHolder;
	int screenWidth,screenHeight;
	//系统使用的相机
	Camera camera;
	//是否在预览中
	boolean isPreview=false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//设置全屏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.main);
		//获取窗口管理器
		WindowManager vm=getWindowManager();
		Display display=vm.getDefaultDisplay();
		DisplayMetrics metrics=new DisplayMetrics();
		//获取屏幕宽高
		display.getMetrics(metrics);
		screenWidth=metrics.widthPixels;
		screenHeight=metrics.heightPixels;
		//获取surface组件
		sView=(SurfaceView)findViewById(R.id.sView);
		//surface无需自己维护缓冲区
		sView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		//获取surfaceview的surfaceholder
		sHolder=sView.getHolder();
		System.out.println("get sHolder");
		//为sHolder添加一个回调监听器
		sHolder.addCallback(new SurfaceHolder.Callback()
		{
			@Override
			public void surfaceChanged(SurfaceHolder holder,int format,int width,int height){
				System.out.println("surface changed");
			}
			@Override
			public void surfaceCreated(SurfaceHolder surfaceholder){
				System.out.println("surface created");
				initCamera();
				System.out.println("init camera!");
			}
			@Override
			public void surfaceDestroyed(SurfaceHolder surfaceholder){
				//如果camera不为null，释放摄像头
				if (camera!=null)
				{
					if (isPreview) 
						camera.stopPreview();
					camera.release();
					camera=null;					
				}
			}
		});
	}
	
	private void initCamera(){
		System.out.println("enter initCamera()");
		if (!isPreview){
			System.out.println("init camera 1st if");
			//默认打开后置摄像头，传参可打开前置摄像头
			try {
				camera=Camera.open(0);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			System.out.println("opened the camera");
			camera.setDisplayOrientation(90);
			System.out.println("打开摄像头");
		}
		if (camera!=null&&!isPreview){
			System.out.println("init camera 2nd if");
			try {
				Camera.Parameters parameters=camera.getParameters();
				//设置预览照片大小
				parameters.setPreviewSize(screenWidth, screenHeight);
				System.out.println(screenWidth+" "+screenHeight);
				//设置每秒显示帧数的最大和最小值
				parameters.setPreviewFpsRange(4, 10);
				parameters.setPictureFormat(ImageFormat.JPEG);
				parameters.set("jpeg-quality", 85);
				parameters.setPictureSize(screenWidth, screenHeight);		
								
				//通过surfaceview显示取景画面
				camera.setPreviewDisplay(sHolder);
				camera.startPreview();
			}
			catch (Exception e){
				e.printStackTrace();
			}
			isPreview=true;
		}
	}

	public void capture(View source){
		System.out.println("enter capture()");
		if (camera!=null){
			camera.autoFocus(autoFocusCallback);
			System.out.println("autofocus");
		}
	}
	
	AutoFocusCallback autoFocusCallback=new AutoFocusCallback() {
		
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			// TODO Auto-generated method stub
			if (success){
				camera.takePicture(new ShutterCallback() {
					
					@Override
					public void onShutter() {
						// TODO Auto-generated method stub
						
					}
				}, new PictureCallback(){
					public void onPictureTaken(byte[] data,Camera c) {
						
					}
				}, myJpegCallback);
			}
		}
	};
	
	PictureCallback myJpegCallback=new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			//根据所拍数据创建位图
			final Bitmap bm=BitmapFactory.decodeByteArray(data, 0, data.length);
			View saveDialog=getLayoutInflater().inflate(R.layout.save, null);
			final EditText photoname=(EditText)saveDialog.findViewById(R.id.phone_name);
			ImageView show=(ImageView)saveDialog.findViewById(R.id.show);
			show.setImageBitmap(bm);
			new AlertDialog.Builder(Main.this).setView(saveDialog)
					.setPositiveButton("保存", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					File file=new File(Environment.getExternalStorageDirectory(),photoname.getText().toString()+".jpg");
					FileOutputStream outStream=null;
					try {
						outStream=new FileOutputStream(file);
						bm.compress(CompressFormat.JPEG, 100, outStream);
						outStream.close();
					}
					catch (IOException e){
						e.printStackTrace();
					}
				}
			}).setNegativeButton("取消",null).show();
			camera.stopPreview();
			camera.startPreview();
			isPreview=true;
		}
	};
}
