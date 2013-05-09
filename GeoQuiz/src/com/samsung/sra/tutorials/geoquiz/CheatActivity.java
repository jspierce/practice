package com.samsung.sra.tutorials.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends Activity {

	public static final String EXTRA_ANSWER_IS_TRUE = "com.samsung.sra.tutorials.geoquiz.answer_is_true";
	public static final String EXTRA_ANSWER_SHOWN = "com.samsung.sra.tutorials.geoquiz.answer_shown";
	
	private static final String USER_CHEATED = "user_cheated";
	
	private boolean mAnswerIsTrue;
	private boolean mUserCheated = false;
	private TextView mAnswerTextView;
	private Button mShowAnswer;
	private TextView mVersionNumberTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cheat);
		
		mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);
		mAnswerTextView = (TextView) findViewById(R.id.answerTextView);
		
		mShowAnswer = (Button) findViewById(R.id.showAnswerButton);
		mShowAnswer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				displayAnswer();
				mUserCheated = true;
				setAnswerShownResult(true);
			}
		});
		
		mVersionNumberTextView = (TextView) findViewById(R.id.versionNumberTextView);
		mVersionNumberTextView.setText("API Level " + Build.VERSION.SDK_INT);
		
		// See if the user has already cheated
		if (savedInstanceState != null) {
			mUserCheated = savedInstanceState.getBoolean(USER_CHEATED, false);
			if (mUserCheated) {
				displayAnswer();
			}
        }
		
		setAnswerShownResult(mUserCheated);
	}

	private void displayAnswer() {
		int answer = mAnswerIsTrue ? R.string.true_button : R.string.false_button;
		mAnswerTextView.setText(answer);
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		
		savedInstanceState.putBoolean(USER_CHEATED, mUserCheated);
	}
	
	
	private void setAnswerShownResult(boolean isAnswerShown) {
		Intent data = new Intent();
		data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
		setResult(RESULT_OK, data);
	}
}
