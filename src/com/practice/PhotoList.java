package com.practice;

import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.widget.ListView;

/*
 * 测试页，从数据库中读取照片路径并显示
 * */
public class PhotoList extends Activity {
	SQLiteDatabase db;
	ListView list;
	DatabaseFunc dbaseFunc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_list);
		
		db=SQLiteDatabase.openOrCreateDatabase(this.getFilesDir().toString()+"/travelbook.db3", null);
		if (db.isOpen()) {
			System.out.println("db opened");
		}
		list=(ListView)findViewById(R.id.photolist_view);
		Cursor cursor=db.rawQuery("select * from pic_info", null);
		String[] title={"photo_path","pic_description"};
		int[] r_id={R.id.list_path,R.id.list_description};
		dbaseFunc.inflateList(cursor, PhotoList.this, R.layout.line, title, r_id, list);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (db!=null&&db.isOpen()) {
			db.close();
		}
	}
}
