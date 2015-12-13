package bart.friendfinderapp.friends;

import java.net.HttpURLConnection;

import bart.friendfinderapp.mapActivity.MapsActivity;

import static bart.friendfinderapp.friends.UpdateUserFriendsPositionController.sendRequest;
import static com.google.android.gms.internal.zzid.runOnUiThread;

/**
 * Created by Godzio on 2015-11-27.
 */
public class UpdateUserFriendsPositionThread extends Thread {

    private final MapsActivity map;
    private boolean threadRunning;

    public UpdateUserFriendsPositionThread( MapsActivity map ) {
        this.map = map;
        threadRunning = true;
    }

    @Override
    public void run() {
        super.run();
        while ( threadRunning ) {
            int response = sendRequest();
            if ( response == HttpURLConnection.HTTP_OK ) {
//                UserFriends.moveMocks();
                runOnUiThread( new Runnable() {
                    @Override
                    public void run() {
                        map.updateFriendsMarkers();
                    }
                } );
                try {
                    Thread.sleep( 1000 );
                } catch ( InterruptedException e ) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void stopThread() {
        this.threadRunning = false;
    }

}
