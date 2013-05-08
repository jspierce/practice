package com.samsung.sra.tutorials.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends Activity {

	public static final String EXTRA_ANSWER_IS_TRUE = "com.samsung.sra.tutorials.geoquiz.answer_is_true";
	public static final String EXTRA_ANSWER_SHOWN = "com.samsung.sra.tutorials.geoquiz.answer_shown";
	
	private boolean mAnswerIsTrue;
	private TextView mAnswerTextView;
	private Button mShowAnswer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cheat);
		
		// By default we don't show the user the answer
		setAnswerShownResult(false);
		
		mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);
		
		mAnswerTextView = (TextView) findViewById(R.id.answerTextView);
		
		mShowAnswer = (Button) findViewById(R.id.showAnswerButton);
		mShowAnswer.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int answer = mAnswerIsTrue ? R.string.true_button : R.string.false_button;	
				mAnswerTextView.setText(answer);
				setAnswerShownResult(true);
			}
		});
	}
	
	private void setAnswerShownResult(boolean isAnswerShown) {
		Intent data = new Intent();
		data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
		setResult(RESULT_OK, data);
	}
}
