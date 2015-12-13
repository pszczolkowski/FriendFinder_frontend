package bart.friendfinderapp.friends;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.util.List;

import bart.friendfinderapp.R;
import bart.friendfinderapp.invitation.SendInvitationController;

import static bart.friendfinderapp.friends.UserFriends.getUserFriends;

public class UserFriendsActivity extends Activity {

    private ListView friendsListView;
    private FriendListElementAdapter adapter;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_user_friends );

        List< User > userFriends = getUserFriends();

        friendsListView = (ListView) findViewById( R.id.friendsListView );
        adapter = new FriendListElementAdapter( this, userFriends );
        friendsListView.setAdapter( adapter );

        TextView noFriendsText = (TextView) findViewById( R.id.noFriendsTextView );

        if ( userFriends.size() == 0 ) {
            noFriendsText.setVisibility( View.VISIBLE );
            friendsListView.setVisibility( View.INVISIBLE );
        } else {
            friendsListView.setVisibility( View.VISIBLE );
            noFriendsText.setVisibility( View.INVISIBLE );
        }

        Button inviteButton = (Button) findViewById( R.id.inviteFriendButton );
        inviteButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                sendInvitation();
            }
        } );

        Button refreshButton = (Button) findViewById( R.id.refreshFriendsListButton );
        refreshButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                refreshUserFriends();
            }
        } );

        refreshUserFriends();

    }

    private void refreshUserFriends() {
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground( Object[] params ) {
                return UpdateUserFriendsController.sendRequest();
            }

            @Override
            protected void onPostExecute( Object o ) {
                super.onPostExecute( o );
                int responseCode = (int) o;
                if ( responseCode == HttpURLConnection.HTTP_OK ) {
                    adapter.updateUserFriendsList( getUserFriends() );
                }
            }
        };
        asyncTask.execute();
    }

    private void sendInvitation() {
        final AlertDialog.Builder alert = new AlertDialog.Builder( this );
        alert.setTitle( "Invite friend" );
        final EditText usernameInput = new EditText( this );
        alert.setView( usernameInput );

        alert.setPositiveButton( "Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick( DialogInterface dialog, int which ) {
                final String username = usernameInput.getText().toString().trim();
                AsyncTask asyncTask = new AsyncTask() {
                    @Override
                    protected Object doInBackground( Object[] params ) {
                        return SendInvitationController.sendRequest( username );
                    }

                    @Override
                    protected void onPostExecute( Object o ) {
                        super.onPostExecute( o );
                        String response = (String) o;
                        createShortToast( response );
                    }
                };
                asyncTask.execute();

                return;
            }
        } );
        alert.setNegativeButton( "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick( DialogInterface dialog, int which ) {
                return;
            }
        } );
        alert.show();
    }

    private void createShortToast( String message ) {
        Toast.makeText( this, message, Toast.LENGTH_SHORT ).show();
    }
}
