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
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import bart.friendfinderapp.LocalUser;
import bart.friendfinderapp.R;

public class MapsActivity extends ActionBarActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    MyCurrentLocationListener locationListener;
    private LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        locationManager = (LocationManager) getSystemService( LOCATION_SERVICE );
        mapFragment.getMapAsync( this );


//        inputYourName();

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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationListener = new MyCurrentLocationListener( mMap );
        getPositionFromGPS( locationListener );
        mMap.setOnMyLocationChangeListener( locationListener );
        mMap.setMyLocationEnabled( true );
        mMap.getUiSettings().setZoomControlsEnabled(true);

    }

    // OBSLUGA GPS
    public void getPositionFromGPS(MyCurrentLocationListener locationListener)
    {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
        } else {
            showGPSDisabledAlertToUser();
        }
        try {
            Criteria criteria = new Criteria( );
            String provider = locationManager.getBestProvider( criteria, true );
            locationManager.requestLocationUpdates( provider, 1000, 0, locationListener );
        }catch (SecurityException missingPermission){
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
    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
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

    // INPUT YOUR NAME
    public void inputYourName(){

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Enter your name:");

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        //BUTTON OK
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String inputName = input.getText().toString();
                createLocalUser(inputName);

                return;
            }
        });

        /*alert.setNegativeButton("Login as guest.", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                return;
            }
        });*/
        alert.show();
    }

    //TWORZENIE LOCAL USERA
    public void createLocalUser(String inputName){

        LocalUser user = new LocalUser(inputName);
    }

    // MENU
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, (android.view.Menu) menu);
        return true;
    }



}
