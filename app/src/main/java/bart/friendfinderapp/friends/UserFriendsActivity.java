package bart.friendfinderapp.friends;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import bart.friendfinderapp.R;

import static bart.friendfinderapp.friends.UserFriends.getUserFriends;
import static bart.friendfinderapp.friends.UserFriends.requestUpdateOfUserFriends;

public class UserFriendsActivity extends Activity {

    private ListView friendsListView;
    private ArrayAdapter< User > adapter;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_user_friends );

        requestUpdateOfUserFriends();

        List<User> userFriends =  getUserFriends();

        friendsListView = (ListView) findViewById( R.id.friendsListView );
        adapter = new FriendListElementAdapter( this, userFriends );
        friendsListView.setAdapter( adapter );

        TextView noFriendsText = (TextView) findViewById( R.id.noFriendsTextView );

        if(userFriends.size() == 0){
            noFriendsText.setVisibility( View.VISIBLE );
            friendsListView.setVisibility( View.INVISIBLE );
        }else{
            friendsListView.setVisibility( View.VISIBLE );
            noFriendsText.setVisibility( View.INVISIBLE );
        }

    }
}
