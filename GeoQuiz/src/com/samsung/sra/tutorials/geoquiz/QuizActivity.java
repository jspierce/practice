package com.samsung.sra.tutorials.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends Activity {

	private Button mTrueButton;
	private Button mFalseButton;
	private Button mCheatButton;
	private ImageButton mNextButton;
	private ImageButton mPrevButton;
	private TextView mQuestionTextView;
	
	private static final String TAG = "QuizActivity";
	private static final String KEY_INDEX = "index";
	private static final String KEY_CHEATED = "cheated";
	
	private TrueFalse[] mQuestionBank = new TrueFalse[] {
			new TrueFalse(R.string.question_oceans, true),
			new TrueFalse(R.string.question_mideast, false),
			new TrueFalse(R.string.question_africa, false),
			new TrueFalse(R.string.question_americas, true),
			new TrueFalse(R.string.question_asia, true)
	};
	
	private boolean[] mCheatedValues = new boolean[] { false, false, false, false, false};
	
	private int mCurrentIndex = 0;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "OnCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);
        
        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
        		updateQuestion();
			}
		});
        
        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		checkAnswer(true);
        	}
        });
        
        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		checkAnswer(false);
        	}
        });
        
        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(QuizActivity.this, CheatActivity.class);
				boolean answerIsTrue = mQuestionBank[mCurrentIndex].isTrueQuestion();
				i.putExtra(CheatActivity.EXTRA_ANSWER_IS_TRUE, answerIsTrue);
				
				//startActivity(i);
				startActivityForResult(i, 0);
			}
		});
        
        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
         		updateQuestion();
        	}
        });
        
        mPrevButton = (ImageButton) findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mCurrentIndex = mCurrentIndex - 1;
				if (mCurrentIndex < 0)
					mCurrentIndex = mQuestionBank.length - 1;
				
        		updateQuestion();
			}
		});
        
        if (savedInstanceState != null) {
        	mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
        	mCheatedValues = savedInstanceState.getBooleanArray(KEY_CHEATED);
        }
        updateQuestion();
    }

    @Override
    public void onStart() {
    	super.onStart();
    	Log.d(TAG, "onStart() called");
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	Log.d(TAG, "onPause() called");
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	Log.d(TAG, "onResume() called");
    }
    
    @Override
    public void onStop() {
    	super.onStop();
    	Log.d(TAG, "onStop() called");
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	Log.d(TAG, "onDestroy() called");
    }
    
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
    	super.onSaveInstanceState(savedInstanceState);
    	
    	Log.i(TAG, "onSaveInstanceState");
    	savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
    	savedInstanceState.putBooleanArray(KEY_CHEATED, mCheatedValues);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (data == null)
    		return;
    	
    	mCheatedValues[mCurrentIndex] = data.getBooleanExtra(CheatActivity.EXTRA_ANSWER_SHOWN, false);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.quiz, menu);
        return true;
    }


	private void updateQuestion() {
		int question = mQuestionBank[mCurrentIndex].getQuestion();
		mQuestionTextView.setText(question);
	}
    
	private void checkAnswer(boolean userPressedTrue) {
		boolean answerIsTrue = mQuestionBank[mCurrentIndex].isTrueQuestion();
		
		int messageResId = 0;
		
		if (mCheatedValues[mCurrentIndex]) {
			messageResId = R.string.judgement_toast;
		} else {
			if (userPressedTrue == answerIsTrue)
				messageResId = R.string.correct_toast;
			else
				messageResId = R.string.incorrect_toast;
		}
		
		Toast.makeText(QuizActivity.this, messageResId, Toast.LENGTH_SHORT).show();
	}
}
