package com.knox.vicinity;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.provider.Settings;
import android.util.Log;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.knox.vicinity.MyLocation.LocationResult;

public class LocationSetter extends MapActivity
{

	// private MyLocation myLocation;
	private Location lastLocation;
	private MapView mapView;
	private MapController mc;

	private static final int DEFAULT_ZOOM_LEVEL = 17;
	private static final int PLACES_RADIUS = 150;

	private List<Overlay> mapOverlays;
	private Drawable geoPointMarker;
	// private HelloItemizedOverlay itemizedOverlay;
	private MyLocationOverlay userLocation;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.locationsetter);
		
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		mapView = (MapView) findViewById(R.id.mapView);
		mapView.setBuiltInZoomControls(true);
		mc = mapView.getController();
		mc.setZoom(DEFAULT_ZOOM_LEVEL);

		mapOverlays = mapView.getOverlays();
		userLocation = new UserLocation(LocationSetter.this, mapView);
		mapOverlays.add(userLocation);
		
		ListView lv = (ListView)findViewById(R.id.list);
		ArrayAdapter<String> aa = new ArrayAdapter<String>(LocationSetter.this, R.layout.chatlistitem, new String[]{"Use only GPS Coordinates"});
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				if(position == 0)
				{
					//Assuming that the first item is the 'Use only GPS Coordinates' item. Should figure out how to get list item with parameter.
					Intent locationResults = new Intent();
					locationResults.putExtra("location", userLocation.getLastFix());
					setResult(LocationSetter.RESULT_OK, locationResults);
					Log.v("vicinity", "Option 1 pressed");
					finish();
				}
			}
		});
	    lv.setAdapter(aa);
	    		
		userLocation.runOnFirstFix(new Runnable()
		{
			public void run()
			{
				LocationSetter.this.runOnUiThread(new Runnable()
				{

					@Override
					public void run()
					{
						mc.animateTo(userLocation.getMyLocation());
						
						Uri base = Uri.parse("https://maps.googleapis.com/maps/api/place/search/json");
						Uri.Builder uriBuilder = base.buildUpon();
						uriBuilder.appendQueryParameter("location",	
								Double.toString(userLocation.getLastFix().getLatitude())
								+ ","
								+ Double.toString(userLocation.getLastFix().getLongitude()));
						uriBuilder.appendQueryParameter("radius", Integer.toString(PLACES_RADIUS));
						// uri.appendQueryParameter("types", );
						// uri.appendQueryParameter("name", ); used to filter results
						uriBuilder.appendQueryParameter("sensor", "true");
						uriBuilder.appendQueryParameter("key", "AIzaSyCLhlNUabFadU88pJ4mrhK_1CRgqxcrPd8");
						String url = uriBuilder.toString();
						HttpGet pr = new HttpGet(url);
						
						new PlacesRequest(LocationSetter.this).execute(pr);
						Log.v("vicinity", "Google Places GET request sent.");
					}
				});
			}
		});
	}

	protected void onPause()
	{
		super.onPause();
		userLocation.disableMyLocation();
	}

	protected void onResume()
	{
		super.onResume();
		if(!userLocation.enableMyLocation())
		{
			AlertDialog.Builder settingsDialogBuilder = new AlertDialog.Builder(LocationSetter.this);
			settingsDialogBuilder.setTitle("Your GPS is off").setMessage("You should turn it on.");
			settingsDialogBuilder.setPositiveButton("Launch GPS Settings", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface arg0, int arg1)
				{
					Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivityForResult(intent, 0);
					arg0.dismiss();
				}
			});
			settingsDialogBuilder.setNegativeButton("Continue without location", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface arg0, int arg1)
				{
					arg0.dismiss();
				}
			});
			settingsDialogBuilder.show();
		}
		
	}

	private int toMicroDegrees(double latitude)
	{
		return (int) (latitude * 1000000);
	}

	public void onBackPressed()
	{
		super.onBackPressed();
		overridePendingTransition(android.R.anim.fade_in,
				android.R.anim.fade_out);
	}

	protected boolean isRouteDisplayed()
	{
		return false;
	}
}
