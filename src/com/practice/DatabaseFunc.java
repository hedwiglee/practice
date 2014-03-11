package com.practice;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.ListView;

/*
 * 数据库类
 * */
public class DatabaseFunc {
	public SQLiteDatabase db;
	//创建或打开数据库
	public void createDB(SQLiteDatabase db){
		File filepath=new File(Environment.getExternalStorageDirectory().toString()+"/CameraPractice/database");
		if (!filepath.exists()){
			filepath.mkdir();
		}
		db=SQLiteDatabase.openOrCreateDatabase(filepath+"/photolist.db3", null);
	}
	//填充listview
	public void inflateList(Cursor cursor,Context context,int layoutInt,String[] name,int[] id,ListView list) {
		//填充simpleCursorAdapter
		SimpleCursorAdapter adapter=new SimpleCursorAdapter(context, layoutInt, cursor, name,id,
								CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		//显示数据
		list.setAdapter(adapter);
	}
}
