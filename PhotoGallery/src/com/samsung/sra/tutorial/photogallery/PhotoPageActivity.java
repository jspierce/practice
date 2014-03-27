package com.samsung.sra.tutorial.photogallery;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class PhotoPageActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragment);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PhotoPageFragment()).commit();
		}
	}

}
