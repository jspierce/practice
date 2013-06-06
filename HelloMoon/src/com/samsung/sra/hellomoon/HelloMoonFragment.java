package com.samsung.sra.hellomoon;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class HelloMoonFragment extends Fragment {
	private AudioPlayer mPlayer = new AudioPlayer();
	private Button mPlayPauseButton;
	private Boolean mPaused = true;
	private Button mStopButton;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_hello_moon, parent, false);
		
		mPlayPauseButton = (Button) v.findViewById(R.id.hellomoon_playPauseButton);
		mPlayPauseButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mPaused) {
					mPlayPauseButton.setText(R.string.hellomoon_pause);
					mPlayer.play(getActivity());
					mPaused = false;
				} else {
					mPlayPauseButton.setText(R.string.hellomoon_play);
					mPlayer.pause();
					mPaused = true;
				}
					
			}
		});
		
		mStopButton = (Button) v.findViewById(R.id.hellomoon_stopButton);
		mStopButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mPlayer.stop();
				mPlayPauseButton.setText(R.string.hellomoon_play);
				mPaused = true;
			}
		});
		
		
		return v;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mPlayer.stop();
	}
}
