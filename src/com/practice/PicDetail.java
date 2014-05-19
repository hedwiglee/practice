package com.practice;

import static android.widget.Toast.makeText;
import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

import java.io.File;
import edu.cmu.pocketsphinx.AssetsTask;
import edu.cmu.pocketsphinx.AssetsTaskCallback;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/*
 * 为所拍摄的照片添加详情
 * */
public class PicDetail extends Activity{    
	
	private ImageView photoview;
	SQLiteDatabase db;
	Button saveBtn;
	String photoname;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pic_detail);
		//显示图片
		photoview = (ImageView) findViewById(R.id.thumbnail);
		Intent camIntent=this.getIntent();
		photoname=camIntent.getStringExtra("picPath");
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        Bitmap bm = BitmapFactory.decodeFile(photoname, options);
        photoview.setImageBitmap(bm);
        //数据库操作
        File filepath=new File(Environment.getExternalStorageDirectory().toString()+"/CameraPractice/database");
		if (!filepath.exists()){
			filepath.mkdir();
		}
		db=SQLiteDatabase.openOrCreateDatabase(filepath+"/travelbook.db3", null);
		saveBtn=(Button)findViewById(R.id.save_detail);
		saveBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View source) {
				// 获取用户输入
				String description=((EditText)findViewById(R.id.photo_description)).getText().toString();
				try {
					insertData(db,description,photoname);
					//启动新activity
	    			Intent intent = new Intent();
	    			intent.setClass(PicDetail.this, PocketSphinxActivity.class);
	    			startActivity(intent);
				}
				catch(SQLException e) {
					db.execSQL("create table pic_info(_id integer primary key autoincrement,integer tour_id," +
								"photo_time date," +
								"pic_description varchar(255),photo_keyword varchar(255),photo_place varchar(100)," +
								"photo_path varchar(150))");
					insertData(db, description, photoname);
				}
			}
		});        
	}	

	private void insertData(SQLiteDatabase db,String description,String path){
		db.execSQL("insert into pic_info values (null,1,null,?,null,null,?)",new String[] {description,path});
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (db!=null&&db.isOpen()) {
			db.close();
		}
	}	
}