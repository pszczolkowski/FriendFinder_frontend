package bart.friendfinderapp.friends;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bart.friendfinderapp.mapActivity.Localization;

/**
 * Created by Godzio on 2015-11-26.
 */
public class FriendsList {

    private static Map< String, User > userFriends = new HashMap<>();

    public static Map<String, User> getUserFriends(){
        return userFriends;
    }

    public static void updateUserFriendsList( List< User > updatedList ) {
        for ( User friend : updatedList ) {
            if ( !userFriends.containsKey( friend.getId() ) ) {
                userFriends.put( friend.getId(), friend );
            }
        }
    }

    public static void updateFriendsLocalizations( Map< String, Localization > friendLocalizations ) {
        for ( String friendId : friendLocalizations.keySet() ) {
            if ( userFriends.containsKey( friendId ) ) {
                userFriends.get( friendId ).setUserLocalization( friendLocalizations.get( friendId ) );
            }
        }
    }
}
