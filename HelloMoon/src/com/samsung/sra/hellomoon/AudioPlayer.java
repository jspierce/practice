package com.samsung.sra.hellomoon;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.widget.Button;

public class AudioPlayer {
	private MediaPlayer mPlayer;
	private Button mPlayPauseButton;
	private int mButtonLabelID;
	private boolean mPlaying = false;
	
	public void setPlayButtonAndLabel(Button button, int resourceID) {
		mPlayPauseButton = button;
		mButtonLabelID = resourceID;
	}
	
	public void stop() {
		if (mPlayer != null) {
			mPlayer.release();
			mPlayer = null;
		}
		mPlaying = false;
	}
	
	public void play(Context c) {
		if (mPlayer == null) {
			mPlayer = MediaPlayer.create(c, R.raw.one_small_step);
			mPlayer.setOnCompletionListener(new OnCompletionListener() {
				
				@Override
				public void onCompletion(MediaPlayer mp) {
					stop();
					mPlaying = false;
					mPlayPauseButton.setText(mButtonLabelID);
					// This is a total hack to reset the play button to pause
					
				}
			});
		}
		
		mPlayer.start();
		mPlaying = true;
	}
	
	public void pause() {
		mPlayer.pause();
		mPlaying = false;
	}
	
	public boolean isPlaying() {
		return mPlaying;
	}
}
