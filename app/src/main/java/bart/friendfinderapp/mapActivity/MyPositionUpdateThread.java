package bart.friendfinderapp.mapActivity;

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
            while ( !threadStopped ) {
                new MyPositionUpdateController().sendMyPosistion( locationListener.getUserLocalization() );
                Thread.sleep( 1000 );
            }
        } catch ( InterruptedException e ) {
            e.printStackTrace();
        }

    }

    public static void stopThread() {
        threadStopped = true;
    }
}
