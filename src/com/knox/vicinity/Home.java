package com.knox.vicinity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import com.google.android.c2dm.C2DMessaging;
import com.knox.vicinity.MyLocation.LocationResult;

import android.app.Activity;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

public class Home extends ListActivity
{
	private static final String APP_EMAIL = "com.knox.vicinity@gmail.com";
	private static final String[] COUNTRIES = new String[]
	{ "Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra",
			"Angola", "Anguilla", "Antarctica", "Antigua and Barbuda",
			"Argentina", "Armenia", "Aruba", "Australia", "Austria",
			"Azerbaijan", "Bahrain", "Bangladesh", "Barbados", "Belarus",
			"Belgium", "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia",
			"Bosnia and Herzegovina", "Botswana", "Bouvet Island", "Brazil",
			"British Indian Ocean Territory", "British Virgin Islands",
			"Brunei", "Bulgaria", "Burkina Faso", "Burundi", "Cote d'Ivoire",
			"Cambodia", "Cameroon", "Canada", "Cape Verde", "Cayman Islands",
			"Central African Republic", "Chad", "Chile", "China",
			"Christmas Island", "Cocos (Keeling) Islands", "Colombia",
			"Comoros", "Congo", "Cook Islands", "Costa Rica", "Croatia",
			"Cuba", "Cyprus", "Czech Republic",
			"Democratic Republic of the Congo", "Denmark", "Djibouti",
			"Dominica", "Dominican Republic", "East Timor", "Ecuador", "Egypt",
			"El Salvador", "Equatorial Guinea", "Eritrea", "Estonia",
			"Ethiopia", "Faeroe Islands", "Falkland Islands", "Fiji",
			"Finland", "Former Yugoslav Republic of Macedonia", "France",
			"French Guiana", "French Polynesia", "French Southern Territories",
			"Gabon", "Georgia", "Germany", "Ghana", "Gibraltar", "Greece",
			"Greenland", "Grenada", "Guadeloupe", "Guam", "Guatemala",
			"Guinea", "Guinea-Bissau", "Guyana", "Haiti",
			"Heard Island and McDonald Islands", "Honduras", "Hong Kong",
			"Hungary", "Iceland", "India", "Indonesia", "Iran", "Iraq",
			"Ireland", "Israel", "Italy", "Jamaica", "Japan", "Jordan",
			"Kazakhstan", "Kenya", "Kiribati", "Kuwait", "Kyrgyzstan", "Laos",
			"Latvia", "Lebanon", "Lesotho", "Liberia", "Libya",
			"Liechtenstein", "Lithuania", "Luxembourg", "Macau", "Madagascar",
			"Malawi", "Malaysia", "Maldives", "Mali", "Malta",
			"Marshall Islands", "Martinique", "Mauritania", "Mauritius",
			"Mayotte", "Mexico", "Micronesia", "Moldova", "Monaco", "Mongolia",
			"Montserrat", "Morocco", "Mozambique", "Myanmar", "Namibia",
			"Nauru", "Nepal", "Netherlands", "Netherlands Antilles",
			"New Caledonia", "New Zealand", "Nicaragua", "Niger", "Nigeria",
			"Niue", "Norfolk Island", "North Korea", "Northern Marianas",
			"Norway", "Oman", "Pakistan", "Palau", "Panama",
			"Papua New Guinea", "Paraguay", "Peru", "Philippines",
			"Pitcairn Islands", "Poland", "Portugal", "Puerto Rico", "Qatar",
			"Reunion", "Romania", "Russia", "Rwanda", "Sqo Tome and Principe",
			"Saint Helena", "Saint Kitts and Nevis", "Saint Lucia",
			"Saint Pierre and Miquelon", "Saint Vincent and the Grenadines",
			"Samoa", "San Marino", "Saudi Arabia", "Senegal", "Seychelles",
			"Sierra Leone", "Singapore", "Slovakia", "Slovenia",
			"Solomon Islands", "Somalia", "South Africa",
			"South Georgia and the South Sandwich Islands", "South Korea",
			"Spain", "Sri Lanka", "Sudan", "Suriname",
			"Svalbard and Jan Mayen", "Swaziland", "Sweden", "Switzerland",
			"Syria", "Taiwan", "Tajikistan", "Tanzania", "Thailand",
			"The Bahamas", "The Gambia", "Togo", "Tokelau", "Tonga",
			"Trinidad and Tobago", "Tunisia", "Turkey", "Turkmenistan",
			"Turks and Caicos Islands", "Tuvalu", "Virgin Islands", "Uganda",
			"Ukraine", "United Arab Emirates", "United Kingdom",
			"United States", "United States Minor Outlying Islands", "Uruguay",
			"Uzbekistan", "Vanuatu", "Vatican City", "Venezuela", "Vietnam",
			"Wallis and Futuna", "Western Sahara", "Yemen", "Yugoslavia",
			"Zambia", "Zimbabwe" };
	private String userId;
	private String sessionKey;
	private String email;
	private long lat;
	private long lon;
	private Location setLocation;
	private ServiceConnection cloudMessageConnection;
	//private MyLocation myLocation;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		setListAdapter(new ArrayAdapter<String>(this, R.layout.chatlistitem,
				COUNTRIES));

		SharedPreferences prefs = getSharedPreferences(
				"vicinity_preferences.xml", MODE_PRIVATE);

		userId = prefs.getString("userId", "DNE");
		sessionKey = prefs.getString("sessionKey", "DNE");
		email = prefs.getString("email", "DNE");

		final Button sendButton = (Button) findViewById(R.id.sendButton);
		final EditText chatField = (EditText) findViewById(R.id.chatField);

		sendButton.setOnClickListener(new OnClickListener()
		{
			public void onClick(View arg0)
			{
				Editable messageEditable = chatField.getEditableText();
				String message = messageEditable.toString();
				messageEditable.clear();
				messagePost(message);
			}
		});
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_CANCELED)
		{
			switch (requestCode)
			{
			case Activity.RESULT_FIRST_USER://LocationSetter for now.
				C2DMessaging.register(this, "com.knox.vicinity@gmail.com");
				if(bindService(new Intent().setClassName(this, ".CloudMessage"), cloudMessageConnection, 0))
					Log.v("vicinity", "C2DM Service bound"); 
				else
					Log.v("vicinity", "C2DM Service not bound");
				setLocation = (Location) data.getExtras().get("location");
				try
				{
					HttpPost httppost = new HttpPost(
							"http://140.233.209.137/vicinity/asyncResponse.php");

					List<NameValuePair> pairs = new ArrayList<NameValuePair>();

					pairs.add(new BasicNameValuePair("requestType", "LOCATION"));
					pairs.add(new BasicNameValuePair("userId", userId));
					pairs.add(new BasicNameValuePair("sessionKey", sessionKey));
					pairs.add(new BasicNameValuePair("lat", Double
							.toString(setLocation.getLatitude())));
					pairs.add(new BasicNameValuePair("lon", Double
							.toString(setLocation.getLongitude())));

					httppost.setEntity(new UrlEncodedFormEntity(pairs,
							HTTP.UTF_8));
					new HomeRequest(Home.this).execute(httppost);
					Log.v("vicinity", "HTTP MESSAGE POST request sent.");

				}
				catch (UnsupportedEncodingException e)
				{
					e.printStackTrace();
				}

				break;
			default:
				Log.v("vicinity", "Invalid requestCode returned from activity.");

			}
		}
	}

	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.home_menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{

		case R.id.location:

			Intent locationSetter = new Intent(Home.this, LocationSetter.class);
			startActivity(locationSetter);
			overridePendingTransition(android.R.anim.fade_in,
					android.R.anim.fade_out);

			return true;

		case R.id.logout:

			try
			{
				HttpPost httppost = new HttpPost(
						"http://140.233.209.137/vicinity/asyncResponse.php");

				List<NameValuePair> pairs = new ArrayList<NameValuePair>();

				pairs.add(new BasicNameValuePair("requestType", "LOGOUT"));
				pairs.add(new BasicNameValuePair("sessionKey", sessionKey));
				pairs.add(new BasicNameValuePair("userId", userId));

				httppost.setEntity(new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
				new HomeRequest(Home.this).execute(httppost);
				Log.v("vicinity", "HTTP LOGOUT POST request sent.");

			}
			catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
			}

			SharedPreferences prefs = getSharedPreferences(
					"vicinity_preferences.xml", MODE_PRIVATE);
			Editor e = prefs.edit();
			e.remove("userId");
			e.remove("sessionKey");
			e.commit();

			Intent splash = new Intent(Home.this, Splash.class);
			startActivityForResult(splash, Home.RESULT_FIRST_USER);
			finish();

			return true;

		case R.id.settings:

			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void messagePost(String message)
	{
		try
		{
			HttpPost httppost = new HttpPost(
					"http://140.233.209.137/vicinity/asyncResponse.php");

			List<NameValuePair> pairs = new ArrayList<NameValuePair>();

			pairs.add(new BasicNameValuePair("requestType", "MESSAGE"));
			pairs.add(new BasicNameValuePair("sessionKey", sessionKey));
			pairs.add(new BasicNameValuePair("userId", userId));
			pairs.add(new BasicNameValuePair("message", message));
			//pairs.add(new BasicNameValuePair("lat", Double.toString(setLocation.getLatitude())));
			//pairs.add(new BasicNameValuePair("long", Double.toString(setLocation.getLongitude())));
			//pairs.add(new BasicNameValuePair("time", ));

			httppost.setEntity(new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
			new HomeRequest(Home.this).execute(httppost);
			Log.v("vicinity", "HTTP MESSAGE POST request sent.");

		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
	}

	protected void onDestroy()
	{
		
		Intent unregIntent = new Intent("com.google.android.c2dm.intent.UNREGISTER");
		unregIntent.putExtra("app", PendingIntent.getBroadcast(this, 0, new Intent(), 0));
		startService(unregIntent);
		
		try
		{
			HttpPost httppost = new HttpPost(
					"http://140.233.209.137/vicinity/asyncResponse.php");

			List<NameValuePair> pairs = new ArrayList<NameValuePair>();

			pairs.add(new BasicNameValuePair("requestType", "STATUS"));
			pairs.add(new BasicNameValuePair("status", "0"));
			pairs.add(new BasicNameValuePair("sessionKey", sessionKey));
			pairs.add(new BasicNameValuePair("userId", userId));

			httppost.setEntity(new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
			new HomeRequest(Home.this).execute(httppost);
			Log.v("vicinity", "HTTP STATUS POST request sent.");

		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
	}
}
