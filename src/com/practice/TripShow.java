package com.practice;

import java.io.File;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ListView;
import android.widget.TextView;

public class TripShow extends Activity {
	//Button endTripButton;
	TextView tripNameTextView;
	TextView timeTextView;
	//TextView tripoverTextView;
	String tripname;
	SQLiteDatabase db;
	Cursor cursor;
	Cursor isoverCursor;
	ListView list;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trip_show); 
		//triplist传递的旅程的_id值
		Intent camIntent=this.getIntent();
		tripname=camIntent.getStringExtra("tripName");
		
		//数据库操作
		File filepath=new File(Environment.getExternalStorageDirectory().toString()+"/CameraPractice/database");
		if (!filepath.exists()){
			filepath.mkdir();
		}
		db=SQLiteDatabase.openOrCreateDatabase(filepath+"/travelbook.db3", null);
		
		try {
			cursor=db.rawQuery("select * from trip_list where _id="+tripname, null);
		}
		catch (Exception e) {
			db.execSQL("create table pic_info(_id integer primary key autoincrement,integer tour_id," +
					"photo_time date," +  
					"pic_description varchar(255),photo_keyword varchar(255),photo_loclati int," +
					"photo_loclongi int,photo_place varchar(100)," +
					"photo_path varchar(150))");
			cursor=db.rawQuery("select * from trip_list where _id="+tripname, null);
		}
		
		//获取该_id对应的旅程名称，并写到textview
		cursor.moveToFirst();
		String valueString=cursor.getString(cursor.getColumnIndex("trip_name"));
		String timeString="|  "+cursor.getString(cursor.getColumnIndex("start_time"))+"-"
						+cursor.getString(cursor.getColumnIndex("end_time"));
		tripNameTextView=(TextView)findViewById(R.id.tripshow_travelname);
		timeTextView=(TextView)findViewById(R.id.tripshow_time);
		tripNameTextView.setText(valueString.toCharArray(), 0, valueString.length());
		timeTextView.setText(timeString.toCharArray(), 0, timeString.length());
		
		list=(ListView)findViewById(R.id.tripshow_list);
		
		//读取图片列表和相应描述
		try {
			cursor=db.rawQuery("select * from pic_info where integer="+tripname, null);
		}
		catch (Exception e) {
			db.execSQL("create table pic_info(_id integer primary key autoincrement,tour_id integer," +
					"photo_time date," +
					"pic_description varchar(255),photo_keyword varchar(255),photo_loclati int," +
					"photo_loclongi int,photo_place varchar(100)," +
					"photo_path varchar(150))");
			cursor=db.rawQuery("select * from pic_info where integer="+tripname, null);
		}		
		System.out.println(cursor.getCount());
		System.out.println("========before tripshow");
		TripBaseAdapter tripBaseAdapter=new TripBaseAdapter(this, list, cursor);
		System.out.println("========after new a tripadapter");
		list.setAdapter(tripBaseAdapter);
		System.out.println("========end of tripshow");
		
		/*SimpleCursorAdapter adapter=new SimpleCursorAdapter(TripShow.this, R.layout.tripshow_line, cursor, title, r_id,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		list.setAdapter(adapter);*/
	}	

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (db!=null&&db.isOpen()) {
			db.close();
		}
	}
}