package com.practice;

import android.os.Bundle;
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
		
		db=SQLiteDatabase.openOrCreateDatabase(this.getFilesDir().toString()+"/travelbook.db3", null);
		try {
			cursor=db.rawQuery("select * from trip_list", null);
		}
		catch(Exception e) {
			db.execSQL("create table trip_list(tour_id integer primary key autoincrement,trip_name varchar(40)," +
					"start_time date,end_time date" +
					"user_id varchar(20),participate varchar(100),thumbnail_photo varchar(100)," +
					"keyword varchar(255),photo_nums int,trip_location varchar(100),is_over int)");	
			cursor=db.rawQuery("select * from trip_list", null);
		}
		tripList=(ListView)findViewById(R.id.trip_list_view);
		String[] title={"thumbnail_photo","trip_name","start_time","end_time","keyword"};
		int[] r_id={R.id.thumbnail_image,R.id.triplist_title_text,R.id.triplist_time_text,R.id.triplist_keyword_text};
		dbase.inflateList(cursor, TripList.this, R.layout.triplist_line, title, r_id, tripList);
	}		
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (db!=null&&db.isOpen()) {
			db.close();
		}
	}
}