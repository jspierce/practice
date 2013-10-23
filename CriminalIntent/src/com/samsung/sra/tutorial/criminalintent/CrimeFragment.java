package com.samsung.sra.tutorial.criminalintent;

import java.util.Date;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class CrimeFragment extends Fragment {
	private Crime mCrime;
	private EditText mTitleField;
	private Button mDateButton;
	private CheckBox mSolvedCheckBox;
	private ImageButton mPhotoButton;
	private ImageView mPhotoView;
	private Button mSuspectButton;
	
	//private static final String TAG = "CrimeFragment";
	public static final String EXTRA_CRIME_ID = "com.samsung.sra.tutorial.criminalintent.crime_id";
	private static final String DIALOG_DATE_TIME_CHOICE = "date time choice";
	private static final String DIALOG_IMAGE = "image";
	private static final int REQUEST_DATE = 0;
	public static final String EXTRA_DATE = "com.samsung.sra.criminalintent.date";
	private static final int REQUEST_PHOTO = 1;
	private static final int REQUEST_CONTACT = 2;
	
	private ActionMode mActionMode;
	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

	    // Called when the action mode is created; startActionMode() was called
	    @Override
	    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
	        // Inflate a menu resource providing context menu items
	        MenuInflater inflater = mode.getMenuInflater();
	        inflater.inflate(R.menu.crime_picture_context, menu);
	        return true;
	    }

	    // Called each time the action mode is shown. Always called after onCreateActionMode, but
	    // may be called multiple times if the mode is invalidated.
	    @Override
	    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
	        return false; // Return false if nothing is done
	    }

	    // Called when the user selects a contextual menu item
	    @Override
	    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
	        switch (item.getItemId()) {
	            case R.id.menu_item_delete_photo:
	                // Delete the photo
	            	mCrime.setPhoto(null, getActivity());
	            	showPhoto();
	            	
	                mode.finish(); // Action picked, so close the CAB
	                return true;
	            default:
	                return false;
	        }
	    }

	    // Called when the user exits the action mode
	    @Override
	    public void onDestroyActionMode(ActionMode mode) {
	        mActionMode = null;
	    }
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true);
		
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
	
	@TargetApi(11)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_crime, parent, false);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if (NavUtils.getParentActivityName(getActivity()) != null)
				getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		
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
				DateOrTimeChoiceFragment dialog = DateOrTimeChoiceFragment.newInstance(mCrime.getDate());
				dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
				dialog.show(fm, DIALOG_DATE_TIME_CHOICE);
			}
		});
		updateDate();
		

		mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
		mSolvedCheckBox.setChecked(mCrime.isSolved());
		mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mCrime.setSolved(isChecked);
			}
		});
		
		mPhotoButton = (ImageButton) v.findViewById(R.id.crime_imageButton);
		mPhotoButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), CrimeCameraActivity.class);
				startActivityForResult(i, REQUEST_PHOTO);
			}
		});
		
		// If there isn't a camera available, disable the camera functionality
		PackageManager pm = getActivity().getPackageManager();
		if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) && (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT))) {
			mPhotoButton.setEnabled(false);
		}
		
		mPhotoView = (ImageView) v.findViewById(R.id.crime_imageView);
		mPhotoView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Photo p = mCrime.getPhoto();
				if (p == null)
					return;
				
				FragmentManager fm = getActivity().getSupportFragmentManager();
				String path = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
				ImageFragment.newInstance(path, p.getRotation()).show(fm, DIALOG_IMAGE);
			}
		});
		
		//mPhotoView.setLongClickable(true);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			mPhotoView.setOnLongClickListener(new View.OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					if (mActionMode != null) {
			            return false;
			        }

			        // Start the CAB using the ActionMode.Callback defined above
			        mActionMode = getActivity().startActionMode(mActionModeCallback);
			        mPhotoView.setSelected(true);
			        return true;
				}
			});
		}
		
		Button reportButton = (Button) v.findViewById(R.id.reportButton);
		reportButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("text/plain");
				i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
				i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
				
				// Force the OS to show a chooser for the activity (if there is more than one choice)
				i = Intent.createChooser(i, getString(R.string.send_report));
				
				startActivity(i);
			}
		});
		
		mSuspectButton = (Button) v.findViewById(R.id.suspect_button);
		mSuspectButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(i, REQUEST_CONTACT);
			}
		});
		
		if (mCrime.getSuspect() != null)
			mSuspectButton.setText(mCrime.getSuspect());
		
		return v;
	}
	
	private void showPhoto() {
		// (Re)set the image button's image based on our photo
		Photo p = mCrime.getPhoto();
		BitmapDrawable b = null;
		if (p != null) {
			String path = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
			b = PictureUtils.getScaledDrawable(getActivity(), path, p.getRotation());
			
			PictureUtils.calculateRotation(path);
		}
		mPhotoView.setImageDrawable(b);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_crime, menu);		
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home :
				if (NavUtils.getParentActivityIntent(getActivity()) != null) {
					NavUtils.navigateUpFromSameTask(getActivity());
				}
				return true;
				
			case R.id.menu_item_delete_crime:
				CrimeLab.get(getActivity()).deleteCrime(mCrime);
				
				// Pop back up to the previous view
				if (NavUtils.getParentActivityIntent(getActivity()) != null) {
					NavUtils.navigateUpFromSameTask(getActivity());
				}
				
				return true;
				
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		showPhoto();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		CrimeLab.get(getActivity()).saveCrimes();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		PictureUtils.cleanImageView(mPhotoView);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK)
			return;
		
		if (requestCode == REQUEST_DATE) {
			Date date = (Date) data.getSerializableExtra(EXTRA_DATE);
			mCrime.setDate(date);
			updateDate();
		} else if (requestCode == REQUEST_PHOTO) {
			// Create a new Photo object and attach it to the crime
			String filename = data.getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
			if (filename != null) {
				String path = getActivity().getFileStreamPath(filename).getAbsolutePath();
				int rotation = PictureUtils.calculateRotation(path);
				
				// See if we already have a photo; if so, delete it
				
				Photo p = new Photo(filename, rotation);
				mCrime.setPhoto(p, getActivity());
				showPhoto();
				//Log.i(TAG, "Crime " + mCrime.getTitle() + " has photo " + filename);
			}
		} else if (requestCode == REQUEST_CONTACT) {
			Uri contactUri = data.getData();
			
			// Specify which fields we want the query to return values for
			String [] queryFields = new String[] { ContactsContract.Contacts.DISPLAY_NAME };
			
			// Perform the query - the contactUri is like a "where" clause here
			Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);
			
			// Double-check that we actually got results
			if (c.getCount() == 0) {
				c.close();
				return;
			}
			
			// Pull out the first column of the first row of data - this is the suspect's name
			c.moveToFirst();
			String suspect = c.getString(0);
			mCrime.setSuspect(suspect);
			mSuspectButton.setText(suspect);
			c.close();
		}
	}
	
	private void updateDate() {
		mDateButton.setText(DateFormat.format("EEEE, MMMM dd, yyyy", mCrime.getDate()));
	}
	
	private String getCrimeReport() {
		String solvedString = null;
		if (mCrime.isSolved()) {
			solvedString = getString(R.string.crime_report_solved);
		} else {
			solvedString = getString(R.string.crime_report_unsolved);
		}
		
		String dateFormat = "EEE, MMM dd";
		String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();
		
		String suspect = mCrime.getSuspect();
		if (suspect == null)
			suspect = getString(R.string.crime_report_no_suspect);
		else
			suspect = getString(R.string.crime_report_subject, suspect);
		
		String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);
		
		return report;
	}
}
