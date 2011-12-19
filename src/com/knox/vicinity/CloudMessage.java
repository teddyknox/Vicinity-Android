package com.knox.vicinity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import com.google.android.c2dm.*;

public class CloudMessage extends C2DMBaseReceiver 
{
	public CloudMessage(String senderId)
	{
		super("com.knox.vicinity@gmail.com");
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public IBinder onBind(Intent intent)
	{
		// TODO Auto-generated method stub
		return super.onBind(intent);
	}
	
	@Override
	public void onRegistrered(Context context, String registrationId) {
		Log.w("vicinity", registrationId);
	}

	@Override
	public void onUnregistered(Context context) {
		Log.w("vicinity", "got here!");
	}

	@Override
	public void onError(Context context, String errorId) {
		Log.w("vicinity", errorId);
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.w("vicinityx", intent.getStringExtra("payload"));
	}
}/* BroadcastReceiver
{
	public void onReceive(Context context, Intent intent) {
	    if (intent.getAction().equals("com.google.android.c2dm.intent.REGISTRATION")) {
	        handleRegistration(context, intent);
	    } else if (intent.getAction().equals("com.google.android.c2dm.intent.RECEIVE")) {
	        handleMessage(context, intent);
	     }
	 }

	private void handleRegistration(Context context, Intent intent) {
	    String registration = intent.getStringExtra("registration_id"); 
	    if (intent.getStringExtra("error") != null) {
	    	Log.v("vicinity", intent.getStringExtra("error"));
	    	/*if(intent.getStringExtra("error").equals("SERVICE_NOT_AVAILABLE")){
	    		
	    	}else if(intent.getStringExtra("error").equals("ACCOUNT_MISSING"))
	    	{
	    		
	    	}else if(intent.getStringExtra("error").equals("AUTHENTICATION_FAILED"))
	    	{
	    		
	    	}else if(intent.getStringExtra("error").equals("TOO_MANY_REGISTRATIONS"))
	    	{
	    		
	    	}else if(intent.getStringExtra("error").equals("INVALID_SENDER"))
	    	{
	    		
	    	}else if(intent.getStringExtra("error").equals("PHONE_REGISTRATION_ERROR"))
	    	{
	    		
	    	}*/

/*
	    	
	    	
	    } else if (intent.getStringExtra("unregistered") != null) {
	        // unregistration done, new messages from the authorized sender will be rejected
	    } else if (registration != null) {
	       // Send the registration ID to the 3rd party site that is sending the messages.
	       // This should be done in a separate thread.
	       // When done, remember that all registration is done. 
	    }
	}
	
	private void handleMessage(Context context, Intent intent) {

		
	}

}*/
