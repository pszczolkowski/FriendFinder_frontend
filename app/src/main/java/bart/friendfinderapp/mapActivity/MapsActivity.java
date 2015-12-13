package bart.friendfinderapp.mapActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import bart.friendfinderapp.friends.UserFriends;
import bart.friendfinderapp.friends.UserFriendsActivity;
import bart.friendfinderapp.invitation.InvitationActivity;
import bart.friendfinderapp.invitation.UserInvitations;
import bart.friendfinderapp.loginActivity.LoginActivity;
import bart.friendfinderapp.shared.UserCredentials;

import static bart.friendfinderapp.friends.UserFriends.getUserFriends;

public class MapsActivity extends ActionBarActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    MyCurrentLocationListener locationListener;
    private LocationManager locationManager;

    boolean switchMap = true;
    private final Map< String, Marker > friendMarkers = new HashMap<>();
    private MyPositionUpdateThread updateMyPositionThread;
    private UpdateUserFriendsPositionThread updateUserFriendsPositionThread;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_maps );
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById( R.id.map );
        locationManager = (LocationManager) getSystemService( LOCATION_SERVICE );

        mapFragment.getMapAsync( this );

        runUpdateMyLocalizationThread();
        startCheckingForFriendsPositionsChanges();

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
        LocationManager locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            Toast.makeText( this, "GPS is Enabled in your device", Toast.LENGTH_SHORT ).show();
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
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( this );
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
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( this );
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

    private void switchMapType() {

        if ( switchMap ) {
            mMap.setMapType( GoogleMap.MAP_TYPE_HYBRID );
            switchMap = false;
        } else {
            mMap.setMapType( GoogleMap.MAP_TYPE_NORMAL );
            switchMap = true;
        }

    }

    // MENU
    public boolean onCreateOptionsMenu( Menu menu ) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate( R.menu.menu, (android.view.Menu) menu );

        return true;
    }

    // Button click
    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if ( id == R.id.changemap ) {

            switchMapType();

            return true;
        }

        if ( id == R.id.friends ) {
            Intent i = new Intent( getApplicationContext(), UserFriendsActivity.class );
            MyPositionUpdateThread.stopThread();
            startActivity( i );
            return true;
        }

        if ( id == R.id.invitations ) {
            Intent i = new Intent( getApplicationContext(), InvitationActivity.class );
            MyPositionUpdateThread.stopThread();
            startActivity( i );
            return true;
        }

        if ( id == R.id.logout ) {

            if ( updateMyPositionThread != null ) {
                updateMyPositionThread.stopThread();
            }
            if ( updateUserFriendsPositionThread != null ) {
                updateUserFriendsPositionThread.stopThread();
            }
            UserFriends.clear();
            UserInvitations.clear();
            UserCredentials.clear();
            Intent i = new Intent( getApplicationContext(), LoginActivity.class );
            startActivity( i );
            finish();

            return true;
        }

        return super.onOptionsItemSelected( item );
    }


}
