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

import android.net.Uri;
import android.util.Log;

public class FlickrFetchr {
	private static final String TAG = "FlickrFetchr";
	
	private static final String ENDPOINT = "http://api.flickr.com/services/rest/";
	private static final String AppProInt_Code = "93a7f095d43d5ae04d828284e0ac3a25";
	private static final String METHOD_GET_RECENT = "flickr.photos.getRecent";
	private static final String PARAM_EXTRAS = "extras";
	private static final String EXTRA_SMALL_URL = "url_s";
	
	private static final String XML_PHOTO = "photo";
	
	private byte[] getUrlBytes(String urlSpec) throws IOException {
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
			if (eventType == XmlPullParser.START_TAG && XML_PHOTO.equals(parser.getName())) {
				String id = parser.getAttributeValue(null, "id");
				String caption = parser.getAttributeValue(null, "title");
				String smallUrl = parser.getAttributeValue(null, EXTRA_SMALL_URL);
				
				GalleryItem item = new GalleryItem();
				item.setId(id);
				item.setCaption(caption);
				item.setUrl(smallUrl);
				items.add(item);
			}
			
			eventType = parser.next();
		}
	}
	
	public ArrayList<GalleryItem> fetchItems() {
		ArrayList<GalleryItem> items = new ArrayList<GalleryItem>();
		
		try {
			String url = Uri.parse(ENDPOINT).buildUpon().appendQueryParameter("method", METHOD_GET_RECENT)
														.appendQueryParameter("api_key", AppProInt_Code)
														.appendQueryParameter(PARAM_EXTRAS, EXTRA_SMALL_URL)
														.build().toString();
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
}
