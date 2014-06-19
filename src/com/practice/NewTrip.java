package com.practice;

import java.io.File;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import com.practice.DatabaseFunc;

public class NewTrip extends Activity {
	
	SQLiteDatabase db;
	Button saveButton;
	DatabaseFunc dbfunc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_trip);
		
		String path=Environment.getExternalStorageDirectory().toString();
		File storepath=new File(path+"/CameraPractice");
		if (!storepath.exists()){
			storepath.mkdir();
		}
		
		saveButton=(Button)findViewById(R.id.newtrip_save_btn);
		//create database
		/*dbfunc=new DatabaseFunc();
		dbfunc.createDB(dbfunc.db);*/
		File filepath=new File(Environment.getExternalStorageDirectory().toString()+"/CameraPractice/database");
		if (!filepath.exists()){
			filepath.mkdir();
		}
		db=SQLiteDatabase.openOrCreateDatabase(filepath+"/travelbook.db3", null);
		
		saveButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String tripname=((EditText)findViewById(R.id.newtrip_tripname_plain)).getText().toString();
				String tripstart=((EditText)findViewById(R.id.newtrip_starttime_plain)).getText().toString();
				String tripend=((EditText)findViewById(R.id.newtrip_endtime_plain)).getText().toString();
				String tripkey=((EditText)findViewById(R.id.newtrip_keywords_plain)).getText().toString();
				/*try {
					db.execSQL("insert into trip_list(trip_name,start_time,end_time,keyword,is_over) " +
							"values (?,?,?,?,0)",new String[] {tripname,tripstart,tripend,tripkey});
				}
				catch (Exception e) {*/
					db.execSQL("create table if not exists trip_list(_id integer primary key autoincrement,trip_name varchar(40)," +
							"start_time date,end_time date," +
							"user_id varchar(20),participate varchar(100),thumbnail_photo varchar(100)," +
							"keyword varchar(255),photo_nums integer,trip_location varchar(100),is_over integer)");
					db.execSQL("insert into trip_list(trip_name,start_time,end_time,keyword,is_over) " +
							"values (?,?,?,?,0)",new String[] {tripname,tripstart,tripend,tripkey});
				//}
				
				//启动新activity
    			Intent intent = new Intent();
    			intent.setClass(NewTrip.this, MapMain.class);
    			startActivity(intent);
    			
			}
		});
	}
}