package com.samsung.sra.tutorial.criminalintent;

import java.util.UUID;

public class Crime {
	
	private UUID mId;
	private String mTitle;
	
	public Crime() {
		// Generate unique identifier
		mId = UUID.randomUUID();
	}
}
