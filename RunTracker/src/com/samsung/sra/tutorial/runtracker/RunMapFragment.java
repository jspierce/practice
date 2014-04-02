package com.samsung.sra.tutorial.runtracker;

import java.util.ArrayList;
import java.util.Date;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Point;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.samsung.sra.tutorial.runtracker.RunDatabaseHelper.LocationCursor;


public class RunMapFragment extends SupportMapFragment implements LoaderCallbacks<Cursor> {
	private static final String TAG = "RunMapFragment";
	private static final String ARG_RUN_ID = "RUN_ID";
	private static final int LOAD_LOCATIONS = 0;
	
	private GoogleMap mGoogleMap;
	private long mRunId;
	private RunManager mRunManager;
	private ArrayList<Location> mLocationArray;
	private Polyline mPathLine;
	private Marker mStopMarker;
	
	private BroadcastReceiver mLocationReceiver = new LocationReceiver() {
		@Override
		protected void onLocationReceived(Context context, Location location) {	
			if (mLocationArray != null)
				mLocationArray.add(location);
			
			if (!mRunManager.isTrackingRun(mRunId)) {
				Log.d(TAG, "Not tracking run, returning");
				return;
			}
			
			if (isVisible()) {
				updateUI();
			}
		}		
	};

	public static RunMapFragment newInstance(long runId) {
		Bundle args = new Bundle();
		args.putLong(ARG_RUN_ID, runId);
		RunMapFragment rf = new RunMapFragment();
		rf.setArguments(args);
		return rf;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Check for a Run ID as an argument, and if there is one load the run's location data
		Bundle args = getArguments();
		if (args != null) {
			long runId = args.getLong(ARG_RUN_ID, -1);
			if (runId != -1) {
				mRunId = runId;
				LoaderManager lm = getLoaderManager();
				lm.initLoader(LOAD_LOCATIONS, args, this);
			}
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, parent, savedInstanceState);
		
		// Stash a reference to the Google Map
		mGoogleMap = getMap();
		
		// Show the user's location
		//mGoogleMap.setMyLocationEnabled(true);
		
		// Keep a handle on the RunManager
		mRunManager = RunManager.get(getActivity());
		
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
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		long runId = args.getLong(ARG_RUN_ID);
		return new LocationListCursorLoader(getActivity(), runId);
	}
	
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor)  {
		// Pull out the locations
		LocationCursor locationCursor = (LocationCursor) cursor;
		mLocationArray = new ArrayList<Location>();
		locationCursor.moveToFirst();
		while (!locationCursor.isAfterLast()) {
			Location location = locationCursor.getLocation();
			mLocationArray.add(location);
			locationCursor.moveToNext();
		}
		
		cursor.close();
		
		updateUI();
	}
	
	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// Clear out the location data
		mLocationArray = null;
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void updateUI() {
		if (mGoogleMap == null | mLocationArray == null)
			return;
		
		// Remove any current overlays
		if (mPathLine != null) {
			mPathLine.remove();
			mPathLine = null;
		}
			
		
		if (mStopMarker != null) {
			mStopMarker.remove();
			mStopMarker = null;
		}
		
		// Set up an overlay on the map for this run's locations
		// Create a polyline with all of the points
		PolylineOptions line = new PolylineOptions();
		
		// Also create a LatLngBounds so you can zoom to fit
		LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();
		
		// Iterate over the locations
		for (int i = 0; i < mLocationArray.size(); i++) {
			Location location = mLocationArray.get(i);
			LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
			
			Resources r = getResources();
			
			// If this is the first location, add a marker for it
			if (i == 0) {
				String startDate = new Date(location.getTime()).toString();
				MarkerOptions startMarkerOptions = new MarkerOptions()
					.position(latLng)
					.title(r.getString(R.string.run_start))
					.snippet(r.getString(R.string.run_started_at_format, startDate));
				mGoogleMap.addMarker(startMarkerOptions);
			} else if (i == (mLocationArray.size() - 1)) {
				// If this is the last marker location (and not also the first), add a marker
				String endDate = new Date(location.getTime()).toString();
				MarkerOptions finishMarkerOptions = new MarkerOptions()
					.position(latLng)
					.title(r.getString(R.string.run_finish))
					.snippet(r.getString(R.string.run_finished_at_format, endDate));
				mStopMarker = mGoogleMap.addMarker(finishMarkerOptions);
			}
			
			line.add(latLng);
			latLngBuilder.include(latLng);
		}
		
		// Add the polyline to the map
		mPathLine = mGoogleMap.addPolyline(line);
		
		// Make the map zoom to show the track, with some padding
		// Use the size of the current display in pixels as a bounding box
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		Point outSize = new Point();
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB_MR2) {
			display.getSize(outSize);
		} else {
			outSize.x = display.getWidth();
			outSize.y = display.getHeight();
		}
		LatLngBounds latLngBounds = latLngBuilder.build();
		CameraUpdate movement = CameraUpdateFactory.newLatLngBounds(latLngBounds, outSize.x, outSize.y, 15);
		mGoogleMap.moveCamera(movement);
	}
	
	
}
