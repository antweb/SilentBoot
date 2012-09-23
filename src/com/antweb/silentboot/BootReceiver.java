package com.antweb.silentboot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.provider.Settings;
import android.util.Log;

/**
 * Restores previous state at boot-up.
 * 
 * @author Anton Weber (antweb)
 * @version 1.1
 */
public class BootReceiver extends BroadcastReceiver {
	
	public static final String PREFS_NAME = "silentbootpref";
	
	/**
	 * onReceive method
	 * 
	 * @param context
	 * @intent intent
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		
		if(settings.getBoolean("enabled", true)) {
			AudioManager audiomanager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		    
			
			int mode = settings.getInt("lastmode", AudioManager.RINGER_MODE_SILENT);
			//Log.d("SilentBoot","Trying to restore mode " + mode);
			audiomanager.setRingerMode(mode);
			//Log.d("SilentBoot","Mode is " + audiomanager.getRingerMode());
			
			//Airplane toggle
			if(settings.getBoolean("airplanetoggle", false)) {
				
				//Load previous state
				int lastairplanemode = settings.getInt("lastairplanemode", -1);
				
				//Re-enable previous state if necessary
				if(lastairplanemode != -1 && lastairplanemode != 0) {
						
					//Log.d("SilentBoot","Trying to set airplane mode to "+ lastairplanemode);
					Settings.System.putInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, lastairplanemode);
					
					Intent changeintent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
					changeintent.putExtra("state", lastairplanemode);
					context.sendBroadcast(intent);

					//Log.d("SilentBoot","Airplane mode is " + Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0));
					
				}

			}
			
			//Start notification
			if(settings.getBoolean("compatibility", false)) {
				Log.d("SilentBoot", "Trying to launch activity...");
				Intent intent_notify = new Intent(context, SilentBoot.class);
				intent_notify.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
				intent_notify.putExtra("notify", true);
				context.startActivity(intent_notify);
			}

		} else {
			//Log.d("SilentBoot","Silent Boot disabled");
		}
	}
}
