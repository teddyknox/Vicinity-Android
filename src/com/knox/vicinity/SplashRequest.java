package com.knox.vicinity;

import android.util.Log;
import android.widget.Toast;
import android.app.Activity;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import android.net.http.AndroidHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;

public class SplashRequest extends AsyncTask<HttpUriRequest, Void, String>
{

	private Activity cxt;
	private ProgressDialog loading;

	public SplashRequest(Activity cxt)
	{
		super();
		this.cxt = cxt;
	}

	protected void onPreExecute()
	{
		loading = new ProgressDialog(cxt);
		loading.setTitle("");
		loading.setMessage("Logging you in...");
		loading.setIndeterminate(true);
		loading.setCancelable(true);
		loading.setOnCancelListener(new OnCancelListener()
		{

			public void onCancel(DialogInterface loading)
			{
				SplashRequest.this.cancel(true);
			}
		});
		loading.show();
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
					return EntityUtils.toString(entity);
				}
				else
				{
					Log.v("vicinity", "Server returned no entity");
					return "Failure";
				}
			}
			else
			{
				return "Cancelled";
			}
		}
		catch (Exception e)
		{
			Log.v("vicinity", "http exception: " + e.getMessage());
			return "Exception";
		}
	}

	protected void onProgressUpdate(Integer... integers)
	{}

	protected void onPostExecute(String returnString)
	{

		try
		{
			JSONObject j = new JSONObject(returnString);
			if (j.getString("status").equals("OK"))
			{
				SharedPreferences prefs = cxt.getSharedPreferences(
						"vicinity_preferences.xml", Context.MODE_PRIVATE);

				if (!(prefs.contains("userId") && prefs.contains("sessionKey") && prefs
						.contains("email")))
				{
					// TODO add "remember me" checkbox functionality.

					Editor editPrefs = prefs.edit();

					editPrefs.putString("userId", j.getString(("userId")));
					editPrefs.putString("sessionKey", j.getString("sessionKey"));
					editPrefs.putString("email", j.getString("email"));
					editPrefs.commit();
				}
				
				Log.v("vicinity", "Starting Home Activity");
				Intent homeIntent = new Intent(cxt, Home.class);
				loading.dismiss();
				cxt.startActivity(homeIntent);
				cxt.finish();
				cxt.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				
			}
			else
			{
				loading.dismiss();
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}
}
