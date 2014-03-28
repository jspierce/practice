package com.samsung.sra.tutorial.runtracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class LocationReceiver extends BroadcastReceiver {
	private static final String TAG = "LocationReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// Check to see if we received a Location extra
		Location loc = (Location) intent.getParcelableExtra(LocationManager.KEY_LOCATION_CHANGED);
		
		if (loc != null) {
			onLocationReceived(context, loc);
			return;
		}
		
		// Otherwise see if the provider changed
		if (intent.hasExtra(LocationManager.KEY_PROVIDER_ENABLED)) {
			boolean enabled = intent.getBooleanExtra(LocationManager.KEY_PROVIDER_ENABLED, false);
			onProviderEnabledChanged(enabled);
		}
	}
	
	protected void onLocationReceived(Context context, Location location) {
		Log.d(TAG, this + " got location from " + location.getProvider() + ": " + location.getLatitude() + ", " + location.getLongitude());
	}

	protected void onProviderEnabledChanged(boolean enabled) {
		Log.d(TAG, "Provider " + (enabled ? "enabled" : "disabled"));
	}
}
