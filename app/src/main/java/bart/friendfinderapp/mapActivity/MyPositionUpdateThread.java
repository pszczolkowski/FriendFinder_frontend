package bart.friendfinderapp.mapActivity;

import static bart.friendfinderapp.mapActivity.MyPositionUpdateController.sendMyPosistion;

/**
 * Created by Godzio on 2015-11-22.
 */
public class MyPositionUpdateThread extends Thread {

    private final MyCurrentLocationListener locationListener;
    boolean threadStopped = false;

    public MyPositionUpdateThread( MyCurrentLocationListener locationListener ) {
        this.locationListener = locationListener;
    }

    @Override
    public void run() {
        super.run();
        try {
            while ( !threadStopped ) {
                sendMyPosistion( locationListener.getUserLocalization() );
                Thread.sleep( 5000 );
            }
        } catch ( InterruptedException e ) {
            e.printStackTrace();
        }

    }

    public void stopThread() {
        threadStopped = true;
    }
}
