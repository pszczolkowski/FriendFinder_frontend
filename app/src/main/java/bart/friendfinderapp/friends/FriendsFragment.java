package bart.friendfinderapp.friends;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.util.List;

import bart.friendfinderapp.R;
import bart.friendfinderapp.invitation.SendInvitationController;

import static bart.friendfinderapp.friends.UserFriends.getUserFriends;

public class FriendsFragment extends Fragment {

    private ListView friendsListView;
    private FriendListElementAdapter adapter;

    public FriendsFragment() {
    }

    public static Fragment newInstance() {
        FriendsFragment fragment = new FriendsFragment();
        Bundle args = new Bundle();
        fragment.setArguments( args );
        return fragment;
    }
    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        FrameLayout fragmentLayout = (FrameLayout) inflater.inflate( R.layout.fragment_user_friends, container, false );
        refreshUserFriends();

        List< User > userFriends = getUserFriends();

        friendsListView = (ListView) fragmentLayout.findViewById( R.id.friendsListView );
        adapter = new FriendListElementAdapter( super.getActivity(), userFriends );
        friendsListView.setAdapter( adapter );

        TextView noFriendsText = (TextView) fragmentLayout.findViewById( R.id.noFriendsTextView );

        if ( userFriends.size() == 0 ) {
            noFriendsText.setVisibility( View.VISIBLE );
            friendsListView.setVisibility( View.INVISIBLE );
        } else {
            friendsListView.setVisibility( View.VISIBLE );
            noFriendsText.setVisibility( View.INVISIBLE );
        }

        Button inviteButton = (Button) fragmentLayout.findViewById( R.id.inviteFriendButton );
        inviteButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                sendInvitation();
            }
        } );

        Button refreshButton = (Button) fragmentLayout.findViewById( R.id.refreshFriendsListButton );
        refreshButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                refreshUserFriends();
            }
        } );

        return fragmentLayout;
    }

    private void refreshUserFriends() {
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground( Object[] params ) {
                return new UpdateUserFriendsController().sendRequest();
            }

            @Override
            protected void onPostExecute( Object o ) {
                super.onPostExecute( o );
                int responseCode = (int) o;
                if ( responseCode == HttpURLConnection.HTTP_OK ) {
                    adapter.updateUserFriendsList( getUserFriends() );
                    createShortToast( "Refreshed" );
                }
            }
        };
        asyncTask.execute();
    }

    private void sendInvitation() {
        final AlertDialog.Builder alert = new AlertDialog.Builder( super.getActivity() );
        alert.setTitle( "Invite friend" );
        final EditText usernameInput = new EditText( super.getActivity() );
        alert.setView( usernameInput );

        alert.setPositiveButton( "Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick( DialogInterface dialog, int which ) {
                final String username = usernameInput.getText().toString().trim();
                AsyncTask asyncTask = new AsyncTask() {
                    @Override
                    protected Object doInBackground( Object[] params ) {
                        return new SendInvitationController().sendRequest( username );
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
        Toast.makeText( super.getActivity(), message, Toast.LENGTH_SHORT ).show();
    }
}
