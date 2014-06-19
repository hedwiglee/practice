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

	private LayoutInflater inflater;
	private List<Trip> trips;
	private int resource;
	private ListView listview;
	Context context;
	
	private TextView idTextView;
	private TextView timeTextView;
	private ImageView photoImageView;	
	private TextView keywordTextView;
	
	public TripBaseAdapter(Context context,ListView listView,List<Trip> trip){
		this.inflater=LayoutInflater.from(context);
		this.listview=listview;
		this.context=context;
		this.trips=trip;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return trips.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if (position >= getCount()) {
			return null;
		}
		return trips.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewholder=null;
		if(convertView==null){
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.tripshow_line, null);
			viewholder=new ViewHolder();
			/*viewholder.idTextView = (TextView)convertView.findViewById(R.id.triplist_id_hidden);	//为了减少开销，则只在第一页时调用findViewById
			viewholder.nameTextView =(TextView) convertView.findViewById(R.id.triplist_title_text);
			viewholder.timeTextView=(TextView)convertView.findViewById(R.id.triplist_starttime_text);*/
			viewholder.photoImageView = (ImageView)convertView.findViewById(R.id.tripshow_pic);
			viewholder.keywordTextView=(TextView)convertView.findViewById(R.id.tripshow_pic_description);
		}
		Trip trip = trips.get(position);
		viewholder.keywordTextView.setText(trip.getKeyword());
		//timeTextView.setText(trip.getTime()+"");		
		//载入图片
		BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        Bitmap bm = BitmapFactory.decodeFile(trip.getImagepath(), options);
        viewholder.photoImageView.setImageBitmap(bm);
		return convertView;
	}
	
	static class ViewHolder {
		TextView idTextView,nameTextView,timeTextView,endtimeTextView,keywordTextView;
		ImageView photoImageView;
	}
	
}