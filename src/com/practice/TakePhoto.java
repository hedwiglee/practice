package com.practice;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/*
 * 拍照界面
 * */
public class TakePhoto extends Activity implements SensorEventListener{

	SurfaceView sView;
	SurfaceHolder sHolder;
	int screenWidth,screenHeight;
	//系统使用的相机
	Camera camera;
	//是否在预览中
	boolean isPreview=false;
	int mOrientation=0;
	SensorManager mSensorManager;	
	SensorEventListener mSensorEventListener;
	//代表手机分别朝向上、右、下、左
	int ORIENTATION_UP=1;
	int ORIENTATION_RIGHT=2;
	int ORIENTATION_DOWN=3;
	int ORIENTATION_LEFT=4;
	//四个方向放置手机时，拍出图片需要旋转的角度
	int ROTATE_UP=90;
	int ROTATE_RIGHT=0;
	int ROTATE_DOWN=270;
	int ROTATE_LEFT=180;
	//手机方向
	int mobileOrientation=0;
	//手机旋转角度
	int mobileRotate=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
		//设置全屏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.take_photo);		

		new cameraThread().start();
	}
	
	public class cameraThread extends Thread {
		public void run() {
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
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub		
	}	
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		float[] values=event.values;
		if (Math.abs(values[1])>=45&&Math.abs(values[1])<=135) {
			if (values[1]<0) {
				mobileOrientation=ORIENTATION_UP;
			}
			else {
				mobileOrientation=ORIENTATION_DOWN;
			}
		}
		else {
			if (values[2]<0) {
				mobileOrientation=ORIENTATION_LEFT;
			}
			else {
				mobileOrientation=ORIENTATION_RIGHT;
			}
		}
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
				System.out.println(screenWidth+" "+screenHeight);
				//设置每秒显示帧数的最大和最小值
				parameters.setPreviewFpsRange(4, 10);
				parameters.setPictureFormat(ImageFormat.JPEG);
				parameters.set("jpeg-quality", 85);
				//设置预览照片大小
				parameters.setPreviewSize(960, 720);	
				parameters.setPictureSize(1280, 800);
				/*int[] size=new int[2];
				size=getSizes(parameters);			
				parameters.setPictureSize(size[0], size[1]);*/
				getSizes(parameters);
				getpreSizes(parameters);
				//camera.setParameters(parameters);
				
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
	
	public static Bitmap rotate(Bitmap b, int degrees) {
		if(degrees==0){
			return b;
		}
		if (degrees != 0 && b != null) {
			Matrix m = new Matrix();
			m.setRotate(degrees, (float) b.getWidth() ,
			(float) b.getHeight() );
			try {
				Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
				b.getHeight(), m, true);
				if (b != b2) {
				b.recycle(); // Bitmap操作完应该显示的释放
				b = b2;
				}
			} 
			catch (OutOfMemoryError ex) {
			// 出现了内存不足异常，最好return 原始的bitmap对象
			}
		}
		//b=compressImage(b);
		return b;
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
			// 拍照时
			//根据所拍数据创建位图
			final Bitmap bm=BitmapFactory.decodeByteArray(data, 0, data.length);
			switch (mobileOrientation) {
			case 1:
				mobileRotate=ROTATE_UP;
				break;
			case 2:
				mobileRotate=ROTATE_RIGHT;
				break;
			case 3:
				mobileRotate=ROTATE_DOWN;
				break;
			case 4:
				mobileRotate=ROTATE_LEFT;
				break;
			default:
				break;
			}
			final Bitmap bmRotate=rotate(bm, mobileRotate);
			View saveDialog=getLayoutInflater().inflate(R.layout.save, null);
			//用时间为图片命名
			final String picname = DateFormat.format("yyyyMMdd_hhmmss",Calendar.getInstance(Locale.CHINA)) + ".jpg";
			new AlertDialog.Builder(TakePhoto.this).setView(saveDialog)
					.setPositiveButton("保存", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					String path=Environment.getExternalStorageDirectory().toString();
					File filepath=new File(path+"/CameraPractice");
					if (!filepath.exists()){
						filepath.mkdir();
					}
					File file=new File(filepath,picname);
					FileOutputStream outStream=null;
					try {
						outStream=new FileOutputStream(file);
						bmRotate.compress(CompressFormat.JPEG, 100, outStream);
						outStream.close();						
						//启动新activity
		    			Intent intent = new Intent();
		    			intent.setClass(TakePhoto.this, PicDetail.class);
		    	        Bundle bundle = new Bundle();
		    	        bundle.putString("picPath", file.getPath());
		    	        bundle.putString("picName", picname);
		    	        bundle.putString("filePath", path+"/CameraPractice");
		    	        intent.putExtra("picPath",file.getPath());
		    	        intent.putExtra("picName",picname);
		    	        intent.putExtra("filePath",path+"/CameraPractice");
		    			startActivity(intent);
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

	@Override
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, 
				mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	@Override
	protected void onPause() {
		mSensorManager.unregisterListener(this);
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		mSensorManager.unregisterListener(this);
		super.onStop();
	}
	
	public static Bitmap decodeBitmap(Bitmap bitmap, int compareSize) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		float realWidth = options.outWidth;
		float realHeight = options.outHeight;
		// 计算缩放比
		int scale = (int) ((realHeight > realWidth ? realHeight : realWidth) / compareSize);
		if (scale <= 0) {
			scale = 1;
		}
		options.inSampleSize = scale;
		options.inJustDecodeBounds = false;
		// 注意这次要把options.inJustDecodeBounds 设为 false,这次图片是要读取出来的。
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		System.out.println("缩略图高度：" + h + "宽度:" + w);
		return bitmap;
	}
	
	private static Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while ( baos.toByteArray().length / 1024>300) {	//循环判断如果压缩后图片是否大于100kb,大于继续压缩		
			baos.reset();//重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;//每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
		return bitmap;
	}
	
	//获取所有的图片支持尺寸
	private static void getSizes(Camera.Parameters parameters){
		int[] out=new int[2];
		List psizelist = parameters.getSupportedPictureSizes();
        if (null != psizelist && 0 < psizelist.size()) {
                int heights[] = new int[psizelist.size()];
                Map<Integer, Integer> map = new HashMap<Integer, Integer>();
                for (int i = 0; i < psizelist.size(); i++) {
                        Size size = (Size) psizelist.get(i);
                        int sizehieght = size.height;
                        int sizewidth = size.width;
                        heights[i] = sizehieght;
                        map.put(sizehieght, sizewidth);
                        System.out.println("@@支持图片尺寸 宽"+sizewidth+" 高 "+sizehieght);
                }
                Arrays.sort(heights);// 取最小尺寸
                parameters.setPictureSize(heights[0], map.get(heights[0]));
                out[0]=heights[0];
                out[1]=map.get(heights[0]);
                System.out.println("@@最小尺寸 宽"+out[0]+" 高 "+out[1]);
        }
        //return out;
	}
	
	//获取所有的预览尺寸
		private static void getpreSizes(Camera.Parameters parameters){
			int[] out=new int[2];
			List psizelist = parameters.getSupportedPreviewSizes();
	        if (null != psizelist && 0 < psizelist.size()) {
	                int heights[] = new int[psizelist.size()];
	                Map<Integer, Integer> map = new HashMap<Integer, Integer>();
	                for (int i = 0; i < psizelist.size(); i++) {
	                        Size size = (Size) psizelist.get(i);
	                        int sizehieght = size.height;
	                        int sizewidth = size.width;
	                        heights[i] = sizehieght;
	                        map.put(sizehieght, sizewidth);
	                        System.out.println("@@支持预览尺寸 宽"+sizewidth+" 高 "+sizehieght);
	                }
	                Arrays.sort(heights);// 取最小尺寸
	                parameters.setPictureSize(heights[0], map.get(heights[0]));
	                out[0]=heights[0];
	                out[1]=map.get(heights[0]);
	                System.out.println("@@最小尺寸 宽"+out[0]+" 高 "+out[1]);
	        }
	        //return out;
		}
}