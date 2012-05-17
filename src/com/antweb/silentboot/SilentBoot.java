package com.antweb.silentboot;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

/**
 * Silent Boot Activity
 * 
 * @author Anton Weber (Ant')
 */
public class SilentBoot extends Activity {

	/** 
	 * Preference file name
	 */
	public static final String PREFS_NAME = "silentbootpref";
	
	/**
	 * Notification ID
	 */
	public static final int NOTIFICATION_ID = 1;

	/**
	 * onCreate method.
	 * 
	 * @param savedInstanceState
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	Log.d("SilentBoot", "Launching Activity");
    	
    	Bundle extras = getIntent().getExtras();
    	if(extras != null && extras.getBoolean("notify")) {
    		startNotification();
    		finish();
    	}
    	
    	setContentView(R.layout.main);
    	SharedPreferences settings		=	getSharedPreferences(PREFS_NAME, 0);
    	CheckBox checkbox_enable		=	(CheckBox) findViewById(R.id.checkBoxEnable);
    	CheckBox checkbox_airplane		=	(CheckBox) findViewById(R.id.checkBoxAirplaneToggle);
    	CheckBox checkbox_compatibility	=	(CheckBox) findViewById(R.id.checkBoxCompatibiblityToggle);
    	TextView textview_status		=	(TextView) findViewById(R.id.textStatus);
    	TextView textview_airplane		=	(TextView) findViewById(R.id.textAirplaneStatus);
    	
    	//Load preferences
    	boolean enabled			=	settings.getBoolean("enabled", false);
    	boolean airplanetoggle	=	settings.getBoolean("airplanetoggle", false);
    	boolean compatibility	=	settings.getBoolean("compatibility", false);
    	
    	if(enabled && compatibility) {
    		startNotification();
    	}
    	
    	//Set loaded value on checkbox
    	checkbox_enable.setChecked(enabled);
    	checkbox_airplane.setChecked(airplanetoggle);
    	checkbox_compatibility.setChecked(compatibility);
    	
    	//Disable checkboxes if necessary
    	if(!enabled) {
    		checkbox_airplane.setEnabled(false);
    		checkbox_compatibility.setEnabled(false);
    	}
    	
    	//Set status text   
    	setEnabledText(textview_status, enabled);
    	setAirplaneText(textview_airplane, airplanetoggle);

    	
    	//Checkbox handlers
    	checkbox_enable.setOnCheckedChangeListener(new OnCheckedChangeListener() {
    		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    		{
    			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
    			SharedPreferences.Editor editor = settings.edit();
    			
    			CheckBox checkbox_airplane		=	(CheckBox) findViewById(R.id.checkBoxAirplaneToggle);
    			CheckBox checkbox_compatibility	=	(CheckBox) findViewById(R.id.checkBoxCompatibiblityToggle);
    			TextView textview_status		=	(TextView) findViewById(R.id.textStatus);
    			TextView textview_airplane		=	(TextView) findViewById(R.id.textAirplaneStatus);
    			
    			if (isChecked) {
    				editor.putBoolean("enabled", true);
    				setEnabledText(textview_status, true);
    	        	textview_airplane.setEnabled(true);
    	        	checkbox_airplane.setEnabled(true);
    	        	checkbox_compatibility.setEnabled(true);
    	        	
    	        	if(settings.getBoolean("compatibility", false))
    	        		startNotification();
    	        	
    	        } else {
    	        	editor.putBoolean("enabled", false);
    	        	setEnabledText(textview_status, false);
    	        	textview_airplane.setEnabled(false);
    	        	checkbox_airplane.setEnabled(false);
    	        	checkbox_compatibility.setEnabled(false);
    	        	
    	        	stopNotification();
    	        }
    	        editor.commit();              
    		}
    	});
    	
    	checkbox_airplane.setOnCheckedChangeListener(new OnCheckedChangeListener() {
    		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    		{
    			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
    			SharedPreferences.Editor editor = settings.edit();
    			TextView textview_airplane	=	(TextView) findViewById(R.id.textAirplaneStatus);
    			
    			if (isChecked) {
    				editor.putBoolean("airplanetoggle", true);
    				setAirplaneText(textview_airplane, true);
    				
    	        } else {
    	        	editor.putBoolean("airplanetoggle", false);
    	        	setAirplaneText(textview_airplane, false);
    	        }
    	        editor.commit();              
    		}
    	});
    	
    	checkbox_compatibility.setOnCheckedChangeListener(new OnCheckedChangeListener() {
    		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    		{
    			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
    			SharedPreferences.Editor editor = settings.edit();
    			
    			if (isChecked) {
    				editor.putBoolean("compatibility", true);
    				startNotification();
    	        } else {
    	        	editor.putBoolean("compatibility", false);
    	        	stopNotification();
    	        }
    	        editor.commit();              
    		}
    	});
    }
    
    
    /**
     * Starts the notification.
     */
    protected void startNotification() {
    	int drawableid;
    	
    	if(android.os.Build.VERSION.SDK_INT  >= android.os.Build.VERSION_CODES.FROYO)
    		drawableid = R.drawable.status_gingerbread;
    	else
    		drawableid = R.drawable.status_froyo;
    	
    	Notification notification	= new Notification(drawableid, getString(R.string.notificationEnabled), System.currentTimeMillis());
    	notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_NO_CLEAR;
    	
    	Intent intent				= new Intent(this, SilentBoot.class);
		PendingIntent pendingintent	= PendingIntent.getActivity(this, 0, intent, 0);
		
		notification.setLatestEventInfo(getApplicationContext(), getString(R.string.notificationTitle), getString(R.string.notificationEnabled), pendingintent);
		
		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(NOTIFICATION_ID, notification);
    }
    
    
    /**
     * Stops the notification.
     */
    protected void stopNotification() {
    	NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(NOTIFICATION_ID);
    }
    
    
    /**
     * Sets the enabled description.
     * 
     * @param view
     * @param enabled
     */
    protected void setEnabledText(TextView view, boolean enabled) {
    	if(enabled) {
    		view.setText(R.string.textEnabled);
    	} else {
    		view.setText(R.string.textDisabled);
    	}
    }
    
    
    /**
     * Sets the airplane toggle description.
     * 
     * @param view
     * @param enabled
     */
    protected void setAirplaneText(TextView view, boolean enabled) {
    	if(enabled) {
    		view.setText(R.string.textAirplaneEnabled);
    	} else {
    		view.setText(R.string.textAirplaneDisabled);
    	}
    }

}