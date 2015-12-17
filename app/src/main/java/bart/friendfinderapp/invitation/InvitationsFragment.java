package bart.friendfinderapp.invitation;

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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.util.List;

import bart.friendfinderapp.R;

import static bart.friendfinderapp.invitation.UserInvitations.getUserInvitations;

public class InvitationsFragment extends Fragment {

    private InvitationsListElementAdapter adapter;
    private LinearLayout fragmentLayout;

    public InvitationsFragment(){}
    
    public static Fragment newInstance(){
        InvitationsFragment fragment = new InvitationsFragment();
        Bundle args = new Bundle();
        fragment.setArguments( args );
        return fragment;
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        fragmentLayout = (LinearLayout) inflater.inflate( R.layout.fragment_user_invitation, container, false );

        refreshInvitationList();
        List< Invitation > invitations = getUserInvitations();

        ListView invitationsListView = (ListView) fragmentLayout.findViewById( R.id.invitationListView );
        adapter = new InvitationsListElementAdapter( super.getActivity(), this, invitations );
        invitationsListView.setAdapter( adapter );

        TextView noInvitations = (TextView) fragmentLayout.findViewById( R.id.noInvitationTextView );

        if ( invitations.size() == 0 ) {
            noInvitations.setVisibility( View.VISIBLE );
            invitationsListView.setVisibility( View.INVISIBLE );
        } else {
            invitationsListView.setVisibility( View.VISIBLE );
            noInvitations.setVisibility( View.INVISIBLE );
        }

        Button inviteButton = (Button) fragmentLayout.findViewById( R.id.inviteButton );
        inviteButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                sendInvitation();
            }
        } );

        Button refreshButton = (Button) fragmentLayout.findViewById( R.id.refreshInvitationListButton);
        refreshButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                refreshInvitationList();
            }
        } );

        return fragmentLayout;
    }

    private void refreshInvitationList() {
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground( Object[] params ) {
                return new GetInvitationController().sendRequest();
            }

            @Override
            protected void onPostExecute( Object o ) {
                super.onPostExecute( o );
                int responseCode = (int) o;
                if ( responseCode == HttpURLConnection.HTTP_OK ) {
                    adapter.updateList();
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
