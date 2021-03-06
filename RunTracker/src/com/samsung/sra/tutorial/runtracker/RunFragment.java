package com.samsung.sra.tutorial.runtracker;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class RunFragment extends Fragment {
	private static final String ARG_RUN_ID = "RUN_ID";
	private static final int NOTIFICATION_ID = 1;
	private static final int LOAD_RUN = 0;
	private static final int LOAD_LOCATION = 1;
	
	private Button mStartButton, mStopButton, mMapButton;
	private TextView mStartedTextView, mLatitudeTextView, mLongitudeTextView, mAltitudeTextView, mDurationTextView;
	private RunManager mRunManager;
	private Run mRun;
	private Location mLastLocation;
	
	private BroadcastReceiver mLocationReceiver = new LocationReceiver() {
		@Override
		protected void onLocationReceived(Context context, Location location) {
			if (!mRunManager.isTrackingRun(mRun))
				return;
			
			mLastLocation = location;
			if (isVisible())
				updateUI();
		}
		
		@Override
		protected void onProviderEnabledChanged(boolean enabled) {
			int toastText = enabled ? R.string.gps_enabled : R.string.gps_disabled;
			Toast.makeText(getActivity(), toastText, Toast.LENGTH_LONG).show();
		}
	};
	
	public static RunFragment newInstance(long runId) {
		Bundle args = new Bundle();
		args.putLong(ARG_RUN_ID, runId);
		RunFragment rf = new RunFragment();
		rf.setArguments(args);
		return rf;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		
		mRunManager = RunManager.get(getActivity());

		// Check for a Run ID as an argument and find the run
		Bundle args = getArguments();
		if (args != null) {
			long runId = args.getLong(ARG_RUN_ID, -1);
			if (runId != -1) {
				LoaderManager lm = getLoaderManager();
				lm.initLoader(LOAD_RUN, args,  new RunLoaderCallbacks());
				lm.initLoader(LOAD_LOCATION, args, new LocationLoaderCallbacks());
			}
		}		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_run, container, false);
		
		mStartedTextView = (TextView) v.findViewById(R.id.run_startedTextView);
		mLatitudeTextView = (TextView) v.findViewById(R.id.run_latitudeTextView);
		mLongitudeTextView = (TextView) v.findViewById(R.id.run_longitudeTextView);
		mAltitudeTextView = (TextView) v.findViewById(R.id.run_altitudeTextView);
		mDurationTextView = (TextView) v.findViewById(R.id.run_durationTextView);
		
		mStartButton = (Button) v.findViewById(R.id.run_startButton);
		mStartButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mRun == null) {
					mRun = mRunManager.startNewRun();
				} else {
					mRunManager.startTrackingRun(mRun);
				}
				
				updateUI();
				
				// Pop up an ongoing notification that we're tracking the user
				Activity activity = getActivity();
				Intent i = new Intent(activity, RunActivity.class);
				i.putExtra(RunActivity.EXTRA_RUN_ID, mRun.getId());
				PendingIntent pi = PendingIntent.getActivity(activity, 0, i, 0);
				Resources r = getResources();
				
				Notification notification = new NotificationCompat.Builder(activity)
					.setTicker(r.getString(R.string.notification_title))
					.setSmallIcon(R.drawable.ic_launcher)
					.setContentTitle(r.getString(R.string.notification_title))
					.setContentText(r.getString(R.string.notification_text))
					.setContentIntent(pi)
					.setOngoing(true)
					.build();
				
				NotificationManager notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
				notificationManager.notify(NOTIFICATION_ID, notification);

			}
		});
		
		mStopButton = (Button) v.findViewById(R.id.run_stopButton);
		mStopButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mRunManager.stopRun();
				updateUI();
				
				// Remove the ongoing notification
				NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
				notificationManager.cancel(NOTIFICATION_ID);
			}
		});
		
		mMapButton = (Button) v.findViewById(R.id.run_mapButton);
		mMapButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), RunMapActivity.class);
				i.putExtra(RunMapActivity.EXTRA_RUN_ID, mRun.getId());
				startActivity(i);
			}
		});
		
		
		updateUI();
		
		return v;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		getActivity().registerReceiver(mLocationReceiver, new IntentFilter(RunManager.ACTION_LOCATION));
	}
	
	@Override
	public void onStop() {
		getActivity().unregisterReceiver(mLocationReceiver);
				
		super.onStop();
	}
	
	
	public void updateUI() {
		boolean started = mRunManager.isTrackingRun();
		boolean trackingThisRun = mRunManager.isTrackingRun(mRun);
		
		if (mRun != null)
			mStartedTextView.setText(mRun.getStartDate().toString());
		
		int durationSeconds = 0;
		if (mRun != null && mLastLocation != null) {
			durationSeconds = mRun.getDurationSeconds(mLastLocation.getTime());
			mLatitudeTextView.setText(Double.toString(mLastLocation.getLatitude()));
			mLongitudeTextView.setText(Double.toString(mLastLocation.getLongitude()));
			mAltitudeTextView.setText(Double.toString(mLastLocation.getAltitude()));
			mMapButton.setEnabled(true);
		} else {
			mMapButton.setEnabled(false);
		}
		
		mDurationTextView.setText(Run.formatDuration(durationSeconds));
		
		mStartButton.setEnabled(!started);
		mStopButton.setEnabled(started && trackingThisRun);
	}
	
	private class RunLoaderCallbacks implements LoaderCallbacks<Run>  {
		
		@Override
		public Loader<Run> onCreateLoader(int id, Bundle args) {
			return new RunLoader(getActivity(), args.getLong(ARG_RUN_ID));
		}
		
		@Override
		public void onLoadFinished(Loader<Run> loader, Run run) {
			mRun = run;
			updateUI();
		}
		
		@Override
		public void onLoaderReset(Loader<Run> loader) {
			// Do nothing
		}
	}
	
	private class LocationLoaderCallbacks implements LoaderCallbacks<Location> {
		
		@Override
		public Loader<Location> onCreateLoader(int id, Bundle args) {
			return new LastLocationLoader(getActivity(), args.getLong(ARG_RUN_ID));
		}
		
		@Override
		public void onLoadFinished(Loader<Location> loader, Location lastLocation) {
			mLastLocation = lastLocation;
			updateUI();
		}
		
		@Override
		public void onLoaderReset(Loader<Location> loader) {
			// Do nothing
		}
	}
}
