package com.practice;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
	private SimpleAdapter adapter;
	private View v;
    
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
		System.out.println("triplist : find listview");
		
		File filepath=new File(Environment.getExternalStorageDirectory().toString()+"/CameraPractice/database");
		if (!filepath.exists()){
			filepath.mkdir();
		}
		db=SQLiteDatabase.openOrCreateDatabase(filepath+"/travelbook.db3", null);
		
		try {
			cursor=db.rawQuery("select * from trip_list", null);
		}
		catch (Exception e) {
			db.execSQL("create table trip_list(_id integer primary key autoincrement,trip_name varchar(40)," +
					"start_time date,end_time date," +
					"user_id varchar(20),participate varchar(100),thumbnail_photo varchar(100)," +
					"keyword varchar(255),photo_nums integer,trip_location varchar(100),is_over integer)");
			cursor=db.rawQuery("select * from trip_list", null);
		}
		String[] title={"_id","trip_name","keyword"};
		int[] r_id={R.id.triplist_id_hidden,R.id.triplist_title_text,R.id.triplist_keyword_text};
		/*dbaseFunc=new DatabaseFunc();
		dbaseFunc.inflateList(cursor, PhotoList.this, R.layout.line, title, r_id, list);*/
		/*SimpleCursorAdapter adapter=new SimpleCursorAdapter(TripList.this, R.layout.triplist_line, cursor, title, r_id,	CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		list.setAdapter(adapter);*/
        adapter = new SimpleAdapter(getActivity(), getData(cursor), R.layout.triplist_line, title, r_id);  
        list.setAdapter(adapter);  
        System.out.println("triplist:set adapter");
		
		//ListView的点击事件
		/*list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id){
				Intent intent = new Intent();
    			intent.setClass(TripList.this, TripShow.class);
    			Bundle bundle = new Bundle();
    			//tripnameText=(TextView)findViewById(R.id.triplist_id_hidden);
    			tripnameText=(TextView)list.getChildAt(position).findViewById(R.id.triplist_id_hidden);
    			tripName=tripnameText.getText().toString();
    			System.out.println("triplist.java tripname:"+tripName);
    	        bundle.putString("tripName", tripName);
    	        intent.putExtra("tripName",tripName);
    	        tripName=null;
    			startActivity(intent);
			}
		});*/
	}
    
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	 {
		System.out.println("triplist oncreateview");
		return v;
	 }
	
	private List<? extends Map<String, ?>> getData(Cursor result) {  
        /*List<Map<String ,Object>> list = new ArrayList<Map<String,Object>>();          
        for (int i = 0; i < strs.length; i++) {  
            Map<String, Object> map = new HashMap<String, Object>();  
            map.put("title", strs[i]);  
            list.add(map);                
        }   */         
        
        List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map = new HashMap<String, String>();

        while (result.moveToNext()) {
          map.put("_id", result.getString(result.getColumnIndex("_id")));
          map.put("trip_name", result.getString(result.getColumnIndex("trip_name")));
          map.put("keyword", result.getString(result.getColumnIndex("keyword")));
          list.add(map);
          System.out.println("triplist map:"+result.getString(result.getColumnIndex("trip_name")));
        }
        return list;  
    }    
	
    @Override  
    public void onActivityCreated(Bundle savedInstanceState) {  
    	System.out.println("enter onactivity");
        super.onActivityCreated(savedInstanceState);    
        System.out.println("on activity created");
    }  
}