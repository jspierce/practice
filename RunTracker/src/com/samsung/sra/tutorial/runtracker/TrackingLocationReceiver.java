package com.samsung.sra.tutorial.runtracker;

import android.content.Context;
import android.location.Location;

public class TrackingLocationReceiver extends LocationReceiver {
	//private static final String TAG = "TrackingLocationReceiver";
	
	@Override
	protected void onLocationReceived(Context c, Location location) {
		//Log.d(TAG, "Logging a location");
		
		RunManager.get(c).insertLocation(location);
	}
}
