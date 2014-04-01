package com.samsung.sra.fun.braininput;

import java.util.Random;

import android.app.Fragment;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class IntentionFragment extends Fragment {
	private static final String TAG = "IntentionFragment";
	
	private MediaPlayer mMediaPlayer;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_intention, container, false);
		
		Resources res = getResources();
		String[] candidateIntentions = res.getStringArray(R.array.candidate_intentions_array);
		
		Random randomGenerator = new Random();
		int index = randomGenerator.nextInt(candidateIntentions.length);
		
		Log.d(TAG, "Intention: " + candidateIntentions[index]);
		
		TextView intentionText = (TextView) v.findViewById(R.id.intention_text);
		intentionText.setText(candidateIntentions[index]);
		
		Button stashButton = (Button) v.findViewById(R.id.stash_button);
		stashButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().finish();
			}
			
		});
		
		return v;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		mMediaPlayer = MediaPlayer.create(getActivity(), R.raw.beep);
		
		// Play a beep as if we recognized an intention
		if (mMediaPlayer != null)
			mMediaPlayer.start();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		if (mMediaPlayer != null) {
			mMediaPlayer.release();
		}
	}

	
}
