package bart.friendfinderapp.mapActivity;

/**
 * Created by Godzio on 2015-11-22.
 */
public class Localization {

    private final double longitude;
    private final double latitude;

    public Localization( double longitude, double latitude ) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
}
