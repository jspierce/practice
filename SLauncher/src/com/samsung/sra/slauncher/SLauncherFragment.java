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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SLauncherFragment extends ListFragment {
	private static final String TAG = "SLauncherFragment";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
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
		ArrayAdapter<ResolveInfo> adapter = new ArrayAdapter<ResolveInfo>(getActivity(), android.R.layout.simple_list_item_1, activities) {
			public View getView(int pos, View concertView, ViewGroup parent) {
				View v = super.getView(pos, concertView, parent);
				PackageManager pm = getActivity().getPackageManager();
				
				// Documentation says that simple_list_item_1 is a TextView, so cast it so that we can set the text value
				TextView tv = (TextView) v;
				ResolveInfo ri = getItem(pos);
				tv.setText(ri.loadLabel(pm));
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
		
		startActivity(i);
	}
}
