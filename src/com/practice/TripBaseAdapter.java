package com.practice;

import java.util.List;

import com.practice.R.string;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class TripBaseAdapter extends BaseAdapter{

	//private List<Trip> trips;
	private int resource;
	private ListView listview;
	private LayoutInflater mInflater;
	Context context;
	
	private TextView idTextView;
	private TextView timeTextView;
	private ImageView photoImageView;	
	private TextView keywordTextView;
	private Cursor cursor;
	
	public TripBaseAdapter(Context context,ListView listView,Cursor cursor){
		this.mInflater=LayoutInflater.from(context);
		this.listview=listview;
		this.context=context;
		this.cursor=cursor;
		mInflater=LayoutInflater.from(context);
		System.out.println("========tripbaseadapter builder");
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		System.out.println("========tripbaseadapter getcount:"+cursor.getCount());
		//return trips.size();
		return cursor.getCount();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if (position >= getCount()) {
			return null;
		}
		System.out.println("========tripbaseadapter getposition");
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		System.out.println("========tripbaseadapter getitem");
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		System.out.println("========before move position");
		cursor.moveToPosition(position);
		System.out.println("========after move position");
		ViewHolder viewholder=null;
		if(convertView==null){
			convertView = mInflater.inflate(R.layout.tripshow_line, null);
			viewholder=new ViewHolder();
			/*viewholder.idTextView = (TextView)convertView.findViewById(R.id.triplist_id_hidden);	//为了减少开销，则只在第一页时调用findViewById
			viewholder.nameTextView =(TextView) convertView.findViewById(R.id.triplist_title_text);
			viewholder.timeTextView=(TextView)convertView.findViewById(R.id.triplist_starttime_text);*/
			viewholder.photoImageView = (ImageView)convertView.findViewById(R.id.tripshow_pic);
			viewholder.keywordTextView=(TextView)convertView.findViewById(R.id.tripshow_pic_description);
			convertView.setTag(viewholder);
		}else{
			viewholder = (ViewHolder) convertView.getTag();
		}
        //Bitmap bm = decodeBitmap(trip.getImagepath());
		Bitmap bm = decodeBitmap(cursor.getString(6));
		System.out.println("========path:"+cursor.getString(6));
        viewholder.photoImageView.setImageBitmap(bm);
		System.out.println("========before set text");
		viewholder.keywordTextView.setText(cursor.getString(3));
		System.out.println("========text:"+cursor.getString(3));
		return convertView;
	}
	
	static class ViewHolder {
		TextView idTextView,nameTextView,timeTextView,endtimeTextView,keywordTextView;
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