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
import java.util.ArrayList;
import java.util.List;

import bart.friendfinderapp.shared.Constants;

import static bart.friendfinderapp.shared.Constants.GLOBAL_TIMEOUT;
import static bart.friendfinderapp.shared.UserCredentials.getUserCredentials;

/**
 * Created by Godzio on 2015-11-26.
 */
public class UpdateUserFriendsController {

    private static HttpURLConnection connection;
    private static URL url;

    public static int sendRequest() {
        int responseCode = 0;

        try {
            url = new URL( Constants.APP_URL + "/user/friend" );
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
                String responseMessage = readResponseBody();
                JSONArray responseAsJson = new JSONArray( responseMessage );
                List< User > friends = castResponseToList( responseAsJson );
                UserFriends.updateUserFriends( friends );
            } else if ( responseCode != HttpURLConnection.HTTP_OK ) {
                logErrorMessage( responseCode );
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

    private static void logErrorMessage( int responseCode ) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( connection.getErrorStream() ) );
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append( "Error code: " ).append( responseCode ).append( " " );
        String line;

        while ( ( line = bufferedReader.readLine() ) != null ) {
            stringBuilder.append( line );
        }
        bufferedReader.close();
        Log.i( "Update User Friends", stringBuilder.toString() );
    }

    private static List< User > castResponseToList( JSONArray responseAsJson ) throws JSONException {
        List< User > users = new ArrayList<>();
        if ( responseAsJson != null ) {
            for ( int i = 0; i < responseAsJson.length(); i++ ) {
                JSONObject singleArrayRecord = responseAsJson.getJSONObject( i );
                users.add( new User( singleArrayRecord.getString( "id" ), singleArrayRecord.getString( "username" ) ) );
            }
        }

        return users;
    }
}
