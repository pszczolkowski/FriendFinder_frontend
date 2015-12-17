package bart.friendfinderapp.loginActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import bart.friendfinderapp.R;
import bart.friendfinderapp.exceptions.UserCantBeReadException;
import bart.friendfinderapp.friends.UserFriends;
import bart.friendfinderapp.invitation.UserInvitations;
import bart.friendfinderapp.mainActivity.MainActivity;
import bart.friendfinderapp.shared.UserCredentials;

import static android.Manifest.permission.READ_CONTACTS;
import static bart.friendfinderapp.shared.Constants.LOGIN_FILE;
import static bart.friendfinderapp.shared.UserCredentials.createUserCredentials;
import static bart.friendfinderapp.shared.UserCredentials.getUserCredentials;

/**
 * Screen to login or sign up to application
 */
public class LoginActivity extends Activity implements LoaderCallbacks< Cursor > {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    private Boolean have_an_account = false;
    private Boolean remember_me = false;

    /**
     * Keep track of the tryToSignIn task to ensure we can cancel it if requested.
     */
    private UserLoginTask userLoginTask = null;
    private UserRegisterTask userRegisterTask = null;

    // UI references.
    private AutoCompleteTextView loginView;
    private EditText loginPasswordView;
    private EditText addPasswordView;
    private EditText confirmPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Button signInButton;
    private Button registerButton;
    private CheckBox haveAnAccountCheckbox;
    private CheckBox rememberMeChecbox;
    private UserCredentials userCredentials;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            // Activity was brought to front and not created,
            // Thus finishing this will get us to the last viewed activity
            finish();
            return;
        }
        boolean fileReaded = readDataFromFile();
        if ( savedInstanceState != null && !fileReaded ) {
            if ( savedInstanceState.containsKey( "have_an_account" ) ) {
                have_an_account = (Boolean) savedInstanceState.get( "have_an_account" );
            }
            if ( savedInstanceState.containsKey( "remember_me" ) ) {
                remember_me = (Boolean) savedInstanceState.get( "remember_me" );
            }

            if ( remember_me ) {
                if ( savedInstanceState.containsKey( "login" ) && savedInstanceState.containsKey( "token" ) && savedInstanceState.containsKey( "expiration" ) ) {
                    String login = savedInstanceState.getString( "login" );
                    String token = savedInstanceState.getString( "token" );
                    String expiration = savedInstanceState.getString( "expiration" );
                    createUserCredentials( login, token, expiration );
                    userCredentials = getUserCredentials();
                }
            }
        }
        setContentView( R.layout.activity_login );
        // Set up the tryToSignIn form.
        loginView = (AutoCompleteTextView) findViewById( R.id.login );
        populateAutoComplete();

        loginPasswordView = (EditText) findViewById( R.id.password );
        loginPasswordView.setOnEditorActionListener( new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction( TextView textView, int id, KeyEvent keyEvent ) {
                if ( id == R.id.login || id == EditorInfo.IME_NULL ) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        } );
        addPasswordView = (EditText) findViewById( R.id.add_password );
        confirmPasswordView = (EditText) findViewById( R.id.confirm_password );

        signInButton = (Button) findViewById( R.id.sign_in_button );
        signInButton.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick( View view ) {
                attemptLogin();
            }
        } );

        registerButton = (Button) findViewById( R.id.register_button );
        registerButton.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick( View v ) {
                attemptRegister();
            }
        } );

        haveAnAccountCheckbox = (CheckBox) findViewById( R.id.have_account_checkBox );
        haveAnAccountCheckbox.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick( View v ) {
                have_an_account = !have_an_account;
                setElementsVisibility();
            }
        } );
        rememberMeChecbox = (CheckBox) findViewById( R.id.remember_me_checkBox );
        rememberMeChecbox.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick( View v ) {
                remember_me = !remember_me;
                setElementsVisibility();

            }
        } );

        mLoginFormView = findViewById( R.id.login_form );
        mProgressView = findViewById( R.id.login_progress );

        /**
         * Set checked option for checkboxes
         */
        setCheckboxes();
        /**
         * Set elements visibility
         */
        setElementsVisibility();

        if ( remember_me ) {
            if ( userCredentials != null ) {
                loginView.setText( (CharSequence) userCredentials.getLogin() );
                loginPasswordView.setText( "******" );
            }
        }
    }

    private void setCheckboxes() {
        haveAnAccountCheckbox.setChecked( have_an_account );
        rememberMeChecbox.setChecked( remember_me );
    }

    private void setElementsVisibility() {
        if ( have_an_account ) {
            loginPasswordView.setVisibility( View.VISIBLE );
            rememberMeChecbox.setVisibility( View.VISIBLE );
            signInButton.setVisibility( View.VISIBLE );

            addPasswordView.setVisibility( View.GONE );
            confirmPasswordView.setVisibility( View.GONE );
            registerButton.setVisibility( View.GONE );
        } else {
            loginPasswordView.setVisibility( View.GONE );
            rememberMeChecbox.setVisibility( View.GONE );
            signInButton.setVisibility( View.GONE );

            addPasswordView.setVisibility( View.VISIBLE );
            confirmPasswordView.setVisibility( View.VISIBLE );
            registerButton.setVisibility( View.VISIBLE );
        }
    }

    private void moveToMainActivity() {
        saveDataToFile();
        Intent i = new Intent( LoginActivity.this, MainActivity.class );
        startActivity( i );
        UserFriends.requestUpdateOfUserFriends();
        UserInvitations.requestUpdateOfUserInvitations();
        finish();
    }

    @Override
    protected void onSaveInstanceState( Bundle outState ) {
        super.onSaveInstanceState( outState );
        outState.putSerializable( "have_an_account", have_an_account );
        outState.putSerializable( "remember_me", remember_me );

        if ( remember_me ) {
            try {
                UserCredentials userCredentials = getUserCredentials();
                outState.putSerializable( "login", userCredentials.getLogin() );
                outState.putSerializable( "token", userCredentials.getToken() );
                outState.putSerializable( "expiration", userCredentials.getExpiration() );
            } catch ( UserCantBeReadException ex ) {
                ex.printStackTrace();
            }
        }

        saveDataToFile();

    }

    private void saveDataToFile() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put( "have_an_account", have_an_account );
            jsonObject.put( "remember_me", remember_me );

            if ( remember_me ) {
                try {
                    UserCredentials userCredentials = getUserCredentials();
                    jsonObject.put( "login", userCredentials.getLogin() );
                    jsonObject.put( "token", userCredentials.getToken() );
                    jsonObject.put( "expiration", userCredentials.getExpiration() );
                } catch ( UserCantBeReadException ex ) {
                    ex.printStackTrace();
                }
            }
            String data = jsonObject.toString();
            FileOutputStream fileOutputStream = openFileOutput( LOGIN_FILE, Context.MODE_PRIVATE );
            fileOutputStream.write( data.getBytes() );
            fileOutputStream.close();

        } catch ( FileNotFoundException e ) {
            e.printStackTrace();
        } catch ( JSONException e ) {
            e.printStackTrace();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    private boolean readDataFromFile() {
        boolean success = false;
        try {
            InputStream inputStream = openFileInput( LOGIN_FILE );
            InputStreamReader fileInputStream = new InputStreamReader( inputStream );
            BufferedReader bufferedReader = new BufferedReader( fileInputStream );

            String line = null;
            StringBuilder stringBuilder = new StringBuilder();
            while ( ( line = bufferedReader.readLine() ) != null ) {
                stringBuilder.append( line );
            }
            inputStream.close();

            JSONObject jsonObject = new JSONObject( stringBuilder.toString() );

            have_an_account = jsonObject.optBoolean( "have_an_account" );
            remember_me = jsonObject.optBoolean( "remember_me" );

            if ( remember_me ) {
                String login = jsonObject.optString( "login" ).trim();
                String token = jsonObject.optString( "token" ).trim();
                String expiration = jsonObject.optString( "expiration" ).trim();
                if ( !login.isEmpty() && !token.isEmpty() && !expiration.isEmpty() ) {
                    createUserCredentials( login, token, expiration );
                    userCredentials = getUserCredentials();
                }
            }
            success = true;
        } catch ( FileNotFoundException e ) {
            e.printStackTrace();
        } catch ( IOException e ) {
            e.printStackTrace();
        } catch ( JSONException e ) {
            e.printStackTrace();
        } catch ( UserCantBeReadException e ) {
            e.printStackTrace();
        }
        return success;
    }

    private void populateAutoComplete() {
        if ( !mayRequestContacts() ) {
            return;
        }

        getLoaderManager().initLoader( 0, null, this );
    }

    private boolean mayRequestContacts() {
        if ( Build.VERSION.SDK_INT < Build.VERSION_CODES.M ) {
            return true;
        }
        if ( checkSelfPermission( READ_CONTACTS ) == PackageManager.PERMISSION_GRANTED ) {
            return true;
        }
        if ( shouldShowRequestPermissionRationale( READ_CONTACTS ) ) {
            Snackbar.make( loginView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE )
                    .setAction( android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi( Build.VERSION_CODES.M )
                        public void onClick( View v ) {
                            requestPermissions( new String[]{ READ_CONTACTS }, REQUEST_READ_CONTACTS );
                        }
                    } );
        } else {
            requestPermissions( new String[]{ READ_CONTACTS }, REQUEST_READ_CONTACTS );
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult( int requestCode, @NonNull String[] permissions,
                                            @NonNull int[] grantResults ) {
        if ( requestCode == REQUEST_READ_CONTACTS ) {
            if ( grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempt to register new account.
     * If there are form errors (missing fields, password to short, etc.), the
     * errors are presented and no actual attempt is made.
     */
    private void attemptRegister() {
        if ( userRegisterTask != null ) {
            return;
        }

        // Reset errors.
        loginView.setError( null );
        addPasswordView.setError( null );
        confirmPasswordView.setError( null );

        // Store values at the time of the register attempt.
        String login = loginView.getText().toString().trim();
        String password = addPasswordView.getText().toString().trim();
        String confirmedPassword = confirmPasswordView.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if ( !TextUtils.isEmpty( password ) && !isPasswordValid( password ) ) {
            addPasswordView.setError( getString( R.string.error_invalid_password ) );
            focusView = addPasswordView;
            cancel = true;
        }
        //Display message when password and confirmedPassword have different values
        if ( !password.equals( confirmedPassword ) ) {
            confirmPasswordView.setError( getString( R.string.error_passwords_mismatch ) );
            cancel = true;
        }

        // Check for a valid (not empty) login
        if ( TextUtils.isEmpty( login ) ) {
            loginView.setError( getString( R.string.error_field_required ) );
            focusView = loginView;
            cancel = true;
        }

        if ( cancel ) {
            // There was an error; don't attempt to send request and focus the first form field with an error
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to perform request to server.
            showProgress( true );
            userRegisterTask = new UserRegisterTask( login, password );
            userRegisterTask.execute( (Void) null );
        }
    }

    /**
     * Attempt to sign in on existing account.
     * If there are form errors (missing fields, password to short, etc.), the
     * errors are presented and no actual attempt is made.
     */
    private void attemptLogin() {
        if ( userLoginTask != null ) {
            return;
        }

        if ( remember_me && userCredentials != null ) {
            moveToMainActivity();
            return;
        }

        // Reset errors.
        loginView.setError( null );
        loginPasswordView.setError( null );

        // Store values at the time of the tryToSignIn attempt.
        String login = loginView.getText().toString().trim();
        String password = loginPasswordView.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if ( !TextUtils.isEmpty( password ) && !isPasswordValid( password ) ) {
            loginPasswordView.setError( getString( R.string.error_invalid_password ) );
            focusView = loginPasswordView;
            cancel = true;
        }

        // Check for a valid (not empty) login
        if ( TextUtils.isEmpty( login ) ) {
            loginView.setError( getString( R.string.error_field_required ) );
            focusView = loginView;
            cancel = true;
        }

        if ( cancel ) {
            // There was an error; don't attempt to send request and focus the first form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to perform the request to login.
            showProgress( true );
            userLoginTask = new UserLoginTask( login, password );
            userLoginTask.execute( (Void) null );
        }
    }

    private boolean isPasswordValid( String password ) {
        return password.length() >= 6;
    }

    /**
     * Represents an asynchronous tryToSignIn task used to authenticate user.
     */
    public class UserLoginTask extends AsyncTask< Void, Void, Boolean > {

        private final String login;
        private final String password;
        private int responseCode;

        UserLoginTask( String login, String password ) {
            this.login = login;
            this.password = password;
        }

        @Override
        protected Boolean doInBackground( Void... params ) {

            responseCode = new LoginController().tryToSignIn( login, password );

            return true;
        }

        @Override
        protected void onPostExecute( final Boolean success ) {
            userLoginTask = null;
            showProgress( false );

            if ( responseCode == HttpURLConnection.HTTP_OK ) {
                moveToMainActivity();
            } else if ( responseCode == HttpURLConnection.HTTP_BAD_REQUEST ) {
                loginView.setError( getString( R.string.error_invalid_login ) );
                loginPasswordView.setError( getString( R.string.error_incorrect_password ) );
                loginPasswordView.requestFocus();
            } else if ( responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR ) {
                createShortToast( getString( R.string.internal_server_error ) );
            } else {
                createShortToast( "Unknown error - check internet connection" );
            }
        }

        @Override
        protected void onCancelled() {
            userLoginTask = null;
            showProgress( false );
        }
    }

    /**
     * Represents an asynchronous tryToRegister task used to register new user.
     */
    public class UserRegisterTask extends AsyncTask< Void, Void, Boolean > {

        private final String login;
        private final String password;
        private int responseCode;

        UserRegisterTask( String login, String password ) {
            this.login = login;
            this.password = password;
        }

        @Override
        protected Boolean doInBackground( Void... params ) {
            responseCode = new RegisterController().tryToRegister( login, password );
            return true;
        }

        @Override
        protected void onPostExecute( final Boolean success ) {
            userRegisterTask = null;
            showProgress( false );

            //on success registration start thread wih attempt to login on created account
            if ( responseCode == HttpURLConnection.HTTP_OK ) {
                createShortToast( "Registration successful, logging into application" );
                showProgress( true );
                userLoginTask = new UserLoginTask( login, password );
                userLoginTask.execute( (Void) null );
            } else if ( responseCode == HttpURLConnection.HTTP_BAD_REQUEST ) {
                loginView.setError( getString( R.string.error_login_already_in_use ) );
                loginPasswordView.requestFocus();
            } else {
                createShortToast( getString( R.string.internal_server_error ) );
            }
        }

        @Override
        protected void onCancelled() {
            userLoginTask = null;
            showProgress( false );
        }
    }

    private void createShortToast( String message ) {
        Toast.makeText( this, message, Toast.LENGTH_SHORT ).show();
    }

    @Override
    public Loader< Cursor > onCreateLoader( int i, Bundle bundle ) {
        return new CursorLoader( this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath( ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY ), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE },

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC" );
    }

    @Override
    public void onLoadFinished( Loader< Cursor > cursorLoader, Cursor cursor ) {
        List< String > emails = new ArrayList<>();
        cursor.moveToFirst();
        while ( !cursor.isAfterLast() ) {
            emails.add( cursor.getString( ProfileQuery.ADDRESS ) );
            cursor.moveToNext();
        }

        addEmailsToAutoComplete( emails );
    }

    @Override
    public void onLoaderReset( Loader< Cursor > cursorLoader ) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addEmailsToAutoComplete( List< String > loginAddressCollection ) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter< String > adapter =
                new ArrayAdapter<>( LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, loginAddressCollection );

        loginView.setAdapter( adapter );
    }

    /**
     * Shows the progress UI and hides the tryToSignIn form.
     */
    @TargetApi( Build.VERSION_CODES.HONEYCOMB_MR2 )
    private void showProgress( final boolean show ) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2 ) {
            int shortAnimTime = getResources().getInteger( android.R.integer.config_shortAnimTime );

            mLoginFormView.setVisibility( show ? View.GONE : View.VISIBLE );
            mLoginFormView.animate().setDuration( shortAnimTime ).alpha(
                    show ? 0 : 1 ).setListener( new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd( Animator animation ) {
                    mLoginFormView.setVisibility( show ? View.GONE : View.VISIBLE );
                }
            } );

            mProgressView.setVisibility( show ? View.VISIBLE : View.GONE );
            mProgressView.animate().setDuration( shortAnimTime ).alpha(
                    show ? 1 : 0 ).setListener( new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd( Animator animation ) {
                    mProgressView.setVisibility( show ? View.VISIBLE : View.GONE );
                }
            } );
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility( show ? View.VISIBLE : View.GONE );
            mLoginFormView.setVisibility( show ? View.GONE : View.VISIBLE );
        }
    }
}

