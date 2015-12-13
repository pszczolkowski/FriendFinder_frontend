package bart.friendfinderapp.invitation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static bart.friendfinderapp.invitation.GetInvitationController.sendRequest;

/**
 * Created by Godzio on 2015-12-12.
 */
public class UserInvitations {

    private static Map< Integer, Invitation > invitationMap = new HashMap<>();

    public static List< Invitation > getUserInvitations() {
        return new ArrayList<>( invitationMap.values() );
    }

    public static void updateUserInvitation( List< Invitation > updatedList ) {
        for ( Invitation invitation : updatedList ) {
            if ( !invitationMap.containsKey( invitation.getId() ) ) {
                invitationMap.put( invitation.getId(), invitation );
            }
        }
    }

    public static void requestUpdateOfUserInvitations() {
        Thread thread = new Thread( new Runnable() {
            @Override
            public void run() {
                sendRequest();
            }
        } );
        thread.start();
    }

    public static void remove( int id ) {
        invitationMap.remove( id );
    }

    public static void clear(){
        invitationMap.clear();
    }
}
