package bart.friendfinderapp.friends;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import bart.friendfinderapp.R;

/**
 * Created by Godzio on 2015-11-27.
 */
public class FriendListElementAdapter extends ArrayAdapter< User > {
    private final Context context;
    private List< User > friends;

    public FriendListElementAdapter( Context context, List< User > friends ) {
        super( context, R.layout.single_friend_element, friends );
        this.context = context;
        this.friends = friends;
    }
    public void updateUserFriendsList ( List<User> friends){
        this.friends = friends;
        this.notifyDataSetChanged();
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent ) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        final User friend = friends.get( position );
        View singleRowView = inflater.inflate( R.layout.single_friend_element, parent, false );
        TextView usernameView = (TextView) singleRowView.findViewById( R.id.friendUsernameTextView );
        usernameView.setText( friend.getUsername() );
        final Switch showHideSwitch = (Switch) singleRowView.findViewById( R.id.showHideSwitch );
        showHideSwitch.setChecked( friend.isUserShownOnMap() );
        if ( friend.isUserShownOnMap() ) {
            showHideSwitch.setText( "Shown" );
        } else {
            showHideSwitch.setText( "Hidden" );
        }
        showHideSwitch.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged( CompoundButton buttonView, boolean isChecked ) {
                friend.changeIsUserShownOnMap();
                if ( friend.isUserShownOnMap() ) {
                    showHideSwitch.setText( "Shown" );
                } else {
                    showHideSwitch.setText( "Hidden" );
                }
            }
        } );

        return singleRowView;
    }
}
