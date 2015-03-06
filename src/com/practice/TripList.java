package com.practice;

import java.io.File;
import android.app.Activity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ListView;

/*
 * 测试页，从数据库中读取照片路径并显示
 * */
public class TripList extends Fragment {
	SQLiteDatabase db;
	ListView list;
	TextView tripnameText;
	DatabaseFunc dbaseFunc;
	Cursor cursor;
	static public String tripName;
	private View v;
	int PHOTO=1;
	String choosepic=null;	//在本地浏览图片时所选图片的路径
	String choosePicId;//长按时点击的item的id
    
    @Override  
    public void onAttach(Activity activity) {  
        super.onAttach(activity);
    }  
	
	public void onCreate(Bundle savedInstanceState)
	{		
        super.onCreate(savedInstanceState);  
        LayoutInflater inflater = getActivity().getLayoutInflater();
        v = inflater.inflate(R.layout.trip_list, (ViewGroup) getActivity().findViewById(R.id.container), false);
		list=(ListView)v.findViewById(R.id.trip_list_view);
		
		File filepath=new File(Environment.getExternalStorageDirectory().toString()+"/CameraPractice/database");
		if (!filepath.exists()){
			filepath.mkdir();
		}
		db=SQLiteDatabase.openOrCreateDatabase(filepath+"/travelbook.db3", null);
		
		try {
			cursor=db.rawQuery("select * from trip_list order by _id desc", null);			
		}
		catch (Exception e) {
			db.execSQL("create table trip_list(_id integer primary key autoincrement,trip_name varchar(40)," +
					"start_time date,end_time date," +
					"user_id varchar(20),participate varchar(100),thumbnail_photo varchar(100)," +
					"keyword varchar(255),photo_nums integer,trip_location varchar(100),is_over integer)");
			cursor=db.rawQuery("select * from trip_list order by _id desc", null);
		}
        
        ListBaseAdapter tripBaseAdapter=new ListBaseAdapter(getActivity(), list, cursor);
		list.setAdapter(tripBaseAdapter);
		
		//ListView的点击事件
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id){
				Intent intent = new Intent();
    			intent.setClass(getActivity(), TripShow.class);
    			Bundle bundle = new Bundle();
    			//tripnameText=(TextView)findViewById(R.id.triplist_id_hidden);
    			//list=(ListView)v.findViewById(R.id.trip_list_view);
    			tripnameText=(TextView)list.getChildAt(position-list.getFirstVisiblePosition()).findViewById(R.id.triplist_id_hidden);
    			tripName=tripnameText.getText().toString();//旅程的ID值
    			System.out.println("=======tripname:"+tripName);
    	        bundle.putString("tripName", tripName);
    	        intent.putExtra("tripName",tripName);
    	        tripName=null;
    			startActivity(intent);
			}
		});

		//列表长按修改旅程缩略图事件
		list.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,final int pos, long id) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(getActivity()).setMessage("编辑旅程封面").setNegativeButton("取消", null).
				setPositiveButton("确定", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						TextView idTextView=(TextView)list.getChildAt(pos-list.getFirstVisiblePosition()).findViewById(R.id.triplist_id_hidden);
		    			choosePicId=idTextView.getText().toString();//旅程的ID值
					 	Intent intent = new Intent();
				        /* 开启Pictures画面Type设定为image */
				        intent.setType("image/*");
				        /* 使用Intent.ACTION_GET_CONTENT这个Action */
				        intent.setAction(Intent.ACTION_GET_CONTENT); 
				        /* 取得相片后返回本画面 */
				        startActivityForResult(intent, PHOTO);						
					}		        	
		        }).show();
				return false;
			}
		});
	}
    
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	 {
		System.out.println("triplist oncreateview");
		return v;
	 }
	
	//将cursor的结果转换为map数据
	private List<? extends Map<String, ?>> getData(Cursor result) {  
        List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        while (result.moveToNext()) {
          HashMap<String, String> map = new HashMap<String, String>();
          map.put("_id", result.getString(result.getColumnIndex("_id")));
          map.put("trip_name", result.getString(result.getColumnIndex("trip_name")));
          map.put("start_time", result.getString(result.getColumnIndex("start_time")));
          map.put("end_time", result.getString(result.getColumnIndex("end_time")));
          map.put("keyword", result.getString(result.getColumnIndex("keyword")));
          map.put("thumbnail_photo", result.getString(result.getColumnIndex("thumbnail_photo")));
          list.add(map);
        }      
        return list;  
    }    
	
	private String getRealPath(Uri fileUrl){
    	String fileName = null;
    	Uri filePathUri = fileUrl;
    	if(fileUrl!= null)
    	{
    		if (fileUrl.getScheme().toString().compareTo("content")==0) //content://开头的uri
    		{
    			Cursor cursor = getActivity().getContentResolver().query(fileUrl, null, null, null, null);
    			if (cursor != null && cursor.moveToFirst())
    			{
    				int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
    				fileName = cursor.getString(column_index); //取出文件路径
    				if(!fileName.startsWith("/mnt")){
    					//检查是否有”/mnt“前缀

    					fileName = "/mnt" + fileName; 
    				}
    				cursor.close();
    			}
    		}
    		else if (fileUrl.getScheme().compareTo("file")==0) //file:///开头的uri
    		{
    			fileName = filePathUri.toString();
    			fileName = filePathUri.toString().replace("file://", "");
    			//替换file://
    			if(!fileName.startsWith("/mnt")){
    				//加上"/mnt"头
    				fileName += "/mnt"; 
    			}
    		}
    	}
    	return fileName;
    }
	
    @Override  
    public void onActivityCreated(Bundle savedInstanceState) {  
    	System.out.println("enter onactivity");
        super.onActivityCreated(savedInstanceState);    
        System.out.println("on activity created");
    }  
    
    //对修改封面后的返回结果进行处理
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if( requestCode == PHOTO && resultCode==android.app.Activity.RESULT_OK){
			Uri uri = data.getData();
			Log.e("uri", uri.toString());			
			choosepic=getRealPath(uri);			
			try {
				System.out.println("update trip_list set thumbnail_photo=\""+choosepic+"\" where _id="+choosePicId);
				db.execSQL("update trip_list set thumbnail_photo=\""+choosepic+"\" where _id="+choosePicId);
				//cursor=db.rawQuery("update trip_list set thumbnail_photo=\"/mnt/ext_sdcard/CameraPractice/20150116_101412.jpg\" where _id=2", null);
			}
			catch (Exception e) {
				db.execSQL("create table trip_list(_id integer primary key autoincrement,trip_name varchar(40)," +
						"start_time date,end_time date," +
						"user_id varchar(20),participate varchar(100),thumbnail_photo varchar(100)," +
						"keyword varchar(255),photo_nums integer,trip_location varchar(100),is_over integer)");
				db.execSQL("update trip_list set thumbnail_photo=\""+choosepic+"\" where _id="+choosePicId);
			}
		}
		
	}
}