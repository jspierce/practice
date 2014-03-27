package com.samsung.sra.tutorial.photogallery;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

public class ThumbnailDownloader<Token> extends HandlerThread {
	private static final String TAG = "ThumbnailDownloader";
	private static final int MESSAGE_DOWNLOAD = 0;
	private static final int MAX_CACHE_SIZE = 20;
	
	LruCache<String,Bitmap> mThumbnailCache;
	Handler mHandler;
	Map<Token, String> requestMap = Collections.synchronizedMap(new HashMap<Token, String>());
	Handler mResponseHandler;
	Listener<Token> mListener;
	
	public interface Listener<Token> {
		void onThumbnailDownloaded(Token token, Bitmap thumbnail);
	}
	
	
	public ThumbnailDownloader(Handler responseHandler) {
		super(TAG);
		
		mResponseHandler = responseHandler;
		mThumbnailCache = new LruCache<String, Bitmap>(MAX_CACHE_SIZE);
	}
	
	@SuppressLint("HandlerLeak")
	@Override
	protected void onLooperPrepared() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == MESSAGE_DOWNLOAD) {
					@SuppressWarnings("unchecked")
					Token token = (Token) msg.obj;
					Log.i(TAG, "Got a request for url: " + requestMap.get(token));
					handleDownloadRequest(token);
				}
			}
		};
	}
	
	public void setListener(Listener<Token> listener) {
		mListener = listener;
	}
	
	public void queueThumbnail(Token token, String url) {
		Log.i(TAG, "Got a URL:" + url);
		
		
		// Check if the cache already has a bitmap for the url
		Bitmap bitmap = mThumbnailCache.get(url);
		if (bitmap == null) {
			// If it doesn't, queue a message requesting the bitmap
			Log.d(TAG, "Cache miss");
			requestMap.put(token, url);
			mHandler.obtainMessage(MESSAGE_DOWNLOAD, token).sendToTarget();
		} else {
			// If it does, set the thumbnail (we should be on the UI thread, so no special call should be necessary)
			Log.d(TAG, "Cache hit");
			mListener.onThumbnailDownloaded(token, bitmap);
		}
		
	}
	
	public void clearQueue() {
		mHandler.removeMessages(MESSAGE_DOWNLOAD);
		requestMap.clear();
	}
	
	private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
        }
    }

    return inSampleSize;
}
	
	private void handleDownloadRequest(final Token token) {
		try {
			final String url = requestMap.get(token);
			if (url == null)
				return;
			
			byte[] bitmapBytes = new FlickrFetchr().getUrlBytes(url);
			
			// First decode with inJustDecodeBounds=true to check dimensions
		    final BitmapFactory.Options options = new BitmapFactory.Options();
		    options.inJustDecodeBounds = true;
		    BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);

		    // Calculate the inSampleSize to sample at (results in a lower memory requirement)
		    ImageView imageView = (ImageView) token;
		    if (imageView == null)
		    	return;
		    options.inSampleSize = calculateInSampleSize(options, imageView.getWidth(), imageView.getHeight());

		    // Decode bitmap with inSampleSize set
		    options.inJustDecodeBounds = false;
		    final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
		    	    
			Log.i(TAG, "Bitmap created");
			
			// Add the bitmap to our queue
			mThumbnailCache.put(url, bitmap);
			
			// Create a runnable to set the imageview's bitmap on the UI thread
			mResponseHandler.post(new Runnable() {
				public void run() {
					// Make sure the imageView is still associated with this url
					if (requestMap.get(token) != url)
						return;
					
					requestMap.remove(token);
					mListener.onThumbnailDownloaded(token, bitmap);
				}
			});
			
		} catch (IOException ioe) {
			Log.e(TAG, "Error downloading image", ioe);
		}
	}
}
