package com.samsung.sra.hellomoon;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class AudioPlayer {
	private MediaPlayer mPlayer;
	
	public void stop() {
		if (mPlayer != null) {
			mPlayer.release();
			mPlayer = null;
		}
	}
	
	public void play(Context c) {
		if (mPlayer == null) {
			mPlayer = MediaPlayer.create(c, R.raw.one_small_step);
			mPlayer.setOnCompletionListener(new OnCompletionListener() {
				
				@Override
				public void onCompletion(MediaPlayer mp) {
					stop();
				}
			});
		}
		
		mPlayer.start();
	}
	
	public void pause() {
		mPlayer.pause();
	}
}
