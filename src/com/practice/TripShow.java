package com.practice;

import java.io.File;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class TripShow extends Activity {
	Button endTripButton;
	TextView tripNameTextView;
	String tripname;
	SQLiteDatabase db;
	Cursor cursor;
	
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
			cursor=db.rawQuery("select * from trip_list where _id='"+tripname+"'", null);
		}
		catch (Exception e) {
			db.execSQL("create table trip_list(_id integer primary key autoincrement,trip_name varchar(40)," +
					"start_time date,end_time date," +
					"user_id varchar(20),participate varchar(100),thumbnail_photo varchar(100)," +
					"keyword varchar(255),photo_nums integer,trip_location varchar(100),is_over integer)");
			cursor=db.rawQuery("select * from trip_list where _id='"+tripname+"'", null);
		}
		
		//获取该_id对应的旅程名称，并写到textview
		cursor.moveToFirst();
		String valueString=cursor.getString(cursor.getColumnIndex("trip_name"));
		tripNameTextView=(TextView)findViewById(R.id.tripshow_travelname);
		tripNameTextView.setText(valueString.toCharArray(), 0, valueString.length());
		
		
		endTripButton=(Button)findViewById(R.id.end_trip_btn);
		endTripButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
	}	

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (db!=null&&db.isOpen()) {
			db.close();
		}
	}
}
