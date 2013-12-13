package com.samsung.sra.slauncher;

import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class STasksFragment extends ListFragment {
	private static final String TAG = "STasksFragment";
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if (NavUtils.getParentActivityName(getActivity()) != null)
				getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		
		ActivityManager manager = (ActivityManager) getActivity().getSystemService(Activity.ACTIVITY_SERVICE);
		List<RecentTaskInfo> tasks = manager.getRecentTasks(20, ActivityManager.RECENT_IGNORE_UNAVAILABLE);
		
		Log.i(TAG, "I've found " + tasks.size() + " activities.");
		
		PackageManager pm = getActivity().getPackageManager();
		for (int i = 0; i < tasks.size(); i++) {
			ResolveInfo info = pm.resolveActivity(tasks.get(i).baseIntent, PackageManager.MATCH_DEFAULT_ONLY);
			Log.d(TAG, info.loadLabel(pm).toString());
		}
		
		// Create an ArrayAdapter that creates simple list item views for the activity labels
		ArrayAdapter<RecentTaskInfo> adapter = new ArrayAdapter<RecentTaskInfo>(getActivity(), android.R.layout.simple_list_item_1, tasks) {
			public View getView(int pos, View convertView, ViewGroup parent) {
				View v = super.getView(pos, convertView, parent);
				PackageManager pm = getActivity().getPackageManager();
				
				// simple_list_item_1 is a TextView, so cast it to set its text value
				TextView tv = (TextView) v;
				RecentTaskInfo taskInfo = getItem(pos);
				ResolveInfo info = pm.resolveActivity(taskInfo.baseIntent, PackageManager.MATCH_DEFAULT_ONLY);
				tv.setText(info.loadLabel(pm).toString());
				
				// Return the view
				return v;
			}
		};
		
		// Then set the list adapter to be it
		setListAdapter(adapter);
	}
	
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		RecentTaskInfo taskInfo = (RecentTaskInfo) l.getAdapter().getItem(position);
		ActivityManager manager = (ActivityManager) getActivity().getSystemService(Activity.ACTIVITY_SERVICE);
		
		// If the user didn't select anything just return
		if (taskInfo == null) return;
		
		// Move the selected task to the front
		manager.moveTaskToFront(taskInfo.id, ActivityManager.MOVE_TASK_WITH_HOME);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home :
				if (NavUtils.getParentActivityIntent(getActivity()) != null) {
					NavUtils.navigateUpFromSameTask(getActivity());
				}
				return true;
								
			default:
				return super.onOptionsItemSelected(item);
		}
	}

}
