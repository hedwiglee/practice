package com.practice;

import java.io.File;

import android.R.string;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/*
 * 测试页，从数据库中读取照片路径并显示
 * */
public class TripList extends Activity {
	SQLiteDatabase db;
	ListView list;
	TextView tripnameText;
	DatabaseFunc dbaseFunc;
	Cursor cursor;
	static public String tripName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trip_list);
		
		File filepath=new File(Environment.getExternalStorageDirectory().toString()+"/CameraPractice/database");
		if (!filepath.exists()){
			filepath.mkdir();
		}
		db=SQLiteDatabase.openOrCreateDatabase(filepath+"/travelbook.db3", null);
		list=(ListView)findViewById(R.id.trip_list_view);
		
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
		System.out.println(cursor.getCount());
		String[] title={"_id","trip_name","keyword"};
		int[] r_id={R.id.triplist_id_hidden,R.id.triplist_title_text,R.id.triplist_keyword_text};
		/*dbaseFunc=new DatabaseFunc();
		dbaseFunc.inflateList(cursor, PhotoList.this, R.layout.line, title, r_id, list);*/
		SimpleCursorAdapter adapter=new SimpleCursorAdapter(TripList.this, R.layout.triplist_line, cursor, title, r_id,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		list.setAdapter(adapter);
		
		//ListView的点击事件
		list.setOnItemClickListener(new OnItemClickListener() {
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