package bart.friendfinderapp.invitation;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.util.List;

import bart.friendfinderapp.R;

import static bart.friendfinderapp.invitation.UserInvitations.getUserInvitations;

/**
 * Created by Godzio on 2015-12-12.
 */
public class InvitationsListElementAdapter extends ArrayAdapter< Invitation > {
    private final Context context;
    private List< Invitation > invitations;
    private final InvitationsFragment invitationFragment;

    public InvitationsListElementAdapter( Context context, InvitationsFragment invitationFragment, List< Invitation > invitations ) {
        super( context, R.layout.single_invitation_element, invitations );
        this.context = context;
        this.invitations = invitations;
        this.invitationFragment = invitationFragment;
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent ) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        final Invitation invitation = invitations.get( position );
        View singleRowView = inflater.inflate( R.layout.single_invitation_element, parent, false );
        TextView usernameView = (TextView) singleRowView.findViewById( R.id.invitationSender );
        usernameView.setText( invitation.getInviterUsername() );

        Button acceptButton = (Button) singleRowView.findViewById( R.id.buttonAccept );
        acceptButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {

                AsyncTask asyncTask = new AsyncTask() {
                    @Override
                    protected Object doInBackground( Object[] params ) {
                        return new AcceptInvitationController().sendRequest( invitation.getId() );
                    }
                    @Override
                    protected void onPostExecute( Object o ) {
                        super.onPostExecute( o );
                        int responseCode = (int) o;
                        if ( responseCode == HttpURLConnection.HTTP_OK ) {
                            createShortToast( "Invitation accepted" );
                            UserInvitations.remove( invitation.getId() );
                            InvitationsListElementAdapter.this.remove( invitation );
                            notifyDataSetChanged();
                        } else if ( responseCode == HttpURLConnection.HTTP_BAD_REQUEST ) {
                            createShortToast( "Invitation not found" );
                        } else if ( responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR ) {
                            createShortToast( "We encountered internal error on server. Please try again later" );
                        } else {
                            createShortToast( "Unknown error occurred - try to check your internet connection or try again later" );
                        }
                    }
                };
                asyncTask.execute(  );
            }
        } );

        Button declineButton = (Button) singleRowView.findViewById( R.id.buttonDecline );
        declineButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                AsyncTask asyncTask = new AsyncTask() {
                    @Override
                    protected Object doInBackground( Object[] params ) {
                        return new RefuseInvitationController().sendRequest( invitation.getId() );
                    }
                    @Override
                    protected void onPostExecute( Object o ) {
                        super.onPostExecute( o );
                        int responseCode = (int) o;
                        if ( responseCode == HttpURLConnection.HTTP_OK ) {
                            createShortToast( "Invitation refused" );
                            UserInvitations.remove( invitation.getId() );
                            InvitationsListElementAdapter.this.remove( invitation );
                            notifyDataSetChanged();
                        } else if ( responseCode == HttpURLConnection.HTTP_BAD_REQUEST ) {
                            createShortToast( "Invitation not found" );
                        } else if ( responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR ) {
                            createShortToast( "We encountered internal error on server. Please try again later" );
                        } else {
                            createShortToast( "Unknown error occurred - try to check your internet connection or try again later" );
                        }
                    }
                };
                asyncTask.execute(  );
            }
        } );

        return singleRowView;
    }

    public void updateList(){
        invitations = getUserInvitations();
        notifyDataSetChanged();
    }

    private void createShortToast( String message ) {
        Log.d( "response", message );
        Toast.makeText( context, message, Toast.LENGTH_SHORT ).show();
    }
}
