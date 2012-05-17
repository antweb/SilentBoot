package com.antweb.silentboot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
	
	public static final String PREFS_NAME = "silentbootpref";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("SilentBoot","BootReceiver received " + intent.toString());
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		
		if(settings.getBoolean("enabled", true)) {
			AudioManager audiomanager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		    
			int mode = settings.getInt("lastmode", AudioManager.RINGER_MODE_SILENT);
			Log.d("SilentBoot","Trying to restore mode " + mode);
			audiomanager.setRingerMode(mode);
			Log.d("SilentBoot","Mode is " + audiomanager.getRingerMode());
		} else {
			Log.d("SilentBoot","Silent Boot disabled");
		}
	}
}
