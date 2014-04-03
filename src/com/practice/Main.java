package com.practice;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

public class Main extends FragmentActivity implements ActionBar.TabListener{

	private static final String SELECTED_ITEM="selected_item";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("main oncreate");
		final ActionBar actionBar=getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		actionBar.addTab(actionBar.newTab().setText("地图").setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText("游记").setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText("探索").setTabListener(this));
		setContentView(R.layout.main);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (savedInstanceState.containsKey(SELECTED_ITEM)) {
			getActionBar().setSelectedNavigationItem(savedInstanceState.getInt(SELECTED_ITEM));
		}
		System.out.println("2");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		outState.putInt(SELECTED_ITEM, getActionBar().getSelectedNavigationIndex());
		System.out.println("3");
	}


	@Override
	public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
		// TODO Auto-generated method stub
		System.out.println("4");
		
	}

	@Override
	public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
		// TODO Auto-generated method stub
		Fragment fg_map=new MapMain();
		Fragment fg_trip=new TripList();
		Fragment fg_explore=new Explore();
		/*Bundle args=new Bundle();
		args.putInt(DummyFragment.ARG_SECTION_NUMBER, tab.getPosition()+1);
		fg.setArguments(args);*/
		FragmentManager fm=getSupportFragmentManager();
		FragmentTransaction fragmentTransaction=fm.beginTransaction();
		System.out.println("position:"+tab.getPosition());
		switch (tab.getPosition()) {
		case 0:
			fragmentTransaction.replace(R.id.container, fg_map);	
			break;
		case 1:
			fragmentTransaction.replace(R.id.container, fg_trip);
			break;
		case 2:
			fragmentTransaction.replace(R.id.container, fg_explore);			
			break;

		default:
			break;
		}
		fragmentTransaction.commit();
		System.out.println("5");
	}

	@Override
	public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
		// TODO Auto-generated method stub

		System.out.println("6");
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem mi) {
		if (mi.isCheckable()) {
			mi.setChecked(true);
		}
		switch (mi.getItemId()) {
		case R.id.action_camera:
			Intent intent_camera = new Intent();
			intent_camera.setClass(Main.this, TakePhoto.class);
			startActivity(intent_camera);			
			break;
		case R.id.action_new:
			Intent intent_new = new Intent();
			intent_new.setClass(Main.this, NewTrip.class);
			startActivity(intent_new);
		default:
			break;
		}
		return true;
	}
}
