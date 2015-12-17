package bart.friendfinderapp.loginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static bart.friendfinderapp.shared.Constants.APP_URL;
import static bart.friendfinderapp.shared.Constants.GLOBAL_TIMEOUT;

/**
 * Created by Godzio on 2015-11-21.
 */
public class RegisterController {

    private HttpURLConnection connection;
    private URL url;

    /**
     * Method to send registration request to server
     * @param login
     * @param password
     * @return server response code (possible values are: 200, 400 or 500)
     */
    public int tryToRegister( String login, String password ) {
        int responseCode = 0;
        try {
            //Create connection to server
            url = new URL( APP_URL + "/user/register" );
            connection = (HttpURLConnection) url.openConnection();
            //Set request method to POST
            connection.setRequestMethod( "POST" );
            connection.setUseCaches( false );
            //Set headers for connection
            connection.setRequestProperty( "Accept", "application/json" );
            connection.setRequestProperty( "Content-type", "application/json" );
            //Set timeouts for connection
            connection.setConnectTimeout( GLOBAL_TIMEOUT );
            connection.setReadTimeout( GLOBAL_TIMEOUT );

            //Create JSON object that wil be send as request body
            JSONObject request = new JSONObject();
            request.put( "Username", login );
            request.put( "Login", login );
            request.put( "Password", password );
            request.put( "ConfirmPassword", password );

            //Set previously created JSON object as a body of the request
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write( request.toString().getBytes() );
            //Send request to server
            connection.connect();
            //Check the response code of request
            responseCode = connection.getResponseCode();

        } catch ( MalformedURLException e ) {
            e.printStackTrace();
        } catch ( IOException e ) {
            e.printStackTrace();
        } catch ( JSONException e ) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
        return responseCode;
    }
}
