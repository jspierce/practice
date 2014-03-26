package com.samsung.sra.tutorial.photogallery;

import android.os.HandlerThread;
import android.util.Log;

public class ThumbnailDownloader<Token> extends HandlerThread {
	private static final String TAG = "ThumbnailDownloader";
	
	public ThumbnailDownloader() {
		super(TAG);
	}
	
	public void queueThumbnail(Token token, String url) {
		Log.i(TAG, "Got a URL:" + url);
	}
}
