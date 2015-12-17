package bart.friendfinderapp.friends;

import java.net.HttpURLConnection;

import bart.friendfinderapp.mapActivity.MapFragment;

import static com.google.android.gms.internal.zzip.runOnUiThread;

/**
 * Created by Godzio on 2015-11-27.
 */
public class UpdateUserFriendsPositionThread extends Thread {

    private final MapFragment map;
    private boolean threadRunning;

    public UpdateUserFriendsPositionThread( MapFragment map ) {
        this.map = map;
        threadRunning = true;
    }

    @Override
    public void run() {
        super.run();
        while ( threadRunning ) {
            int response = new UpdateUserFriendsPositionController().sendRequest();
            if ( response == HttpURLConnection.HTTP_OK ) {
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
