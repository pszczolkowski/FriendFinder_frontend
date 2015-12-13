package bart.friendfinderapp.friends;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bart.friendfinderapp.mapActivity.Localization;

import static bart.friendfinderapp.friends.UpdateUserFriendsController.sendRequest;

/**
 * Created by Godzio on 2015-11-26.
 * This class stores information about user friends
 */
public class UserFriends {

    private static Map< String, User > userFriends = new HashMap<>();
    private static List< String > mockedUsersIds = new ArrayList<>();

    /**
     * Method to get User Friends as List
     *
     * @return
     */
    public static List< User > getUserFriends() {
        return new ArrayList<>( userFriends.values() );
    }

    /**
     * Method to update userFriends. For every User on list we check if map with userFriends contains key with value of UserId, if not we add new Friend.
     *
     * @param updatedList
     */
    public static void updateUserFriends( List< User > updatedList ) {
        for ( User friend : updatedList ) {
            if ( !userFriends.containsKey( friend.getId() ) ) {
                userFriends.put( friend.getId(), friend );
            }
        }
    }

    /**
     * Method to update localization informations of userFriends. For every key (UserId) in map with localization, we check if map with userFriends contains such User.
     * If user was found on userFriendsMap method update it's Localization data.
     *
     * @param friendLocalizations
     */
    public static void updateFriendsLocalizations( Map< String, Localization > friendLocalizations ) {
        for ( String friendId : friendLocalizations.keySet() ) {
            if ( userFriends.containsKey( friendId ) ) {
                userFriends.get( friendId ).setUserLocalization( friendLocalizations.get( friendId ) );
            }
        }
    }

    public static void requestUpdateOfUserFriends() {
        Thread thread = new Thread( new Runnable() {
            @Override
            public void run() {
                sendRequest();
            }
        } );
        thread.start();
    }

    public static void mockFriends( String string, Localization localization ) {
        User user = new User( string, string );
        user.setUserLocalization( localization );
        userFriends.put( string, user );
        mockedUsersIds.add( string );
    }

    public static void moveMocks() {
        for ( String mockId : mockedUsersIds ) {
            User mockedUser = userFriends.get( mockId );
            Localization mockedUserLocalization = mockedUser.getUserLocalization();

            mockedUser.setUserLocalization( new Localization( mockedUserLocalization.getLongitude() + 0.001, mockedUserLocalization.getLatitude() + 0.001 ) );
        }
    }

    public static void clear(){
        userFriends.clear();
        mockedUsersIds.clear();
    }
}
