package com.samsung.sra.tutorial.criminalintent;

import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class CrimeFragment extends Fragment {

	private Crime mCrime;
	private EditText mTitleField;
	private Button mDateButton;
	private Button mTimeButton;
	private CheckBox mSolvedCheckBox;
	
	public static final String EXTRA_CRIME_ID = "com.samsung.sra.tutorial.criminalintent.crime_id";
	private static final String DIALOG_DATE = "date";
	private static final String DIALOG_TIME = "time";
	private static final int REQUEST_DATE = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		UUID crimeId = (UUID) getArguments().getSerializable(EXTRA_CRIME_ID);
		
		mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
	}
	
	public static CrimeFragment newInstance(UUID crimeId) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_CRIME_ID, crimeId);
		
		CrimeFragment fragment = new CrimeFragment();
		fragment.setArguments(args);
		
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_crime, parent, false);
		
		mTitleField = (EditText) v.findViewById(R.id.crime_title);
		mTitleField.setText(mCrime.getTitle());
		
		// Set the cursor position at the end of the text
		int position = mTitleField.length();
		Editable titleText = mTitleField.getText();
		Selection.setSelection(titleText, position);
		
		mTitleField.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence c, int start, int before, int count) {
				mCrime.setTitle(c.toString());
			}
			
			public void beforeTextChanged(CharSequence c, int start, int count, int after) {
				// This space intentionally left blank
			}
			
			public void afterTextChanged(Editable c) {
				// This one too
			}
		});
		
		mDateButton = (Button) v.findViewById(R.id.crime_date);
		mDateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager fm = getActivity().getSupportFragmentManager();
				DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
				dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
				dialog.show(fm, DIALOG_DATE);
			}
		});
		
		mTimeButton = (Button) v.findViewById(R.id.crime_time);
		mTimeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager fm = getActivity().getSupportFragmentManager();
				TimePickerFragment dialog = TimePickerFragment.newInstance(mCrime.getDate());
				dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
				dialog.show(fm, DIALOG_TIME);
			}
		});
		updateDateAndTime();

		mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
		mSolvedCheckBox.setChecked(mCrime.isSolved());
		mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mCrime.setSolved(isChecked);
			}
		});
		
		return v;
	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK)
			return;
		
		if (requestCode == REQUEST_DATE) {
			Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
			mCrime.setDate(date);
			updateDateAndTime();
		}
	}
	
	private void updateDateAndTime() {
		Date date = mCrime.getDate();
		mDateButton.setText(DateFormat.format("EEEE, MMMM dd, yyyy", date));
		mTimeButton.setText(DateFormat.format("h:mm aa", date));
	}
}
