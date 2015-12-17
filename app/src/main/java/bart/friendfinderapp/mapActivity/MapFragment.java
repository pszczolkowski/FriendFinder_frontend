package bart.friendfinderapp.mapActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bart.friendfinderapp.R;
import bart.friendfinderapp.friends.UpdateUserFriendsPositionThread;
import bart.friendfinderapp.friends.User;

import static bart.friendfinderapp.friends.UserFriends.getUserFriends;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    MyCurrentLocationListener locationListener;
    private LocationManager locationManager;

    private final Map< String, Marker > friendMarkers = new HashMap<>();
    private MyPositionUpdateThread updateMyPositionThread;
    private UpdateUserFriendsPositionThread updateUserFriendsPositionThread;
    private FrameLayout frameLayout;

    public MapFragment(){}

    public static Fragment newInstance(){
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        fragment.setArguments( args );
        return fragment;
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        frameLayout = (FrameLayout) inflater.inflate( R.layout.fragment_maps, container, false );
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById( R.id.map );
        locationManager = (LocationManager) super.getActivity().getSystemService( Context.LOCATION_SERVICE );

        mapFragment.getMapAsync( this );

        runUpdateMyLocalizationThread();
        startCheckingForFriendsPositionsChanges();

        return frameLayout;

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady( GoogleMap googleMap ) {
        mMap = googleMap;

        locationListener = new MyCurrentLocationListener( mMap );
        getPositionFromGPS( locationListener );
        mMap.setOnMyLocationChangeListener( locationListener );
        mMap.setMyLocationEnabled( true );
        mMap.getUiSettings().setZoomControlsEnabled( true );
        mMap.setMapType( GoogleMap.MAP_TYPE_NORMAL );

        updateFriendsMarkers();
    }

    // OBSLUGA GPS
    public void getPositionFromGPS( MyCurrentLocationListener locationListener ) {
        LocationManager locationManager = (LocationManager) super.getActivity().getSystemService( Context.LOCATION_SERVICE );

        if ( locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            Toast.makeText( super.getActivity(), "GPS is Enabled in your device", Toast.LENGTH_SHORT ).show();
        } else {
            showGPSDisabledAlertToUser();
        }
        try {
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider( criteria, true );
            locationManager.requestLocationUpdates( provider, 1000, 0, locationListener );
        } catch ( SecurityException missingPermission ) {
            warningDialog();
        }
    }

    private void warningDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( super.getActivity() );
        alertDialogBuilder
                .setMessage( "You didn't accept sharing your gps location and without it app is useless." )
                .setNegativeButton( "Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick( DialogInterface dialog, int id ) {
                                dialog.cancel();
                            }
                        } );
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    // WYMUSZENIE NA UZYTKOWNIKU WLACZENIA GPSA W URZADZENIU
    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( super.getActivity() );
        alertDialogBuilder.setMessage( "GPS is disabled in your device. Would you like to enable it?" )
                .setCancelable( false )
                .setPositiveButton( "Goto Settings Page To Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick( DialogInterface dialog, int id ) {
                                Intent callGPSSettingIntent = new Intent( android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS );
                                startActivity( callGPSSettingIntent );
                            }
                        } );
        alertDialogBuilder.setNegativeButton( "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick( DialogInterface dialog, int id ) {
                        dialog.cancel();
                    }
                } );
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void startCheckingForFriendsPositionsChanges() {
        updateUserFriendsPositionThread = new UpdateUserFriendsPositionThread( this );
        updateUserFriendsPositionThread.start();
    }

    public void updateFriendsMarkers() {
        List< User > friendsList = getUserFriends();
        for ( User friend : friendsList ) {
            if ( friendShouldBeShowedOnMap( friend ) ) {
                if ( friendMarkers.containsKey( friend.getId() ) ) {
                    Marker friendMarker = friendMarkers.get( friend.getId() );
                    friendMarker.setPosition( new LatLng( friend.getUserLocalization().getLongitude(), friend.getUserLocalization().getLatitude() ) );
                } else {
                    friendMarkers.put( friend.getId(), mMap.addMarker( new MarkerOptions()
                                    .position( new LatLng( friend.getUserLocalization().getLongitude(), friend.getUserLocalization().getLatitude() ) )
                                    .title( friend.getUsername() )
                                    .snippet( calculateDistance( friend.getUserLocalization() ) )
                    ) );
                }
            } else {
                if ( friendMarkers.containsKey( friend.getId() ) ) {
                    friendMarkers.get( friend.getId() ).setVisible( false );
                    friendMarkers.remove( friend.getId() );
                }
            }
        }
    }

    private void runUpdateMyLocalizationThread() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while ( locationListener == null ) {
                    Thread.yield();
                }
                updateMyPositionThread = new MyPositionUpdateThread( locationListener );
                updateMyPositionThread.start();
            }
        };
        Thread thread = new Thread( runnable );
        thread.start();
    }

    private boolean friendShouldBeShowedOnMap( User friend ) {
        return friend.isUserShownOnMap() && friendPositionKnown( friend );
    }

    private boolean friendPositionKnown( User friend ) {
        return friend.getUserLocalization() != null;
    }

    private String calculateDistance( Localization friendLocalization ) {
        Localization userLocalization = locationListener.getUserLocalization();
        if ( userLocalization != null ) {
            Location userLocation = new Location( "user" );
            userLocation.setLatitude( userLocalization.getLatitude() );
            userLocation.setLongitude( userLocalization.getLongitude() );
            Location friendLocation = new Location( "friend" );
            userLocation.setLatitude( friendLocalization.getLatitude() );
            userLocation.setLongitude( friendLocalization.getLongitude() );

            return "Distance to friend: " + userLocation.distanceTo( friendLocation ) + "m";
        } else {
            return "It's not possible to calculate distance";
        }

    }

    public void switchMapType( MapType mapType) {

        if ( mapType.equals( MapType.HYBRID ) ) {
            mMap.setMapType( GoogleMap.MAP_TYPE_HYBRID );
        } else {
            mMap.setMapType( GoogleMap.MAP_TYPE_NORMAL );
        }

    }

}
