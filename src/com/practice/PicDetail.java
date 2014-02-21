package com.practice;

import java.io.IOException;

import android.media.ExifInterface;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.widget.ImageView;

public class PicDetail extends Activity {

	private ImageView photoview;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pic_detail);
		
		photoview = (ImageView) findViewById(R.id.thumbnail);
		/*Bundle b=getIntent().getExtras();
		String photoname=b.getString("picName");*/
		Intent camIntent=this.getIntent();
		String photoname=camIntent.getStringExtra("picPath");
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap bm = BitmapFactory.decodeFile(photoname, options);
        photoview.setImageBitmap(bm);
	}
}