package com.practice;

import java.util.List;  
import java.util.Map;  
import android.content.Context;  
import android.graphics.Bitmap;  
import android.view.LayoutInflater;  
import android.view.View;  
import android.view.ViewGroup;  
import android.widget.Checkable;  
import android.widget.ImageView;  
import android.widget.RatingBar;  
import android.widget.SimpleAdapter;  
import android.widget.TextView;  

public class MyAdapter extends SimpleAdapter{

	public MyAdapter(Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return super.getView(position, convertView, parent);
	}
	
}