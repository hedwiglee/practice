package com.practice;

import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DummyFragment extends Fragment {

	public static final String ARG_SECTION_NUMBER="section_number";
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle saveInstanceState){
		TextView textView=new TextView(getActivity());
		textView.setGravity(Gravity.START);
		Bundle args=getArguments();
		textView.setText(args.getInt(ARG_SECTION_NUMBER)+" ");
		textView.setTextSize(30);
		return textView;
	}
}
