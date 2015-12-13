package bart.friendfinderapp.invitation;

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

import java.util.List;

import bart.friendfinderapp.R;

import static bart.friendfinderapp.invitation.UserInvitations.getUserInvitations;
import static bart.friendfinderapp.invitation.UserInvitations.requestUpdateOfUserInvitations;

public class InvitationActivity extends Activity {

    private InvitationsListElementAdapter adapter;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_invitation );

        requestUpdateOfUserInvitations();
        List< Invitation > invitations = getUserInvitations();

        ListView invitationsListView = (ListView) findViewById( R.id.invitationListView );
        adapter = new InvitationsListElementAdapter( this, this, invitations );
        invitationsListView.setAdapter( adapter );

        TextView noInvitations = (TextView) findViewById( R.id.noInvitationTextView );

        if ( invitations.size() == 0 ) {
            noInvitations.setVisibility( View.VISIBLE );
            invitationsListView.setVisibility( View.INVISIBLE );
        } else {
            invitationsListView.setVisibility( View.VISIBLE );
            noInvitations.setVisibility( View.INVISIBLE );
        }

        Button inviteButton = (Button) findViewById( R.id.inviteButton );
        inviteButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                sendInvitation();
            }
        } );
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
