package com.knox.vicinity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class PlacesRequest extends AsyncTask<HttpUriRequest, Void, String>
{

	private Activity cxt;

	public PlacesRequest(Activity cxt)
	{
		super();
		this.cxt = cxt;
	}

	protected String doInBackground(HttpUriRequest... request)
	{	
		AndroidHttpClient httpClient = AndroidHttpClient.newInstance("");
		try
		{
			HttpResponse response = httpClient.execute(request[0]);
			if (!isCancelled())
			{
				HttpEntity entity = response.getEntity();
				if (entity != null)
				{
					return "OK";//EntityUtils.toString(entity);
				} else
				{
					Log.v("vicinity", "Server returned no entity");
					return "Failure";
				}
			}else{
				return "Cancelled";
			}
		} catch (Exception e) {
			Log.v("vicinity", "http exception: " + e.getMessage());
			return "Exception";
		}
	}
	
	protected void onPostExecute(String returnString)
	{
		Log.v("vicinity",returnString);
		try
		{
			JSONObject j = new JSONObject(returnString);
			if (j.getString("status").equals("OK"))
			{
				//TODO add places to list
			}else 
			{
				Toast.makeText(cxt, "Error getting places.", Toast.LENGTH_LONG).show();
				Log.v("vicinity", returnString);
			}
			
			
		}catch(JSONException e){e.printStackTrace();}
	}

}
