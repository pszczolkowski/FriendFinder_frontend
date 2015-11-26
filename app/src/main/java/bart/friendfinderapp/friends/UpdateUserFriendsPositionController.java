package bart.friendfinderapp.friends;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import bart.friendfinderapp.mapActivity.Localization;

import static bart.friendfinderapp.shared.Constants.APP_URL;
import static bart.friendfinderapp.shared.Constants.GLOBAL_TIMEOUT;
import static bart.friendfinderapp.shared.UserCredentials.getUserCredentials;

/**
 * Created by Godzio on 2015-11-26.
 */
public class UpdateUserFriendsPositionController {
    private static HttpURLConnection connection;
    private static URL url;

    public static int sendRequest() {
        int responseCode = 0;
        try {
            url = new URL( APP_URL + "/user/location" );
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod( "GET" );
            connection.setUseCaches( false );
            connection.setRequestProperty( "Accept", "application/json" );
            connection.setRequestProperty( "Authorization", "Bearer " + getUserCredentials().getToken() );
            connection.setConnectTimeout( GLOBAL_TIMEOUT );
            connection.setReadTimeout( GLOBAL_TIMEOUT );

            connection.connect();

            responseCode = connection.getResponseCode();
            if ( responseCode == HttpURLConnection.HTTP_OK ) {
                String responseAsString = readResponseBody();
                JSONArray responseAsJson = new JSONArray( responseAsString );
                Map< String, Localization > friendLocalizations = convertResponseToMap( responseAsJson );
                UserFriends.updateFriendsLocalizations( friendLocalizations );
            } else if ( responseCode != HttpURLConnection.HTTP_OK ) {
                logErrorMessage();
            }
        } catch ( MalformedURLException e ) {
            e.printStackTrace();
        } catch ( IOException e ) {
            e.printStackTrace();
        } catch ( JSONException e ) {
            e.printStackTrace();
        }
        return responseCode;
    }

    private static String readResponseBody() throws IOException {

        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );
        StringBuilder stringBuilder = new StringBuilder();
        String line;

        while ( ( line = bufferedReader.readLine() ) != null ) {
            stringBuilder.append( line );
        }
        bufferedReader.close();
        return stringBuilder.toString();
    }

    private static void logErrorMessage() throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( connection.getErrorStream() ) );
        StringBuilder stringBuilder = new StringBuilder();
        String line;

        while ( ( line = bufferedReader.readLine() ) != null ) {
            stringBuilder.append( line );
        }
        bufferedReader.close();
        Log.i( "Server error message", stringBuilder.toString() );
    }

    private static Map< String, Localization > convertResponseToMap( JSONArray responseAsJsonArray ) throws JSONException {
        Map< String, Localization > friendsLocalizations = new HashMap<>();
        if ( responseAsJsonArray != null ) {
            for ( int i = 0; i < responseAsJsonArray.length(); i++ ) {
                JSONObject singleRecord = responseAsJsonArray.getJSONObject( i );
                friendsLocalizations.put( singleRecord.getString( "FriendId" ), new Localization( singleRecord.getDouble( "Longitude" ), singleRecord.getDouble( "Latitude" ) ) );
            }
        }
        return friendsLocalizations;
    }
}
