package bart.friendfinderapp.invitation;

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

import static bart.friendfinderapp.shared.Constants.APP_URL;
import static bart.friendfinderapp.shared.Constants.GLOBAL_TIMEOUT;
import static bart.friendfinderapp.shared.UserCredentials.getUserCredentials;

/**
 * Created by Godzio on 2015-12-05.
 */
public class GetInvitationController {

    private static HttpURLConnection connection;
    private static URL url;

    public static int sendRequest() {
        int responseCode = 0;
        try {
            url = new URL( APP_URL + "/invitation" );
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
                List< Invitation > invitations = castResponseToList( responseAsJson );
                UserInvitations.updateUserInvitation( invitations );
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
        Log.i( "Get invitations error", stringBuilder.toString() );
    }

    private static List< Invitation > castResponseToList( JSONArray responseAsJson ) throws JSONException {
        List< Invitation > invitations = new ArrayList<>();
        if ( responseAsJson != null ) {
            for ( int i = 0; i < responseAsJson.length(); i++ ) {
                JSONObject singleArrayRecord = responseAsJson.getJSONObject( i );
                invitations.add( new Invitation( singleArrayRecord.getInt( "id" ), singleArrayRecord.getString( "invitingId" ), singleArrayRecord.getString( "invitingName" ), singleArrayRecord.getString( "sentAt" ) ) );
            }
        }

        return invitations;
    }

}
