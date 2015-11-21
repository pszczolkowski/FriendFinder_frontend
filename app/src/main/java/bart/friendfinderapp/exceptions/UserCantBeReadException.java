package bart.friendfinderapp.exceptions;

/**
 * Created by Godzio on 2015-11-21.
 */
public class UserCantBeReadException extends RuntimeException{

    public UserCantBeReadException() {
        super("User credentials weren't saved properly and reading them is impossible");
    }
}
