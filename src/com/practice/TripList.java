package com.practice;

import java.io.File;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.ListView;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.practice.DatabaseFunc;

/*
 * 所有旅行记录以列表展示
 * */
public class TripList extends Activity {
	
	SQLiteDatabase db;
	ListView tripList;
	DatabaseFunc dbase;
	Cursor cursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trip_list);
		
		File filepath=new File(Environment.getExternalStorageDirectory().toString()+"/CameraPractice/database");
		if (!filepath.exists()){
			filepath.mkdir();
		}
		db=SQLiteDatabase.openOrCreateDatabase(filepath+"/travelbook.db3", null);
		try {
			cursor=db.rawQuery("select * from trip_list", null);
		}
		catch(Exception e) {
			db.execSQL("create table trip_list(tour_id integer primary key autoincrement,trip_name varchar(40)," +
					"start_time date,end_time date," +
					"user_id varchar(20),participate varchar(100),thumbnail_photo varchar(100)," +
					"keyword varchar(255),photo_nums integer,trip_location varchar(100),is_over integer)");
			cursor=db.rawQuery("select * from trip_list", null);
			System.out.println("set catch");
		}
		tripList=(ListView)findViewById(R.id.trip_list_view);
		System.out.println("find list view");
		String[] title={"trip_name","start_time","end_time","keyword"};
		System.out.println("string");
		int[] r_id={R.id.triplist_title_text,R.id.triplist_starttime_text,
					R.id.triplist_endtime_text,R.id.triplist_keyword_text};
		System.out.println("int");
		System.out.println("count:"+cursor.getCount());
		/*dbase=new DatabaseFunc();
		dbase.inflateList(cursor, TripList.this, R.layout.triplist_line, title, r_id, tripList);*/
		SimpleCursorAdapter adapter=new SimpleCursorAdapter(TripList.this, R.layout.triplist_line, cursor, title, r_id,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		tripList.setAdapter(adapter);
	}		
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (db!=null&&db.isOpen()) {
			db.close();
		}
	}
}