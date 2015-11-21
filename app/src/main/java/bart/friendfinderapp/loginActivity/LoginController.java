package bart.friendfinderapp.loginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static bart.friendfinderapp.shared.Constants.APP_URL;
import static bart.friendfinderapp.shared.Constants.GLOBAL_TIMEOUT;
import static bart.friendfinderapp.shared.UserCredentials.createUserCredentials;

/**
 * Created by Godzio on 2015-11-21.
 */
public class LoginController {

    private static HttpURLConnection connection;
    private static URL url;

    /**
     * Method to send login request to server
     * @param login
     * @param password
     * @return server response code (possible values are: 200, 400 or 500)
     */
    public static int tryToSignIn( String login, String password ) {
        int responseCode = 0;
        try {
            //Create connection to server
            url = new URL( APP_URL + "/token" );
            connection = (HttpURLConnection) url.openConnection();
            //Set request method to POST
            connection.setRequestMethod( "POST" );
            connection.setUseCaches( false );
            //Set headers for connection
            connection.setRequestProperty( "Accept", "application/json" );
            connection.setRequestProperty( "Content-type", "x-www-form-urlencoded" );
            //Set timeouts for connection
            connection.setConnectTimeout( GLOBAL_TIMEOUT );
            connection.setReadTimeout( GLOBAL_TIMEOUT );
            //Create String that wil be send to server - format of this string is based on x-www-form-urlencoded
            //Correct format for x-www-form-urlencoded is: valueName=value and values are chained by '&'
            String request = "username=" + login + "&password=" + password + "&grant_type=password";

            //Set request body
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write( request.getBytes() );

            //Send the request
            connection.connect();

            //check server response - 200 if OK, 400 if requested values were incorrect, and 500 if there was some problems with server
            responseCode = connection.getResponseCode();
            if ( responseCode == HttpURLConnection.HTTP_OK ) {
                String responseAsString = readResponseBody();
                createUserCredentialsFromResponse( responseAsString );
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
        return responseCode;
    }

    /**
     * Method to create user Credentials based on server response
     * @param responseAsString
     * @throws JSONException
     */
    private static void createUserCredentialsFromResponse( String responseAsString ) throws JSONException {
        JSONObject responseAsJsonObject = new JSONObject( responseAsString );
        String token = responseAsJsonObject.getString( "access_token" );
        String username = responseAsJsonObject.getString( "userName" );
        String expires = responseAsJsonObject.getString( ".expires" );

        createUserCredentials( username, token, expires );
    }

    /**
     * Method to extract body from server response
     * @return Server response as concatenated String
     * @throws IOException
     */
    private static String readResponseBody() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        InputStream inputStream = connection.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader( inputStream );
        BufferedReader bufferedReader = new BufferedReader( inputStreamReader );

        String line = null;

        while ( ( line = bufferedReader.readLine() ) != null ) {
            stringBuilder.append( line );
        }
        inputStream.close();

        return stringBuilder.toString();
    }


}
