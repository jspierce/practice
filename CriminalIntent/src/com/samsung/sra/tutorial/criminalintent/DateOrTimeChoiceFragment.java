package com.samsung.sra.tutorial.criminalintent;

import java.util.Date;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

public class DateOrTimeChoiceFragment extends DialogFragment {
	private static final String DIALOG_DATE = "date";
	private static final String DIALOG_TIME = "time";
	
	private Date mDate;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		mDate = (Date) getArguments().getSerializable(CrimeFragment.EXTRA_DATE);
		
				
		return new AlertDialog.Builder(getActivity())
			.setTitle(R.string.choose_date_or_time)
			.setNegativeButton(R.string.date_choice, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					FragmentManager fm = getActivity().getSupportFragmentManager();
					DatePickerFragment dateDialog = DatePickerFragment.newInstance(mDate);
					dateDialog.setTargetFragment(getTargetFragment(), getTargetRequestCode());
					dateDialog.show(fm, DIALOG_DATE);
				}
			})
			.setPositiveButton(R.string.time_choice, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					FragmentManager fm = getActivity().getSupportFragmentManager();
					TimePickerFragment timeDialog = TimePickerFragment.newInstance(mDate);
					timeDialog.setTargetFragment(getTargetFragment(), getTargetRequestCode());
					timeDialog.show(fm, DIALOG_TIME);
					
				}
			})
			.create();
	}
	
	public static DateOrTimeChoiceFragment newInstance(Date date) {
		Bundle args = new Bundle();
		args.putSerializable(CrimeFragment.EXTRA_DATE, date);
		DateOrTimeChoiceFragment fragment = new DateOrTimeChoiceFragment();
		fragment.setArguments(args);
		
		return fragment;
	}
	
}
