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

public class TripBaseAdapter extends BaseAdapter{

	private LayoutInflater mInflater;
	Context context;
	
	private Cursor cursor;
	
	public TripBaseAdapter(Context context,ListView listView,Cursor cursor){
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
			convertView = mInflater.inflate(R.layout.tripshow_line, null);
			viewholder=new ViewHolder();
			/*viewholder.idTextView = (TextView)convertView.findViewById(R.id.triplist_id_hidden);	//为了减少开销，则只在第一页时调用findViewById
			viewholder.nameTextView =(TextView) convertView.findViewById(R.id.triplist_title_text);
			viewholder.timeTextView=(TextView)convertView.findViewById(R.id.triplist_starttime_text);*/
			viewholder.photoImageView = (ImageView)convertView.findViewById(R.id.tripshow_pic);
			viewholder.keywordTextView=(TextView)convertView.findViewById(R.id.tripshow_pic_description);
			viewholder.latiTextView=(TextView)convertView.findViewById(R.id.tripshow_lati);			
			//viewholder.longiTextView=(TextView)convertView.findViewById(R.id.tripshow_longi);	
			viewholder.pictime=(TextView)convertView.findViewById(R.id.tripshow_pic_time);				
			convertView.setTag(viewholder);
		}else{
			viewholder = (ViewHolder) convertView.getTag();
		}
        System.out.println("========photoimageview");
		viewholder.pictime.setText(cursor.getString(2));
		viewholder.keywordTextView.setText(cursor.getString(3));
		/*viewholder.latiTextView.setText(Integer.parseInt(cursor.getString(5))*1E-6+"");
		System.out.println("========latinum"+Integer.parseInt(cursor.getString(5))*1E-6+"");
		viewholder.longiTextView.setText(Integer.parseInt(cursor.getString(6))*1E-6+"");*/
		viewholder.photoImageView.setTag(cursor.getString(8));
		System.out.println("tripshow中的图片路径："+cursor.getString(8));
		viewholder.latiTextView.setText(cursor.getString(7));
		//new ImageAsynctask(viewholder.photoImageView, cursor.getString(8), viewholder).execute();
		//Bitmap bm = decodeBitmap(cursor.getString(8));
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Bitmap bm = BitmapFactory.decodeFile(cursor.getString(8));
        viewholder.photoImageView.setImageBitmap(bm);
		return convertView;
	}
		
	static class ViewHolder {
		TextView idTextView,nameTextView,timeTextView,endtimeTextView,keywordTextView,latiTextView,longiTextView,pictime;
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