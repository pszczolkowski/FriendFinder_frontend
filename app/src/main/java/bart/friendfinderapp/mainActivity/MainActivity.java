package bart.friendfinderapp.mainActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import bart.friendfinderapp.R;
import bart.friendfinderapp.friends.FragmentUserFriends;
import bart.friendfinderapp.friends.UserFriends;
import bart.friendfinderapp.invitation.FragmentUserInvitations;
import bart.friendfinderapp.invitation.UserInvitations;
import bart.friendfinderapp.loginActivity.LoginActivity;
import bart.friendfinderapp.mapActivity.MapFragment;
import bart.friendfinderapp.mapActivity.MapType;
import bart.friendfinderapp.shared.UserCredentials;

import static bart.friendfinderapp.shared.UserCredentials.getUserCredentials;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private Fragment fragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );



        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
        drawer.setDrawerListener( toggle );
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById( R.id.nav_view );
        navigationView.setNavigationItemSelectedListener( this );

        View headerView = navigationView.inflateHeaderView( R.layout.nav_header_main );
        TextView username = ( TextView )headerView.findViewById( R.id.username );
        UserCredentials userCredentials= getUserCredentials();
        if( username != null && userCredentials != null ){
            username.setText( userCredentials.getLogin() );
        }

        fragmentManager = getSupportFragmentManager();
        fragment = MapFragment.newInstance();
        fragmentManager.beginTransaction().replace( R.id.main_fragment, fragment ).commit();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        if ( drawer.isDrawerOpen( GravityCompat.START ) ) {
            drawer.closeDrawer( GravityCompat.START );
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate( R.menu.main, menu );
        return false;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if ( id == R.id.action_settings ) {
            return true;
        }

        return super.onOptionsItemSelected( item );
    }

    @SuppressWarnings( "StatementWithEmptyBody" )
    @Override
    public boolean onNavigationItemSelected( MenuItem item ) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if ( id == R.id.nav_map ) {
            fragment = MapFragment.newInstance();
            fragmentManager.beginTransaction().replace( R.id.main_fragment, fragment ).commit();
        } else if ( id == R.id.nav_invitations ) {
            fragment = FragmentUserInvitations.newInstance();
            fragmentManager.beginTransaction().replace( R.id.main_fragment, fragment ).commit();
        } else if ( id == R.id.nav_friends ) {
            fragment = FragmentUserFriends.newInstance();
            fragmentManager.beginTransaction().replace( R.id.main_fragment, fragment ).commit();
        } else if ( id == R.id.nav_default_map_type ) {
            if(fragment.getClass().equals( MapFragment.class )){
                ((MapFragment) fragment).switchMapType( MapType.DEFAULT );
            }
        } else if ( id == R.id.nav_satellite_map_type ) {
            if(fragment.getClass().equals( MapFragment.class )){
                ((MapFragment) fragment).switchMapType( MapType.HYBRID );
            }
        } else if( id == R.id.nav_logout){
            UserFriends.clear();
            UserInvitations.clear();
            UserCredentials.clear();

            Intent intent = new Intent( getApplicationContext(), LoginActivity.class );
            startActivity( intent );
            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        drawer.closeDrawer( GravityCompat.START );
        return true;
    }

}
