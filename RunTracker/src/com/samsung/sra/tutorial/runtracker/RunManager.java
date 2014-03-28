package com.samsung.sra.tutorial.runtracker;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

public class RunManager {
	private static final String TAG = "RunManager";
	
	public static final String ACTION_LOCATION = "com.samsung.sra.tutorial.runtracker.ACTION_LOCATION";
	
	private static final int MINIMUM_TIME_INTERVAL = 0; // milliseconds
	private static final int MINIMUM_DISTANCE_INTERVAL = 0; // meters
	
	private static RunManager sRunManager;		// singleton
	private Context mAppContext;
	private LocationManager mLocationManager;
	
	// The private constructor forces code to use RunManager.get(Context)
	private RunManager(Context appContext) {
		mAppContext = appContext;
		mLocationManager = (LocationManager) appContext.getSystemService(Context.LOCATION_SERVICE);
	}
	
	// Class method for accessing the singleton
	public static RunManager get(Context c) {
		if (sRunManager == null) {
			// Use the application context to avoid leaking activities (avoids keeping a handle on a particular activity in the instance variable)
			sRunManager = new RunManager(c);
		}
		
		return sRunManager;
	}
	
	private PendingIntent getLocationPendingIntent(boolean shouldCreate) {
		Intent broadcast = new Intent(ACTION_LOCATION);
		int flags = shouldCreate ? 0 : PendingIntent.FLAG_NO_CREATE;
		
		return PendingIntent.getBroadcast(mAppContext, 0, broadcast, flags);
	}
	
	public void startLocationUpdates() {
		String provider = LocationManager.GPS_PROVIDER;
		
		// Start updates from the location manager
		PendingIntent pi = getLocationPendingIntent(true);
		mLocationManager.requestLocationUpdates(provider, MINIMUM_TIME_INTERVAL, MINIMUM_DISTANCE_INTERVAL, pi);
	}
	
	public void stopLocationUpdates() {
		PendingIntent pi = getLocationPendingIntent(false);
		if (pi != null) {
			mLocationManager.removeUpdates(pi);
			pi.cancel();
		}
	}
	
	public boolean isTrackingRun() {
		return getLocationPendingIntent(false) != null;
	}
}
