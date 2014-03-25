package com.samsung.sra.tutorial.photogallery;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.net.Uri;
import android.util.Log;

public class FlickrFetchr {
	private static final String TAG = "FlickrFetchr";
	
	private static final String ENDPOINT = "http://api.flickr.com/services/rest/";
	private static final String AppProInt_Code = "<code here>";
	private static final String METHOD_GET_RECENT = "flickr.photos.getRecent";
	private static final String PARAM_EXTRAS = "extras";
	private static final String EXTRA_SMALL_URL = "url_s";
	
	byte[] getUrlBytes(String urlSpec) throws IOException {
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
	
	public void fetchItems() {
		try {
			String url = Uri.parse(ENDPOINT).buildUpon().appendQueryParameter("method", METHOD_GET_RECENT)
														.appendQueryParameter("api_key", AppProInt_Code)
														.appendQueryParameter(PARAM_EXTRAS, EXTRA_SMALL_URL)
														.build().toString();
			String xmlString = getUrl(url);
			Log.i(TAG, "Received xml: " + xmlString);
		} catch (IOException ioe) {
			Log.e(TAG, "Failed to fetch items", ioe);
		}
	}
}
