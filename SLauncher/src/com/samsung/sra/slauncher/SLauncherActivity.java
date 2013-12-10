package com.samsung.sra.slauncher;

import android.support.v4.app.Fragment;

public class SLauncherActivity extends SingleFragmentActivity {

	@Override public Fragment createFragment() {
		return new SLauncherFragment();
	}

}
