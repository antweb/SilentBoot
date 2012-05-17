package com.antweb.silentboot;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * Silent Boot activity
 * 
 * @author Anton Weber (antweb)
 * @version 1.1
 */
public class SilentBoot extends Activity {

	/**
	 * Preference file name.
	 */
	public static final String PREFS_NAME = "silentbootpref";

	/**
	 * onCreate method
	 * 
	 * @param savedInstanceState
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.main);
    	SharedPreferences settings	=	getSharedPreferences(PREFS_NAME, 0);
    	CheckBox checkbox_enable	=	(CheckBox) findViewById(R.id.checkBoxEnable);
    	CheckBox checkbox_airplane	=	(CheckBox) findViewById(R.id.checkBoxAirplaneToggle);
    	TextView textview_status	=	(TextView) findViewById(R.id.textStatus);
    	TextView textview_airplane	=	(TextView) findViewById(R.id.textAirplaneStatus);
    	
    	//Load status
    	boolean enabled			=	settings.getBoolean("enabled", false);
    	boolean airplanetoggle	=	settings.getBoolean("airplanetoggle", false);
    	
    	//Set loaded value on checkbox
    	checkbox_enable.setChecked(enabled);
    	checkbox_airplane.setChecked(airplanetoggle);
    	
    	//Set status text   
    	setEnabledText(textview_status, enabled);
    	setAirplaneText(textview_airplane, airplanetoggle);
  
    	
    	//Checkbox handlers
    	checkbox_enable.setOnCheckedChangeListener(new OnCheckedChangeListener() {
    		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    		{
    			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
    			SharedPreferences.Editor editor = settings.edit();
    			
    			CheckBox checkbox_airplane	=	(CheckBox) findViewById(R.id.checkBoxAirplaneToggle);
    			TextView textview_status	=	(TextView) findViewById(R.id.textStatus);
    			TextView textview_airplane	=	(TextView) findViewById(R.id.textAirplaneStatus);
    			
    			if (isChecked) {
    				editor.putBoolean("enabled", true);
    				setEnabledText(textview_status, true);
    	        	textview_airplane.setEnabled(true);
    	        	checkbox_airplane.setEnabled(true);
    	        } else {
    	        	editor.putBoolean("enabled", false);
    	        	setEnabledText(textview_status, false);
    	        	textview_airplane.setEnabled(false);
    	        	checkbox_airplane.setEnabled(false);
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