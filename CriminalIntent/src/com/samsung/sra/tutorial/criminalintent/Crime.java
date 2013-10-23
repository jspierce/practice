package com.samsung.sra.tutorial.criminalintent;

import java.util.Date;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class Crime {
	
	private UUID mId;
	private String mTitle;
	private Date mDate;
	private boolean mSolved;
	private Photo mPhoto;
	private String mSuspect;
	private String mSuspectPhone;
	
	private static final String JSON_ID = "id";
	private static final String JSON_TITLE = "title";
	private static final String JSON_SOLVED = "solved";
	private static final String JSON_DATE = "date";
	private static final String JSON_PHOTO = "photo";
	private static final String JSON_SUSPECT = "suspect";
	private static final String JSON_SUSPECT_PHONE = "suspect_phone";
	
	
	public Crime() {
		// Generate unique identifier
		mId = UUID.randomUUID();
		
		mDate = new Date();
	}
	
	public Crime(JSONObject json) throws JSONException {
		mId = UUID.fromString(json.getString(JSON_ID));
		mTitle = json.getString(JSON_TITLE);
		mSolved = json.getBoolean(JSON_SOLVED);
		mDate = new Date(json.getLong(JSON_DATE));
		
		if (json.has(JSON_PHOTO))
			mPhoto = new Photo(json.getJSONObject(JSON_PHOTO));
		
		if (json.has(JSON_SUSPECT))
			mSuspect = json.getString(JSON_SUSPECT);
		
		if (json.has(JSON_SUSPECT_PHONE))
			mSuspectPhone = json.getString(JSON_SUSPECT_PHONE);
	}
	
	
	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(JSON_ID, mId.toString());
		json.put(JSON_TITLE, mTitle);
		json.put(JSON_SOLVED, mSolved);
		json.put(JSON_DATE, mDate.getTime());
		
		if (mPhoto != null)
			json.put(JSON_PHOTO, mPhoto.toJSON());
		
		if (mSuspect != null)
			json.put(JSON_SUSPECT, mSuspect);
		
		if (mSuspectPhone != null)
			json.put(JSON_SUSPECT_PHONE, mSuspectPhone);
		
		return json;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public UUID getId() {
		return mId;
	}

	public Date getDate() {
		return mDate;
	}

	public void setDate(Date date) {
		mDate = date;
	}

	public boolean isSolved() {
		return mSolved;
	}

	public void setSolved(boolean solved) {
		mSolved = solved;
	}
	
	public Photo getPhoto() {
		return mPhoto;
	}
	
	public void setPhoto(Photo photo, Context context) {
		// If we already have a photo delete the previous one
		if (mPhoto != null) {
			mPhoto.delete(context);
		}
		
		mPhoto = photo;
	}
	
	public String getSuspect() {
		return mSuspect;
	}
	
	public void setSuspect(String suspect) {
		mSuspect = suspect;
	}
	
	public String getSuspectPhone() {
		return mSuspectPhone;
	}
	
	public void setSuspectPhone(String suspectPhone) {
		mSuspectPhone = suspectPhone;
	}
	
	@Override
	public String toString() {
		return mTitle;
	}
	
	public void delete(Context appContext) {
		setPhoto(null, appContext);
	}
}
