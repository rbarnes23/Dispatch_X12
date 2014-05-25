package com.dispatch_x12;

import com.dispatch_x12.services.ChatService;

import android.app.Application;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class X12Application extends Application {

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	
	
	@Override
	public void onCreate() {
		super.onCreate();
		AppSettings.setContext(getApplicationContext());
		//Initialize the preference manager
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		// start the service from here //ChatService is your service class name
		Bundle bundle = new Bundle();
		bundle.putString("memberid", AppSettings.getMemberid());
		bundle.putString("serverip", AppSettings.getNetworkid());
		bundle.putInt("serverport", 2004);
		Intent commIntent = new Intent(getApplicationContext(), ChatService.class);
		commIntent.putExtras(bundle);
		startService(commIntent);

	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}
	

}