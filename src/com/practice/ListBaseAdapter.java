package com.practice;

import java.io.File;
import java.io.IOException;

import edu.cmu.pocketsphinx.Assets;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.StaticLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ListBaseAdapter extends BaseAdapter{

	private LayoutInflater mInflater;
	Context context;
	
	private Cursor cursor;
	
	public ListBaseAdapter(Context context,ListView listView,Cursor cursor){
		this.mInflater=LayoutInflater.from(context);
		this.context=context;
		this.cursor=cursor;
		mInflater=LayoutInflater.from(context);		
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		//return trips.size();
		return cursor.getCount();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if (position >= getCount()) {
			return null;
		}
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	static ViewHolder viewholder;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		cursor.moveToPosition(position);
		//ViewHolder viewholder=null;
		viewholder=null;
		if(convertView==null){
			convertView = mInflater.inflate(R.layout.triplist_line, null);
			viewholder=new ViewHolder();
			viewholder.photoImageView = (ImageView)convertView.findViewById(R.id.thumbnail_image);
			viewholder.keywordTextView=(TextView)convertView.findViewById(R.id.triplist_keyword_text);
			viewholder.titleTextView=(TextView)convertView.findViewById(R.id.triplist_title_text);
			viewholder.starttime=(TextView)convertView.findViewById(R.id.triplist_starttime_text);	
			viewholder.endtime=(TextView)convertView.findViewById(R.id.triplist_endtime_text);	
			viewholder.idTextView=(TextView)convertView.findViewById(R.id.triplist_id_hidden);			
			convertView.setTag(viewholder);
		}else{
			viewholder = (ViewHolder) convertView.getTag();
		}
        System.out.println("========photoimageview");
		viewholder.idTextView.setText(cursor.getString(0));
		viewholder.titleTextView.setText(cursor.getString(1));
		viewholder.starttime.setText(cursor.getString(2));
		viewholder.endtime.setText(cursor.getString(3));
		viewholder.keywordTextView.setText(cursor.getString(7));
		if (cursor.getString(6)!=null) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			Bitmap bm = BitmapFactory.decodeFile(cursor.getString(6));
	        viewholder.photoImageView.setImageBitmap(bm);
		}
		return convertView;
	}
		
	static class ViewHolder {
		TextView keywordTextView,titleTextView,starttime,endtime,idTextView;
		ImageView photoImageView;
	}
	
	public static Bitmap decodeBitmap(String path, int compareSize) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// 通过这个bitmap获取图片的宽和高
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		if (bitmap == null) {
			System.out.println("bitmap为空");
		}
		float realWidth = options.outWidth;
		float realHeight = options.outHeight;
		System.out.println("真实图片高度：" + realHeight + "宽度:" + realWidth);
		// 计算缩放比
		int scale = (int) ((realHeight > realWidth ? realHeight : realWidth) / compareSize);
		if (scale <= 0) {
			scale = 1;
		}
		options.inSampleSize = scale;
		options.inJustDecodeBounds = false;
		// 注意这次要把options.inJustDecodeBounds 设为 false,这次图片是要读取出来的。
		bitmap = BitmapFactory.decodeFile(path, options);
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		System.out.println("缩略图高度：" + h + "宽度:" + w);
		return bitmap;
	}

	/**
	 * 快捷的返回 100像素的小图像。
	 * 
	 * @param path
	 * @return
	 */
	public static Bitmap decodeBitmap(String path) {
		return decodeBitmap(path, 300);
	}		
}