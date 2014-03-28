package com.practice;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class Main extends FragmentActivity implements ActionBar.TabListener{

	private static final String SELECTED_ITEM="selected_item";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		final ActionBar actionBar=getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.addTab(actionBar.newTab().setText("地图").setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText("游记").setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText("探索").setTabListener(this));
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (savedInstanceState.containsKey(SELECTED_ITEM)) {
			getActionBar().setSelectedNavigationItem(savedInstanceState.getInt(SELECTED_ITEM));
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		outState.putInt(SELECTED_ITEM, getActionBar().getSelectedNavigationIndex());
	}


	@Override
	public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
		// TODO Auto-generated method stub
		Fragment fg=new DummyFragment();
		Bundle args=new Bundle();
		args.putInt(DummyFragment.ARG_SECTION_NUMBER, tab.getPosition()+1);
		fg.setArguments(args);
		FragmentManager fm=getSupportFragmentManager();
		FragmentTransaction fragmentTransaction=fm.beginTransaction();
		fragmentTransaction.replace(R.id.container, fg);
		fragmentTransaction.commit();
	}

	@Override
	public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}
}
