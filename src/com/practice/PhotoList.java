package com.practice;

import java.io.File;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ListView;

/*
 * 测试页，从数据库中读取照片路径并显示
 * */
public class PhotoList extends Activity {
	SQLiteDatabase db;
	ListView list;
	DatabaseFunc dbaseFunc;
	Cursor cursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("======enter PhotoList");
		setContentView(R.layout.photo_list);
		
		File filepath=new File(Environment.getExternalStorageDirectory().toString()+"/CameraPractice/database");
		if (!filepath.exists()){
			filepath.mkdir();
		}
		db=SQLiteDatabase.openOrCreateDatabase(filepath+"/travelbook.db3", null);
		list=(ListView)findViewById(R.id.photolist_view);
		
		try {
			cursor=db.rawQuery("select * from pic_info", null);
		}
		catch (Exception e) {
			db.execSQL("create table pic_info(_id integer primary key autoincrement,integer tour_id," +
					"photo_time date," +
					"pic_description varchar(255),photo_keyword varchar(255),photo_loclati int," +
					"photo_loclongi int,photo_place varchar(100)," +
					"photo_path varchar(150))");
			cursor=db.rawQuery("select * from pic_info", null);
		}
		System.out.println(cursor.getCount());
		String[] title={"photo_path","pic_description","photo_loclati","photo_loclongi"};
		int[] r_id={R.id.list_path,R.id.list_description,R.id.list_lati,R.id.list_longi};
		/*dbaseFunc=new DatabaseFunc();
		dbaseFunc.inflateList(cursor, PhotoList.this, R.layout.line, title, r_id, list);*/
		SimpleCursorAdapter adapter=new SimpleCursorAdapter(PhotoList.this, R.layout.line, cursor, title, r_id,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		list.setAdapter(adapter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (db!=null&&db.isOpen()) {
			db.close();
		}
	}
}