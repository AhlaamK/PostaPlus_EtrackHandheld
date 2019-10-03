package com.postaplus.etrack;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import util.ActivityNotification;
import webservice.FuncClasses.Events;
import webservice.WebService;

import static android.Manifest.permission.READ_CONTACTS;
import static com.postaplus.etrack.ScreenActivity.loginflag;
/*
import static com.postaplus.etrack.R.id.password;
import static com.postaplus.etrack.R.id.usernme;
*/

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    public static String loginStatus;
    public String username;
    static boolean errored = false;
    Events[] eventResponse;
    SQLiteDatabase sqldb = null;
    DatabaseHandler db;
    String pasword,usrname;
    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    ActivityNotification actNoty = new ActivityNotification();
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String PREF_loginstatus = "Loginstatus";
    private UserLoginTask mAuthTask = null;
    public static final String PREFS_NAME = "preferences";
    // UI references.
    private AutoCompleteTextView Username;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    String uusername, password;

    SharedPreferences sharedpreferences;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
   public static String loginval;

    public static Boolean saveLogin;

    String AppVersion = "";
    TextView versionDisp;
    TextView useridtxt;
    public static  String globaltoken="";
    String LoginResp;
    CheckBox cbremeberme;
    String rememberme,passworde,usrnamee;
    private static final String PREF_UNAME = "Username";
    private static final String PREF_PASSWORD = "Password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        Username = (AutoCompleteTextView) findViewById(R.id.usernme);
        mPasswordView = (EditText) findViewById(R.id.passwordet);
        cbremeberme=(CheckBox)findViewById(R.id.cbremeberme);
        //populateAutoComplete();
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();


        loginval=loginPreferences.getString(PREF_loginstatus,loginStatus);

        saveLogin = loginPreferences.getBoolean("saveLogin", false);
        versionDisp=(TextView)findViewById(R.id.versntxt);
        System.out.println("loginval in relogin is" + loginval+"savelogin val is:"+saveLogin);
        if(loginval != null){


            if (!loginval.contentEquals("FALSE")) {

                System.out.println("you are already in logined");

                Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(myIntent);
                loginflag = true;
            } else {

                saveLogin = loginPreferences.getBoolean("saveLogin", false);
                System.out.println("loginval in oncreate is" + loginval+"savelogin val is:"+saveLogin);

                if (saveLogin) {
                    Username.setText(loginPreferences.getString(PREF_UNAME, usrnamee));
                    mPasswordView.setText(loginPreferences.getString(PREF_PASSWORD, passworde));
                    loginval = loginPreferences.getString(PREF_loginstatus, loginStatus);
                    // loadPreferences();
                    cbremeberme.setChecked(true);
                    loginflag=false;
                }
            }
        }



      /*  if(cbremeberme.isChecked()){
            rememberme="Y";
            Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(myIntent);
        }else{
            rememberme="N";
        }
*/
        String mVersion = "";
        try {
            mVersion = this.getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            mVersion = "";
        }

        AppVersion = mVersion;
        System.out.println("versionDisp before home: " + versionDisp);
        versionDisp.setText("Version "+AppVersion);

        System.out.println("log val in login is:"+loginval);
        //Ak comnt
       /* if(loginval!=null) {
            if (!loginval.matches("FALSE")) {
                Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(myIntent);
            }

        }*/

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    // attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button buttonlogin = (Button) findViewById(R.id.btnlogin);
        useridtxt=(TextView)findViewById(R.id.usrntxtv);
        buttonlogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
               /* if (cbremeberme.isChecked()) {
                    rememberme="Y";
                    loginPrefsEditor.putBoolean("saveLogin", true);
                    loginPrefsEditor.putString("username", username);
                    loginPrefsEditor.putString("password", password);
                    loginPrefsEditor.commit();
                } else {
                    rememberme="N";
                    loginPrefsEditor.putBoolean("saveLogin", false);
                    loginPrefsEditor.clear();
                    loginPrefsEditor.commit();

                }*/

                loginval=loginPreferences.getString(PREF_loginstatus,loginStatus);
                if (cbremeberme.isChecked()) {
                    //loginPrefsEditor.putBoolean("saveLogin", true);
                    rememberme="Y";
                    loginflag = true;
                    System.out.println("onPause save password: " + usrnamee + "loginstts is" + passworde);
                    loginPrefsEditor.putBoolean("saveLogin", true);
                    loginPrefsEditor.putString(PREF_UNAME, usrnamee);
                    loginPrefsEditor.putString(PREF_PASSWORD, passworde);
                    loginPrefsEditor.putString(PREF_loginstatus, loginStatus);
                    loginPrefsEditor.commit();



                }
                else {
                    System.out.println("loginval in else is" + loginval);
                    // loginPrefsEditor.clear();
                    loginPrefsEditor.putBoolean("saveLogin", false);
                    loginPrefsEditor.commit();
                    if (loginval != null) {
                        if (loginval.contentEquals("FALSE")) {
                            loginPrefsEditor.clear();
                            loginPrefsEditor.commit();
                            loginflag = false;
                            rememberme="N";
                        }
                    }

                }
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);


    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(Username, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //  populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {



        // Reset errors.
        Username.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
         passworde = mPasswordView.getText().toString();
         usrnamee = Username.getText().toString();
        //String passworde = mPasswordView.getText().toString();
       // String usrnamee
        System.out.println("Username first is"+usrnamee+"password is"+passworde);
        //  loginStatus=WebService.invokeLoginWS(passworde,usrnamee);
        System.out.println("LOginstatus is"+loginStatus);
        System.out.println("webservc is"+loginStatus);
        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(passworde)) {

            mPasswordView.setError("Please enter password");
            focusView = mPasswordView;
            cancel = true;
        }
        else if (!isPasswordValid(passworde)){

            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid username.
        if (TextUtils.isEmpty(usrnamee)) {
            //mEmailView.setError(getString(R.string.error_field_required));
            Username.setError("Please enter  Username");
            focusView = Username;
            cancel = true;
        } else if (!isUsernameValid(usrnamee)) {
            Username.setError("This username or password is invalid");
            focusView = Username;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            // System.out.println("Username is"+username+"password is"+password);

            // loginStatus= WebService.invokeLoginWS(passworde,usrnamee);
            mAuthTask = new UserLoginTask(passworde,usrnamee);

            // System.out.println("Username  is"+usrnamee+"password is"+passworde);
            mAuthTask.execute();
        }
    }

    private boolean isUsernameValid(String usrnamee) {
        //TODO: Replace this with your own logic
        return usrnamee.length()>1;
       /*System.out.println("logsts:"+loginStatus);
        if(loginStatus==null){return false;}
        if(loginStatus.equals("TRUE")||loginStatus.equals("GSO")){return  true;}
        else
            return false;*/
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length()>4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> users = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            users.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addusernameToAutoComplete(users);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addusernameToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        Username.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String usrnamee;
        private final String passworde;

        UserLoginTask(String password , String  usname) {
            usrnamee = usname;
            passworde = password;
            System.out.println("Username back is"+usrnamee+"password is"+passworde);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            System.out.println("Username is"+usrnamee+"password is"+passworde);
            loginStatus= WebService.invokeLoginWS(passworde,rememberme,usrnamee);
            // LoginResp= WebService.invokeLoginWS(passworde,usrnamee);
            System.out.println("loginStatus after web called globsl is"+loginStatus);
            //loginStatus=globaltoken;
            System.out.println("loginStatus globsl is"+loginStatus+"globaltoken=loginStatus"+globaltoken);
            loginPrefsEditor.putString(PREF_loginstatus, loginStatus);
            loginPrefsEditor.commit();
            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }
       /*
          if(loginStatus.equals("TRUE")||loginStatus.equals("GSO"))
            {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("username",usrnamee);
                intent.putExtra("password",passworde);
                actNoty.setUserName(LoginActivity.this, usrnamee);
                actNoty.setPassword(LoginActivity.this,passworde);
                startActivity(intent);
            }
            else{
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
*/



            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);
            //Error status is false
            if (loginStatus!=null) {

                System.out.println("LoginResp post globsl is"+loginStatus);
                //Based on Boolean value returned from WebService
             /*   if (loginStatus.equals("TRUE")||loginStatus.equals("GSO")) {
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    *//*intent.putExtra("username",usrnamee);
                    intent.putExtra("password",passworde);*//*
                    actNoty.setUserName(LoginActivity.this, usrnamee);
                    actNoty.setPassword(LoginActivity.this, passworde);
                    startActivity(intent);
                } */if (loginStatus.equals("FALSE")||loginStatus.equals("Invalid Credentials")){
                    Toast toast = Toast.makeText(getApplicationContext(), "Please Enter Valid Username and Password.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                   // mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.setError("Please Enter Valid Username and Password.");
                    mPasswordView.requestFocus();
                    cbremeberme.setChecked(false);
                    return;
                }else {
                    System.out.println("loginStatus on post globsl val is"+loginStatus);
                    globaltoken=loginStatus;
                    System.out.println(" globsl val is"+loginStatus);
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    /*intent.putExtra("username",usrnamee);
                    intent.putExtra("password",passworde);*/
                    System.out.println("loginStatus globsl is"+globaltoken);
                    actNoty.setUserName(LoginActivity.this, usrnamee);
                    actNoty.setPassword(LoginActivity.this, passworde);
                    actNoty.setToken(LoginActivity.this, globaltoken);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("loginStatus", globaltoken);
                    System.out.println("logstts:"+loginStatus);
                    editor.commit();
                    startActivity(intent);
                }
            }else{
                Toast toast = Toast.makeText(getApplicationContext(), "Please Enter Valid Username and Password.", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
           /* if (success) {
                Intent int1 = new Intent(LoginActivity.this,MainActivity.class);
                int1.putExtra("username",usrnamee);
                int1.putExtra("password",passworde);
                startActivity(new Intent(int1));
                //finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }*/
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }



    public void onBackPressed() {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        //   finish();
        //   System.exit(0);


    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

       // View v = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

      /*  if (v instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];

            Log.d("Activity", "Touch event "+event.getRawX()+","+event.getRawY()+" "+x+","+y+" rect "+w.getLeft()+","+w.getTop()+","+w.getRight()+","+w.getBottom()+" coords "+scrcoords[0]+","+scrcoords[1]);
            if (event.getAction() == MotionEvent.ACTION_UP && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom()) ) {

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }*/

        if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
            InputMethodManager imm = (InputMethodManager)getSystemService(getApplicationContext().
                    INPUT_METHOD_SERVICE);
            if(imm != null){
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }

            Log.e("TouchTest", "Touch down");
        } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
            Log.e("TouchTest", "Touch up");
        }
        return ret;
    }
}

