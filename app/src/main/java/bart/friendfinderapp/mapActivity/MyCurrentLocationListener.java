package bart.friendfinderapp.mapActivity;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Bart on 2015-11-06.
 */
public class MyCurrentLocationListener implements GoogleMap.OnMyLocationChangeListener , LocationListener{

    private final GoogleMap map;
    double lastLatitude;
    double lastLongitude;

    public MyCurrentLocationListener( GoogleMap map ) {
        this.map = map;
    }

    @Override
    public void onLocationChanged( Location location ) {
        lastLatitude = location.getLatitude();
        lastLongitude = location.getLongitude();

        String myLocation = "Latitude = " + location.getLatitude() + " Longitude = " + location.getLongitude();
        Log.e( "MY CURRENT LOCATION", myLocation );
    }

    public double getLatitude( Location location ) {
        return lastLatitude = location.getLatitude();
    }

    public double getLongitude( Location location ) {
        return lastLongitude = location.getLongitude();
    }

    @Override
    public void onStatusChanged( String s, int i, Bundle bundle ) {

    }

    @Override
    public void onProviderEnabled( String s ) {

    }

    @Override
    public void onProviderDisabled( String s ) {

    }

    @Override
    public void onMyLocationChange( Location location ) {
        lastLatitude = location.getLatitude();
        lastLongitude = location.getLongitude();

        String myLocation = "Latitude = " + location.getLatitude() + " Longitude = " + location.getLongitude();
        Log.e( "MY CURRENT LOCATION", myLocation );

        map.moveCamera( CameraUpdateFactory.newLatLng( new LatLng( lastLatitude, lastLongitude ) ) );
        map.animateCamera( CameraUpdateFactory.zoomTo( 17 ) );

        map.setOnMyLocationChangeListener( null );
    }
}