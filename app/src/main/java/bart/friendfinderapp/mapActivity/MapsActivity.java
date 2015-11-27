package bart.friendfinderapp.mapActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
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

import java.io.File;

import bart.friendfinderapp.R;
import bart.friendfinderapp.loginActivity.LoginActivity;

import static bart.friendfinderapp.shared.Constants.LOGIN_FILE;

public class MapsActivity extends ActionBarActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    MyCurrentLocationListener locationListener;
    private LocationManager locationManager;
    private Thread updateMyLocalizationThread;

    boolean switchMap = true;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        mockFriends( "Sapcio", new Localization( 51.743268, 19.478112 ) );
        mockFriends( "Pufcio", new Localization( 51.741268, 19.472212 ) );
        mockFriends( "Hipcio", new Localization( 51.742268, 19.478412 ) );
        mockFriends( "Lifcio", new Localization( 51.744268, 19.498212 ) );
        setContentView( R.layout.activity_maps );
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById( R.id.map );
        locationManager = (LocationManager) getSystemService( LOCATION_SERVICE );

        mapFragment.getMapAsync( this );



        runUpdateMyLocalizationThread();
    }

    private void runUpdateMyLocalizationThread() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while ( locationListener == null ) {
                    Thread.yield();
                }
                Thread updateMyPositionThread = new MyPositionUpdateThread( locationListener );
                updateMyPositionThread.start();
            }
        };
        Thread thread = new Thread( runnable );
        thread.start();
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
        mMap.setOnMyLocationChangeListener(locationListener);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

    }

    // OBSLUGA GPS
    public void getPositionFromGPS( MyCurrentLocationListener locationListener ) {
        LocationManager locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            Toast.makeText( this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT ).show();
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
                .setNegativeButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
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
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void switchMapType() {

        if(switchMap){
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            switchMap=false;
        }
        else{
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            switchMap=true;
        }

    }


    // MENU
    public boolean onCreateOptionsMenu( Menu menu ) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, (android.view.Menu) menu);

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
            return true;
        }

        if ( id == R.id.options ) {
            return true;
        }

        if ( id == R.id.add ) {
            return true;
        }

        if ( id == R.id.showhide ) {
            return true;
        }

        if ( id == R.id.logout ) {

            //trying to delete file with user settings
            new File( LOGIN_FILE ).delete();

            Intent i = new Intent( getApplicationContext(), LoginActivity.class );
            startActivity( i );
            finish();

            return true;
        }

        return super.onOptionsItemSelected( item );
    }




}
