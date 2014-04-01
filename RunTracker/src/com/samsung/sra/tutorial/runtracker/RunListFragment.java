package com.samsung.sra.tutorial.runtracker;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class RunListFragment extends ListFragment implements LoaderCallbacks<Cursor> {
	private static final String TAG = "ListFragment";
	private static final int REQUEST_NEW_RUN = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
			
		// Initialize the loader to load the list of runs
		getLoaderManager().initLoader(0, null, this);
	}
	
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.run_list_options, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_item_new_run:
				Intent i = new Intent(getActivity(), RunActivity.class);
				startActivityForResult(i, REQUEST_NEW_RUN);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// The id argument will be the Run ID; CursorAdapter provides this for free
		Intent i = new Intent(getActivity(), RunActivity.class);
		i.putExtra(RunActivity.EXTRA_RUN_ID, id);
		startActivity(i);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (REQUEST_NEW_RUN == requestCode) {
			// Restart the loader to get any new run available
			getLoaderManager().restartLoader(0, null, this);
		}
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Log.d(TAG, "Creating loader");
		
		// We only load the runs, so assume that's what we're doing
		return new RunListCursorLoader(getActivity());
	}
	
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		Log.d(TAG, "Load finished");
		
		// Create an adapter to point at this cursor
		RunCursorAdapter adapter = new RunCursorAdapter(getActivity(), (RunDatabaseHelper.RunCursor) cursor);
		setListAdapter(adapter);
	}
	
	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		Log.d(TAG, "Reseting loader");
		
		// Stop using the cursor (via the adapter)
		setListAdapter(null);
	}
	
	private class RunCursorAdapter extends CursorAdapter {
		private RunDatabaseHelper.RunCursor mRunCursor;
		
		public RunCursorAdapter(Context context, RunDatabaseHelper.RunCursor cursor) {
			super(context, cursor, 0);
			mRunCursor = cursor;
		}
		
		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			// Use a layout inflater to get a row view
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			return inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
		}
		
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			// Get the run for the current row
			Run run = mRunCursor.getRun();
			
			// Set up the start date text view
			TextView startDateTextView = (TextView) view;
			String cellText = context.getString(R.string.cell_text, run.getStartDate());
			startDateTextView.setText(cellText);
			
			// Visually distinguish the run if it's currently being tracked
			if (RunManager.get(getActivity()).isTrackingRun(run)) {
				startDateTextView.setTextColor(Color.parseColor("#0000ff"));
			} else {
				startDateTextView.setTextColor(Color.parseColor("#000000"));
			}
		}
	}
	
	private static class RunListCursorLoader extends SQLiteCursorLoader {
		public RunListCursorLoader(Context context) {
			super(context);
		}
		
		@Override
		protected Cursor loadCursor() {
			Log.d(TAG, "Attempting to load the cursor");
			
			// Query the list of runs
			return RunManager.get(getContext()).queryRuns();
		}
	}
}
