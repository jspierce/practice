package com.samsung.sra.tutorial.criminalintent;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageFragment extends DialogFragment {
	public static final String EXTRA_IMAGE_PATH = "com.samsung.sra.tutorial.criminalintent.image_path";
	public static final String EXTRA_IMAGE_ROTATION = "com.samsung.sra.tutorial.criminalintent.image_rotation";
	
	public static ImageFragment newInstance(String imagePath, int rotation) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_IMAGE_PATH, imagePath);
		args.putSerializable(EXTRA_IMAGE_ROTATION, rotation);
		
		ImageFragment fragment = new ImageFragment();
		fragment.setArguments(args);
		fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
		
		return fragment;
	}
	
	private ImageView mImageView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		mImageView = new ImageView(getActivity());
		String path = (String) getArguments().getSerializable(EXTRA_IMAGE_PATH);
		Integer rotation = (Integer) getArguments().getSerializable(EXTRA_IMAGE_ROTATION);
		BitmapDrawable image = PictureUtils.getScaledDrawable(getActivity(), path, rotation);
		mImageView.setImageDrawable(image);
		return mImageView;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		
		PictureUtils.cleanImageView(mImageView);
	}
}
