package com.knox.vicinity;

import android.content.Context;
import android.location.Location;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class UserLocation extends MyLocationOverlay
{
//	private MapView mv;

	public UserLocation(Context arg0, MapView arg1)
	{
		super(arg0, arg1);
	//	mv = arg1;
		// TODO Auto-generated constructor stub
	}
	
	/*public void onLocationChanged(Location location)
	{
		mv.getController().animateTo(new GeoPoint((int)location.getLatitude()*1000000, (int)location.getLongitude()*1000000));
	}*/

}
