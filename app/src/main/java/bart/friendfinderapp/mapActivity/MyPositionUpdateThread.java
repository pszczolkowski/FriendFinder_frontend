package bart.friendfinderapp.mapActivity;

import static bart.friendfinderapp.mapActivity.MyPositionUpdateController.sendMyPosistion;

/**
 * Created by Godzio on 2015-11-22.
 */
public class MyPositionUpdateThread extends Thread {

    private final MyCurrentLocationListener locationListener;
    private static boolean threadStopped;

    public MyPositionUpdateThread( MyCurrentLocationListener locationListener ) {
        threadStopped = false;
        this.locationListener = locationListener;
    }

    @Override
    public void run() {
        super.run();
        try {
            while ( true ) {
                if ( !threadStopped ) {
                    sendMyPosistion( locationListener.getUserLocalization() );
                }
                Thread.sleep( 5000 );
            }
        } catch ( InterruptedException e ) {
            e.printStackTrace();
        }

    }

    public static void stopThread() {
        threadStopped = true;
    }

    public static void restartThread() {
        threadStopped = false;
    }
}
