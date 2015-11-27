package bart.friendfinderapp.mapActivity;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static bart.friendfinderapp.shared.Constants.APP_URL;
import static bart.friendfinderapp.shared.Constants.GLOBAL_TIMEOUT;
import static bart.friendfinderapp.shared.UserCredentials.getUserCredentials;

/**
 * Created by Godzio on 2015-11-22.
 */
public class MyPositionUpdateController {

    private static URL url;
    private static HttpURLConnection connection;

    public static int sendMyPosistion( Localization userLocalization ) {
        int responseCode = 0;
        if ( userLocalization != null ) {
            double longitude = userLocalization.getLongitude();
            double latitude = userLocalization.getLatitude();

            try {
                url = new URL( APP_URL + "/user/position" );
                connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod( "POST" );
                connection.setUseCaches( false );

                connection.setRequestProperty( "Accept", "application/json" );
                connection.setRequestProperty( "Content-Type", "application/json" );
                connection.setRequestProperty( "Authorization", "Bearer " + getUserCredentials().getToken() );

                connection.setConnectTimeout( GLOBAL_TIMEOUT );
                connection.setReadTimeout( GLOBAL_TIMEOUT );

                JSONObject request = new JSONObject();
                request.put( "Longitude", longitude );
                request.put( "Latitude", latitude );

                OutputStream outputStream = connection.getOutputStream();
                outputStream.write( request.toString().getBytes() );

                connection.connect();
                //Check the response code of request
                responseCode = connection.getResponseCode();
                if ( responseCode != HttpURLConnection.HTTP_OK ) {
                    logErrorMessage(responseCode);
                }
            } catch ( MalformedURLException e ) {
                e.printStackTrace();
            } catch ( IOException e ) {
                e.printStackTrace();
            } catch ( JSONException e ) {
                e.printStackTrace();
            } finally {
                connection.disconnect();
            }
        }
        return responseCode;
    }

    private static void logErrorMessage(int responseCode) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( connection.getErrorStream() ) );
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append( "Error code: " ).append( responseCode ).append( " " );
        String line;

        while ( ( line = bufferedReader.readLine() ) != null ) {
            stringBuilder.append( line );
        }
        bufferedReader.close();
        Log.i( "UserPositionUpdate", stringBuilder.toString() );
    }
}
