package com.samsung.sra.tutorial.photogallery;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;


public class PhotoGalleryFragment extends Fragment {
	private static final String TAG = "PhotoGalleryFragment";
	GridView mGridView;
	ArrayList<GalleryItem> mItems;
	ThumbnailDownloader<ImageView> mThumbnailThread;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setRetainInstance(true);
		setHasOptionsMenu(true);
		
		updateItems();
		
		Intent i = new Intent(getActivity(), PollService.class);
		getActivity().startService(i);
		
		mThumbnailThread = new ThumbnailDownloader<ImageView>(new Handler());
		mThumbnailThread.setListener(new ThumbnailDownloader.Listener<ImageView>() {
			public void onThumbnailDownloaded(ImageView imageView, Bitmap thumbnail) {
				if (isVisible()) {
					imageView.setImageBitmap(thumbnail);
				}
			}
		});
		mThumbnailThread.start();
		mThumbnailThread.getLooper();
		Log.i(TAG, "Background thread started");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
		
		mGridView = (GridView) v.findViewById(R.id.gridView);
		setupAdapter();
		
		return v;
	}
		
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		mThumbnailThread.quit();
		Log.i(TAG, "Background thread destroyed");
	}
	
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mThumbnailThread.clearQueue();
	}
	
	@TargetApi(11)
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_photo_gallery, menu);
		
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
			// Pull out the SearchView
			MenuItem searchItem = menu.findItem(R.id.menu_item_search);
			//SearchView searchView = (SearchView) searchItem.getActionView();
			SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
			
			// Get the data from searchable.xml as a SearchableInfo instance and hand it to the search view
			SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
			ComponentName name = getActivity().getComponentName();
			SearchableInfo searchInfo = searchManager.getSearchableInfo(name);
			searchView.setSearchableInfo(searchInfo);
			
			// Now set a listener so we can detect when the user closes the search view		
			searchView.setOnCloseListener(new SearchView.OnCloseListener() {
				
				@Override
				public boolean onClose() {
					Activity activity = getActivity();
					String query = PreferenceManager.getDefaultSharedPreferences(activity).getString(FlickrFetchr.PREF_SEARCH_QUERY, null);
					if (query != null) {
						PreferenceManager.getDefaultSharedPreferences(activity)
							.edit()
							.putString(FlickrFetchr.PREF_SEARCH_QUERY, null)
							.commit();
						updateItems();
					}
					
					return false;
				}
			});
			
			// And hide the menu close button, since the search view provides one
			MenuItem searchClearItem = menu.findItem(R.id.menu_item_clear);
			searchClearItem.setVisible(false);
		}
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_item_search:
				getActivity().onSearchRequested();
				return true;
			case R.id.menu_item_clear:
				PreferenceManager.getDefaultSharedPreferences(getActivity())
					.edit()
					.putString(FlickrFetchr.PREF_SEARCH_QUERY, null)
					.commit();
				updateItems();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	
	private void setupAdapter() {
		if (getActivity() == null || mGridView == null)
			return;
		
		if (mItems != null) {
			mGridView.setAdapter(new GalleryItemAdapter(mItems));
		} else {
			mGridView.setAdapter(null);
		}
	}
	
	
	public void updateItems() {
		new FetchItemsTask().execute();
	}
	
	
	private class FetchItemsTask extends AsyncTask<Void, Void, ArrayList<GalleryItem>> {
		@Override
		protected ArrayList<GalleryItem> doInBackground(Void... params) {
			Activity activity = getActivity();
			
			if (activity == null)
				return new ArrayList<GalleryItem>();
			
			String query = PreferenceManager.getDefaultSharedPreferences(activity).getString(FlickrFetchr.PREF_SEARCH_QUERY, null);
			if (query != null) {
				return new FlickrFetchr(getActivity()).search(query);
			} else {
				return new FlickrFetchr().fetchItems();
			}
		}
		
		@Override
		protected void onPostExecute(ArrayList<GalleryItem> items) {
			mItems = items;
						
			setupAdapter();
		}
	}
	
	
	protected class ToastMessageTask extends AsyncTask<String, String, String> {

	    @Override
	    protected String doInBackground(String... params) {
	        return params[0];
	    }


	    protected void onPostExecute(String result){
	           Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
	    }
	}
	
	    
	private class GalleryItemAdapter extends ArrayAdapter<GalleryItem> {
		public GalleryItemAdapter(ArrayList<GalleryItem> items) {
			super(getActivity(), 0, items);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.gallery_item, parent, false);
			}
			
			ImageView imageView = (ImageView) convertView.findViewById(R.id.gallery_item_imageView);
			imageView.setImageResource(R.drawable.placeholder_image);
			
			GalleryItem item = getItem(position);
			
			String url = item.getUrl();
			if (url != null)
				mThumbnailThread.queueThumbnail(imageView, item.getUrl());
			
			return convertView;
		}
	}
}
