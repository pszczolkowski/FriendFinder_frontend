package bart.friendfinderapp;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Bart on 2015-11-06.
 */
public class MyCurrentLoctionListener implements LocationListener {

    double lastLatitude;
    double lastLongitude;

    @Override
    public void onLocationChanged(Location location) {
        lastLatitude = location.getLatitude();
        lastLongitude = location.getLongitude();

        String myLocation = "Latitude = " + location.getLatitude() + " Longitude = " + location.getLongitude();
        Log.e("MY CURRENT LOCATION", myLocation);

    }

    public double getLatitude(Location location){
        return lastLatitude = location.getLatitude();
    }

    public double getLongitude (Location location){
        return lastLongitude = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle){

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}