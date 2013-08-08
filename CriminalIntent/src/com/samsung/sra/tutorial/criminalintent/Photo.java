package com.samsung.sra.tutorial.criminalintent;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class Photo {
	private static final String TAG = "CRIMEPHOTO";
	
	private static final String JSON_FILENAME = "filename";
	private static final String JSON_ROTATION = "rotation";
	
	private String mFilename;
	private int mRotation = 0;;
	
	/** Create a Photo representing an existing file on disk */
	public Photo(String filename, int rotation) {
		mFilename = filename;
		mRotation = rotation;
	}

	public Photo(JSONObject json) throws JSONException {
		mFilename = json.getString(JSON_FILENAME);
		
		if (json.has(JSON_ROTATION))
			mRotation = json.getInt(JSON_ROTATION);
		else
			mRotation = 0;
	}
	

	
	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(JSON_FILENAME, mFilename);
		json.put(JSON_ROTATION, mRotation);
		return json;
	}
	
	public String getFilename() {
		return mFilename;
	}
	
	public int getRotation() {
		return mRotation;
	}
	
	public void delete(Context context) {
		String path = context.getFileStreamPath(mFilename).getAbsolutePath();
		File photoFile = new File(path);
		boolean success = photoFile.delete();
		if (success)
			Log.d(TAG, "Photo file deleted");
		else
			Log.d(TAG, "Failed to delete photo file");
	}
}
