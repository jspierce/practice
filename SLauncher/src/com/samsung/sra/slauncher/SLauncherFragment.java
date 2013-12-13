package com.samsung.sra.slauncher;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SLauncherFragment extends ListFragment {
	private static final String TAG = "SLauncherFragment";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		
		Intent startupIntent = new Intent(Intent.ACTION_MAIN);
		startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		
		PackageManager pm = getActivity().getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(startupIntent, 0);
		
		Log.i(TAG, "I've found " + activities.size() + " activities.");
		
		// Sort the activity labels
		Collections.sort(activities, new Comparator<ResolveInfo>() {
			public int compare(ResolveInfo a, ResolveInfo b) {
				PackageManager pm = getActivity().getPackageManager();
				return String.CASE_INSENSITIVE_ORDER.compare(a.loadLabel(pm).toString(), b.loadLabel(pm).toString());
			}
		});
		
		// Create an ArrayAdapter that creates simple list item views for the activity labels
		ArrayAdapter<ResolveInfo> adapter = new ArrayAdapter<ResolveInfo>(getActivity(), R.layout.list_item_launcher, R.id.activityLabel, activities) {
			public View getView(int pos, View convertView, ViewGroup parent) {
				View v = super.getView(pos, convertView, parent);
				PackageManager pm = getActivity().getPackageManager();
				
				// Set the list item's text view to include the activity label
				TextView tv = (TextView) v.findViewById(R.id.activityLabel);
				ResolveInfo ri = getItem(pos);
				tv.setText(ri.loadLabel(pm));
				
				// Set the list item's image view to include the activity icon
				ImageView iv = (ImageView) v.findViewById(R.id.activityIcon);
				iv.setImageDrawable(ri.loadIcon(pm));
				
				// Return the view
				return v;
			}
		};
		
		// Then set the list adapter to be it
		setListAdapter(adapter);
	}
	
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		ResolveInfo resolveInfo = (ResolveInfo) l.getAdapter().getItem(position);
		ActivityInfo activityInfo = resolveInfo.activityInfo;
		
		// If the user didn't select anything just return
		if (activityInfo == null) return;
		
		// Create an explicit Intent to launch the selected activity
		Intent i = new Intent(Intent.ACTION_MAIN);
		i.setClassName(activityInfo.applicationInfo.packageName, activityInfo.name);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(i);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_slauncher, menu);		
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home :
				if (NavUtils.getParentActivityIntent(getActivity()) != null) {
					NavUtils.navigateUpFromSameTask(getActivity());
				}
				return true;
				
			case R.id.menu_item_show_tasks:
				Log.d(TAG, "Show tasks");
				
				// Start an instance of STasksActivity
				Intent i = new Intent(getActivity(), STasksActivity.class);
				startActivity(i);
				
				return true;
				
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
