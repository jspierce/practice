package com.samsung.sra.fun.braininput;

import android.app.Fragment;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;

public class BrainScanFragment extends Fragment {
	//private static final String TAG = "BrainScanFragment";
	
	private int mCurrentAngle = 0;
	private boolean mScanning = false;
	private OrientationEventListener mOrientationEventListener;
	private MediaPlayer mMediaPlayer;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_brain_scan, container, false);
		
		View scanView = v.findViewById(R.id.input_panel);
		scanView.setFocusableInTouchMode(true);
		scanView.setFocusable(true);
		scanView.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (!mScanning) {
					//Log.d(TAG, "Not scanning, checking angle");
					if ((mCurrentAngle >= 265 && mCurrentAngle <= 275) || (mCurrentAngle >= 85 && mCurrentAngle <= 95)) {
						startScanning();
					}
				}
					
				return true;
			}
		});
		
		
		return v;
	}
	
	private void startScanning() {
		mScanning = true;
		
		if (mMediaPlayer != null) {
			mMediaPlayer.start();
		}
	}
	
	private void stopScanning() {
		mScanning = false;
		
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
		}
		
		Intent i = new Intent(getActivity(), IntentionActivity.class);
		startActivity(i);
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		
		mMediaPlayer = MediaPlayer.create(getActivity(), R.raw.scanning_sound);
		if (mMediaPlayer != null)
			mMediaPlayer.setLooping(true);
		
		mOrientationEventListener = new OrientationEventListener(getActivity()) {

			@Override
			public void onOrientationChanged(int angle) {
				//Log.d(TAG, "Orientation: " + angle);
				mCurrentAngle = angle;
				
				if (mScanning) {
					//Log.d(TAG, "Scanning, checking angle");
					if ((mCurrentAngle > 95 && mCurrentAngle < 265) || (mCurrentAngle > 275) || (mCurrentAngle < 85)) {
						stopScanning();
					}
				}
			}
			
		};
		
		mOrientationEventListener.enable();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		if (mMediaPlayer != null) {
			mMediaPlayer.release();
		}
		
		if (mOrientationEventListener != null) {
			mOrientationEventListener.disable();
		}
	}
}
