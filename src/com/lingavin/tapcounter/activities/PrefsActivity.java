package com.lingavin.tapcounter.activities;

import com.lingavin.tapcounter.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class PrefsActivity extends PreferenceActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs);
	}
}
