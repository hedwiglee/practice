package com.practice;

import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.widget.ListView;

public class PhotoList extends Activity {
	SQLiteDatabase db;
	ListView list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_list);
		
		db=SQLiteDatabase.openOrCreateDatabase(this.getFilesDir().toString()+"/photolist.db3", null);
		if (db.isOpen()) {
			System.out.println("db opened");
		}
		list=(ListView)findViewById(R.id.photolist_view);
		System.out.println("find the listview");
		Cursor cursor=db.rawQuery("select * from pic_info", null);
		System.out.println("searching");
		inflateList(cursor);
		System.out.println("put it in the list");
	}

	private void inflateList(Cursor cursor) {
		//填充simpleCursorAdapter
		SimpleCursorAdapter adapter=new SimpleCursorAdapter(PhotoList.this, R.layout.line, cursor, 
								new String[] {"photo_path","pic_description"},
								new int[] {R.id.list_path,R.id.list_description},
								CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		//显示数据
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
