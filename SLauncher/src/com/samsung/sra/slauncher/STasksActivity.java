package com.samsung.sra.slauncher;

import android.support.v4.app.Fragment;

public class STasksActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		return new STasksFragment();
	}

}
