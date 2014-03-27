package com.samsung.sra.tutorial.photogallery;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationReceiver extends BroadcastReceiver {
	private static final String TAG = "NotificationReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "Received result: " + getResultCode());
		
		if (getResultCode() != Activity.RESULT_OK)
			return; 	// A visible fragment cancelled the broadcast

		int requestCode = intent.getIntExtra("REQUEST_CODE", 0);
		Notification notification = intent.getParcelableExtra("NOTIFICATION");
		
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(requestCode, notification);
	}

}
