package com.antweb.silentboot;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;


public class SilentBoot extends Activity{

	public static final String PREFS_NAME = "silentbootpref";

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.main);
    	SharedPreferences settings	=	getSharedPreferences(PREFS_NAME, 0);
    	CheckBox checkbox_enable	=	(CheckBox) findViewById(R.id.checkBoxEnable);
    	TextView textview_status	=	(TextView) findViewById(R.id.textStatus);
    	
    	//load status
    	boolean enabled	=	settings.getBoolean("enabled", false);
    	
    	//set loaded value on checkbox
    	checkbox_enable.setChecked(enabled);
    	
    	//set status text
    	setText(textview_status, enabled);
    	
    	//checkbox handler
    	checkbox_enable.setOnCheckedChangeListener(new OnCheckedChangeListener() {
    		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    		{
    			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
    			SharedPreferences.Editor editor = settings.edit();
    			TextView textview_status	=	(TextView) findViewById(R.id.textStatus);
    			
    			if (isChecked) {
    				editor.putBoolean("enabled", true);
    				setText(textview_status, true);
    				
    				//debug
    				Intent serviceintent	=	new Intent();
    				serviceintent.setAction("com.antweb.silentboot.SilencerService");
    				startService(serviceintent);
    				
    	        } else {
    	        	editor.putBoolean("enabled", false);
    	        	setText(textview_status, false);
    	        }
    	        editor.commit();              
    		}
    	});
    }
    
    protected void setText(TextView view, boolean enabled) {
    	if(enabled) {
    		view.setText(R.string.textEnabled);
    	} else {
    		view.setText(R.string.textDisabled);
    	}
    }
}