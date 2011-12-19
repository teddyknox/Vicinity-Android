package com.knox.vicinity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Splash extends Activity
{
	private AsyncTask<HttpUriRequest, Void, String> splashRequest;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		SharedPreferences prefs = getSharedPreferences("vicinity_preferences.xml", MODE_PRIVATE);

		if (prefs.contains("userId")
				&& prefs.contains("sessionKey")
				&& prefs.contains("email"))
		{
			setStatus(1, prefs);
		}

		final Button login = (Button)findViewById(R.id.loginButton);
		final Button createAccount = (Button)findViewById(R.id.createAccountButton);

		login.setOnClickListener(new OnClickListener()
		{
			public void onClick(View arg0)
			{
				accountSubmit("LOGIN");
			}
		});

		createAccount.setOnClickListener(new OnClickListener()
		{
			public void onClick(View arg0)
			{
				accountSubmit("CREATE");
			}
		});

	}

	public void accountSubmit(String type)
	{
		final EditText emailText = (EditText) findViewById(R.id.email);
		final EditText passwordText = (EditText) findViewById(R.id.password);
		
		String email = emailText.getText().toString();
		String password = passwordText.getText().toString();

		try
		{
			HttpPost httppost = new HttpPost(
					"http://140.233.209.137/vicinity/asyncResponse.php");

			List<NameValuePair> pairs = new ArrayList<NameValuePair>();

			pairs.add(new BasicNameValuePair("requestType", type));
			pairs.add(new BasicNameValuePair("email", email));
			pairs.add(new BasicNameValuePair("password", password));

			httppost.setEntity(new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
			splashRequest = new SplashRequest(Splash.this)
					.execute(httppost);
			Log.v("vicinity", "HTTP "+type+" POST request sent.");

		} catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
	}
	
	public void setStatus(int status, SharedPreferences prefs)
	{
		try
		{
			HttpPost httppost = new HttpPost(
					"http://140.233.209.137/vicinity/asyncResponse.php");

			List<NameValuePair> pairs = new ArrayList<NameValuePair>();

			pairs.add(new BasicNameValuePair("requestType", "STATUS"));
			pairs.add(new BasicNameValuePair("status", Integer.toString(status)));
			pairs.add(new BasicNameValuePair("sessionKey", prefs.getString("sessionKey", "DNE")));
			pairs.add(new BasicNameValuePair("userId",	prefs.getString("userId", "DNE")));
			
			httppost.setEntity(new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
			splashRequest = new SplashRequest(Splash.this)
					.execute(httppost);
			Log.v("vicinity", "HTTP STATUS POST request sent.");

		} catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
	}

}
