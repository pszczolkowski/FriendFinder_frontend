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
public class SendInvitationController {

    private URL url;
    private HttpURLConnection connection;
    private String response;

    public String sendRequest( String username ) {
        response = "";
        try {
            url = new URL( APP_URL + "/user/" + username + "/invite" );
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod( "POST" );
            connection.setUseCaches( false );

            connection.setRequestProperty( "Accept", "application/json" );
            connection.setRequestProperty( "Content-Type", "application/json" );
            connection.setRequestProperty( "Authorization", "Bearer " + getUserCredentials().getToken() );

            connection.setConnectTimeout( GLOBAL_TIMEOUT );
            connection.setReadTimeout( GLOBAL_TIMEOUT );

            connection.connect();
            int responseCode = connection.getResponseCode();

            if ( responseCode != HttpURLConnection.HTTP_OK ) {
                logErrorMessage( responseCode );
            } else {
                response = "Invitation send";
            }
        } catch ( MalformedURLException e ) {
            e.printStackTrace();
        } catch ( ProtocolException e ) {
            e.printStackTrace();
        } catch ( IOException e ) {
            e.printStackTrace();
        }finally {
            connection.disconnect();
        }

        return response;
    }

    private void logErrorMessage( int responseCode ) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( connection.getErrorStream() ) );
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append( "Error code: " ).append( responseCode ).append( " " );
        String line;

        while ( ( line = bufferedReader.readLine() ) != null ) {
            stringBuilder.append( line );
        }
        bufferedReader.close();
        Log.i( "Send invite error", stringBuilder.toString() );
        response = stringBuilder.toString().replace( "\"", "" ).replace( "{", "" ).replace( "}", "" ).split( ":" )[2];
    }
}
