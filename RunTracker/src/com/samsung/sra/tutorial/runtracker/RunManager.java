package com.samsung.sra.tutorial.runtracker;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class RunManager {
	private static final String TAG = "RunManager";
	
	private static final String PREFS_FILE = "runs";
	private static final String PREF_CURRENT_RUN_ID = "RunManager.currentRunId";
	
	public static final String ACTION_LOCATION = "com.samsung.sra.tutorial.runtracker.ACTION_LOCATION";
	
	private static final String TEST_PROVIDER = "TEST_PROVIDER";
	
	private static final int MINIMUM_TIME_INTERVAL = 0; // milliseconds
	private static final int MINIMUM_DISTANCE_INTERVAL = 0; // meters
	
	private static RunManager sRunManager;		// singleton
	private Context mAppContext;
	private LocationManager mLocationManager;
	private RunDatabaseHelper mDatabaseHelper;
	private SharedPreferences mPrefs;
	private long mCurrentRunId;
	
	// The private constructor forces code to use RunManager.get(Context)
	private RunManager(Context appContext) {
		mAppContext = appContext;
		mLocationManager = (LocationManager) appContext.getSystemService(Context.LOCATION_SERVICE);
		mDatabaseHelper = new RunDatabaseHelper(appContext);
		mPrefs = mAppContext.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
		mCurrentRunId = mPrefs.getLong(PREF_CURRENT_RUN_ID, -1);
	}
	
	// Class method for accessing the singleton
	public static RunManager get(Context c) {
		if (sRunManager == null) {
			// Use the application context to avoid leaking activities (avoids keeping a handle on a particular activity in the instance variable)
			sRunManager = new RunManager(c);
		}
		
		return sRunManager;
	}
	
	public Run startNewRun() {
		// Insert a run into the database
		Run run = insertRun();
		
		// Start tracking the run
		startTrackingRun(run);
		return run;
	}
	
	public void startTrackingRun(Run run) {
		// Keep the ID
		mCurrentRunId = run.getId();
		
		// Store it in shared preferences
		mPrefs.edit().putLong(PREF_CURRENT_RUN_ID, mCurrentRunId).commit();
		
		// Start location updates
		startLocationUpdates();
	}
	
	public void stopRun() {
		stopLocationUpdates();
		mCurrentRunId = -1;
		mPrefs.edit().remove(PREF_CURRENT_RUN_ID).commit();
	}
	
	private Run insertRun() {
		Run run = new Run();
		run.setId(mDatabaseHelper.insertRun(run));
		return run;
	}
	
	public void insertLocation(Location location) {
		if (mCurrentRunId != -1) {
			mDatabaseHelper.insertLocation(mCurrentRunId, location);
		}
	}
	
	public RunDatabaseHelper.RunCursor queryRuns() {
		return mDatabaseHelper.queryRuns();
	}
	
	public Run getRun(long id) {
		Run run = null;
		RunDatabaseHelper.RunCursor cursor = mDatabaseHelper.queryRun(id);
		cursor.moveToFirst();
		
		// If we got a row, get a run
		if (!cursor.isAfterLast())
			run = cursor.getRun();
		
		cursor.close();
		return run;
	}
	
	private PendingIntent getLocationPendingIntent(boolean shouldCreate) {
		Intent broadcast = new Intent(ACTION_LOCATION);
		int flags = shouldCreate ? 0 : PendingIntent.FLAG_NO_CREATE;
		
		return PendingIntent.getBroadcast(mAppContext, 0, broadcast, flags);
	}
	
	public void startLocationUpdates() {
		String provider = LocationManager.GPS_PROVIDER;
		
		// If we have the test provider installed and it's enabled, use it
		if (mLocationManager.getProvider(TEST_PROVIDER) != null && mLocationManager.isProviderEnabled(TEST_PROVIDER)) {
			provider = TEST_PROVIDER;
		}
		Log.d(TAG, "Using provider " + provider);
		
		// Get the last known location (if there is one) and broadcast it
		Location lastKnown = mLocationManager.getLastKnownLocation(provider);
		if (lastKnown != null) {
			// Reset the time to now
			lastKnown.setTime(System.currentTimeMillis());
			broadcastLocation(lastKnown);
		}
		
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
	
	private void broadcastLocation(Location location) {
		Intent broadcast = new Intent(ACTION_LOCATION);
		broadcast.putExtra(LocationManager.KEY_LOCATION_CHANGED, location);
		mAppContext.sendBroadcast(broadcast);
	}
	
	
	public boolean isTrackingRun() {
		return getLocationPendingIntent(false) != null;
	}
	
	public boolean isTrackingRun(Run run) {
		return mCurrentRunId == run.getId();
	}
}
