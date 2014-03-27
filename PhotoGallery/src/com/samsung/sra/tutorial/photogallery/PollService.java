package com.samsung.sra.tutorial.photogallery;

import java.util.ArrayList;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.util.Log;

public class PollService extends IntentService {
	private static final String TAG = "PollService";
	
	private static final int POLL_INTERVAL = 1000 * 15;	// 15 seconds
	
	public PollService() {
		super(TAG);
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		// Make sure our service can use the network in the background
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		@SuppressWarnings("deprecation")
		boolean isNetworkAvailable = cm.getBackgroundDataSetting() && cm.getActiveNetworkInfo() != null;
		if (!isNetworkAvailable) return;
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String query = prefs.getString(FlickrFetchr.PREF_SEARCH_QUERY, null);
		String lastResultId = prefs.getString(FlickrFetchr.PREF_LAST_RESULT_ID, null);
		
		ArrayList<GalleryItem> items;
		if (query != null) {
			items = new FlickrFetchr().search(query);
		} else {
			items = new FlickrFetchr().fetchItems();
		}
		
		if (items.size() == 0)
			return;
		
		String resultId = items.get(0).getId();
		
		if (!resultId.equals(lastResultId)) {
			Log.i(TAG, "Got a new result: " + resultId);
		} else {
			Log.i(TAG, "Got an old result: " + resultId);
		}
		
		prefs.edit()
			.putString(FlickrFetchr.PREF_LAST_RESULT_ID, resultId)
			.commit();
	}

	public static void setServiceAlarm(Context context, boolean turnOn) {
		Intent i = new Intent(context, PollService.class);
		PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
		
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
		
		if (turnOn) {
			alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), POLL_INTERVAL, pi);
		} else {
			alarmManager.cancel(pi);
			pi.cancel();
		}
	}
	
	public static boolean isServiceAlarmOn(Context context) {
		Intent i = new Intent(context, PollService.class);
		
		// Will return null if a pending intent doesn't already exist for the intent type
		PendingIntent pi = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_NO_CREATE);
		
		return pi != null;
	}
}
