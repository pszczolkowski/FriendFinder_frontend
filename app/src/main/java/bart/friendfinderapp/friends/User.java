package bart.friendfinderapp.friends;

import bart.friendfinderapp.mapActivity.Localization;

/**
 * Created by Bart on 2015-11-06.
 * Updated:
 * This class no longer holds data about current user - instead it is a class that hold information about friend.
 * Stored information:
 * String Username - friend username, cannot be null, required in constructor, read only
 * String Id - friend id in app, cannot be null, required in constructor, read only
 * Boolean isUserShownOnMap - boolean value that decide if user have his marker on map or not, by default in constructor set to true, can be changed through metod
 * <p/>
 * Localization - information about user position, null be default, set through method.
 */
public class User {
    private String username;
    private String id;
    private boolean isUserShownOnMap;

    private Localization userLocalization;

    public User( String id, String username ) {
        this.username = username;
        this.id = id;
        isUserShownOnMap = true;
    }

    public String getUsername() {
        return username;
    }

    public String getId() {
        return id;
    }

    public void changeIsUserShownOnMap( boolean isShowed ) {
        this.isUserShownOnMap = isShowed;
    }

    public void setUserLocalization( Localization localization ) {
        this.userLocalization = localization;
    }

    public Localization getUserLocalization() {
        return userLocalization;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof User ) ) return false;

        User user = (User) o;

        return id.equals( user.id );

    }

}
