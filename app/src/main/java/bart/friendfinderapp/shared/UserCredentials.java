package bart.friendfinderapp.shared;

import bart.friendfinderapp.exceptions.UserCantBeReadException;

/**
 * Created by Godzio on 2015-11-21.
 */
public class UserCredentials {

    private String login;
    private String token;
    private String expiration;

    private static UserCredentials userCredentials = null;

    private UserCredentials( String login, String token, String expiration ) {
        this.login = login;
        this.token = token;
        this.expiration = expiration;
    }

    public static void createUserCredentials( String login, String token, String expiration ) {
        userCredentials = new UserCredentials( login, token, expiration );
    }


    public static UserCredentials getUserCredentials() {
        if ( userCredentials == null ) {
            throw new UserCantBeReadException();
        }
        return userCredentials;
    }

    public String getLogin() {
        return login;
    }

    public String getToken() {
        return token;
    }

    public String getExpiration() {
        return expiration;
    }
}
