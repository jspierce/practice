package com.samsung.sra.tutorial.photogallery;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class FlickrFetchr {
	private static final String TAG = "FlickrFetchr";
	public static final String PREF_SEARCH_QUERY = "searchQuery";
	public static final String PREF_LAST_RESULT_ID = "lastResultID";
	
	private static final String ENDPOINT = "https://api.flickr.com/services/rest/";
	private static final String AppProInt_Code = "93a7f095d43d5ae04d828284e0ac3a25";
	private static final String METHOD_GET_RECENT = "flickr.photos.getRecent";
	private static final String METHOD_SEARCH = "flickr.photos.search";
	private static final String PARAM_EXTRAS = "extras";
	private static final String EXTRA_SMALL_URL = "url_s";
	private static final String PARAM_TEXT = "text";
	
	private static final String XML_PHOTO = "photo";
	private static final String XML_PHOTOS = "photos";
	
	private Activity mToastActivity;
	
	public FlickrFetchr(Activity activity) {
		mToastActivity = activity;
	}
	
	public FlickrFetchr() {
		mToastActivity = null;
	}
	
	protected byte[] getUrlBytes(String urlSpec) throws IOException {
		URL url = new URL(urlSpec);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			InputStream in = connection.getInputStream();
				
			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				return null;
			}
				
			int bytesRead = 0;
			byte[] buffer = new byte[1024];
			while ((bytesRead = in.read(buffer)) > 0) {
				out.write(buffer, 0, bytesRead);
			}
				
			out.close();
			return out.toByteArray();
		} finally {
			connection.disconnect();
		}
	}
		
	public String getUrl(String urlSpec) throws IOException {
		return new String(getUrlBytes(urlSpec));
	}
	
	private void parseItems(ArrayList<GalleryItem> items, XmlPullParser parser) throws XmlPullParserException, IOException {
		int eventType = parser.next();
		
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.START_TAG) {
				String tagName = parser.getName();
				if (XML_PHOTO.equals(tagName)) {
					// If this is a photo get its attributes
					String id = parser.getAttributeValue(null, "id");
					String caption = parser.getAttributeValue(null, "title");
					String smallUrl = parser.getAttributeValue(null, EXTRA_SMALL_URL);
					
					GalleryItem item = new GalleryItem();
					item.setId(id);
					item.setCaption(caption);
					item.setUrl(smallUrl);
					items.add(item);
				} else if (XML_PHOTOS.equals(tagName)) {
					// If it's information about the photos and we have an activity, generate a Toast on the UI thread showing the number of total results
					// <photos page="1" pages="34558" perpage="100" total="3455794">
					final String totalCount = parser.getAttributeValue(null, "total");
					if (mToastActivity != null) {
						mToastActivity.runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(mToastActivity, totalCount + " photos found", Toast.LENGTH_SHORT).show();
							}
						});
					}
				}		
			}
			
			eventType = parser.next();
		}
	}
	
	public ArrayList<GalleryItem> downloadGalleryItems(String url) {
		ArrayList<GalleryItem> items = new ArrayList<GalleryItem>();
		
		try {
			String xmlString = getUrl(url);
			Log.i(TAG, "Received xml: " + xmlString);
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(new StringReader(xmlString));
			
			parseItems(items, parser);
		} catch (IOException ioe) {
			Log.e(TAG, "Failed to fetch items", ioe);
		} catch (XmlPullParserException xppe) {
			Log.e(TAG, "Failed to parse items", xppe);
		}
		
		return items;
	}
	
	
	public ArrayList<GalleryItem> fetchItems() {
		String url = Uri.parse(ENDPOINT).buildUpon().appendQueryParameter("method", METHOD_GET_RECENT)
				.appendQueryParameter("api_key", AppProInt_Code)
				.appendQueryParameter(PARAM_EXTRAS, EXTRA_SMALL_URL)
				.build().toString();
		
		return downloadGalleryItems(url);
	}
	
	public ArrayList<GalleryItem> search(String query) {
		String url = Uri.parse(ENDPOINT).buildUpon().appendQueryParameter("method", METHOD_SEARCH)
				.appendQueryParameter("api_key", AppProInt_Code)
				.appendQueryParameter(PARAM_EXTRAS, EXTRA_SMALL_URL)
				.appendQueryParameter(PARAM_TEXT, query)
				.build().toString();
		
		return downloadGalleryItems(url);
	}
			
}
