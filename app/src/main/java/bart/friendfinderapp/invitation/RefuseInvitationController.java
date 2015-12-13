package bart.friendfinderapp.invitation;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import static bart.friendfinderapp.shared.Constants.APP_URL;
import static bart.friendfinderapp.shared.Constants.GLOBAL_TIMEOUT;
import static bart.friendfinderapp.shared.UserCredentials.getUserCredentials;

/**
 * Created by Godzio on 2015-12-12.
 */
public class RefuseInvitationController {

    private static URL url;
    private static HttpURLConnection connection;

    public static int sendRequest( int invitationId ) {
        int responseCode = 0;
        try {
            url = new URL( APP_URL + "/user/" + invitationId + "/accept" );
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod( "POST" );
            connection.setUseCaches( false );

            connection.setRequestProperty( "Accept", "application/json" );
            connection.setRequestProperty( "Content-Type", "application/json" );
            connection.setRequestProperty( "Authorization", "Bearer " + getUserCredentials().getToken() );

            connection.setConnectTimeout( GLOBAL_TIMEOUT );
            connection.setReadTimeout( GLOBAL_TIMEOUT );

            connection.connect();
            responseCode = connection.getResponseCode();

            if ( responseCode != HttpURLConnection.HTTP_OK ) {
                logErrorMessage( responseCode );
            }
        } catch ( MalformedURLException e ) {
            e.printStackTrace();
        } catch ( ProtocolException e ) {
            e.printStackTrace();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        return responseCode;
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
        Log.i( "Refuse invitation error", stringBuilder.toString() );
    }
}
