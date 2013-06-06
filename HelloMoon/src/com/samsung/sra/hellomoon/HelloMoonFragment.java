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
	private Button mStopButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_hello_moon, parent, false);
		
		mPlayPauseButton = (Button) v.findViewById(R.id.hellomoon_playPauseButton);
		mPlayer.setPlayButtonAndLabel(mPlayPauseButton, R.string.hellomoon_play);
		
		if (mPlayer.isPlaying())
			mPlayPauseButton.setText(R.string.hellomoon_pause);
		
		mPlayPauseButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!mPlayer.isPlaying()) {
					mPlayer.play(getActivity());
					mPlayPauseButton.setText(R.string.hellomoon_pause);
				} else {
					mPlayer.pause();
					mPlayPauseButton.setText(R.string.hellomoon_play);
				}
					
			}
		});
		
		mStopButton = (Button) v.findViewById(R.id.hellomoon_stopButton);
		mStopButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mPlayer.stop();
				mPlayPauseButton.setText(R.string.hellomoon_play);
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
