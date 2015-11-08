package bart.friendfinderapp;

import android.location.LocationListener;

/**
 * Created by Bart on 2015-11-06.
 */
public class LocalUser {
    public String Name;
    double latitude;
    double longitude;

    public LocalUser(String Name){
        this.Name=Name;
    }

    public LocalUser(String Name, double latitude, double longitude){
        this.Name=Name;
        this.latitude=latitude;
        this.longitude=longitude;
    }

    public String getName(){
        return Name;
    }
}
