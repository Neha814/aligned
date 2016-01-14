package com.aligned.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aligned.R;
import com.aligned.utility.Constants;
import com.aligned.utility.GPSTracker;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private String TAG = "LoginActivity", regid;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String PROPERTY_REG_ID = "registration_id";

  //  private String SENDER_ID = "795168895654";
    private SharedPreferences.Editor editor;
    private static final int RC_SIGN_IN = 0;

    boolean mSignInClicked;
    private boolean mIntentInProgress;
    private static final int G_SIGN_IN = 100;

    private GoogleApiClient mGoogleApiClient;
    private ConnectionResult mConnectionResult;
    private SharedPreferences sharedPreferences, sp;
    private GoogleCloudMessaging gcm;
    private CallbackManager callbackManager;
    private Button facebook_btn,google_btn;
    private ImageView logo_img;
    private LinearLayout linear_layout;
    private TextView permission_policy;
    private ProgressDialog dialog;
    private static final int PROFILE_PIC_SIZE = 400;
    private boolean inHome = false ;
    RelativeLayout relative_layout;
    Double lat;
    Double lng ;

    private AsyncHttpClient client;

    GPSTracker gps ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        inHome = sp.getBoolean("inHome", false);
        gps = new GPSTracker(getApplicationContext());
        lat = gps.getLatitude();
        lng = gps.getLongitude();

        setContentView(R.layout.activity_login);
        initialize();

        if (!((LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE))
                .isProviderEnabled("gps")) {
            showGPSDisabledAlertToUser();

        }

        logo_img.setVisibility(View.GONE);
        linear_layout.setVisibility(View.GONE);


        if(inHome){
          Intent i = new Intent(LoginActivity.this , MainActivity.class);
            startActivity(i);
            finish();
        } else {

            Animation slide_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in);
            Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_from_up);

            logo_img.setVisibility(View.VISIBLE);
            logo_img.startAnimation(slide_up);

            linear_layout.setVisibility(View.VISIBLE);
            linear_layout.startAnimation(slide_in);

            FacebookSdk.sdkInitialize(getApplicationContext());
            callbackManager = CallbackManager.Factory.create();
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).addApi(Plus.API)
                    .addScope(Plus.SCOPE_PLUS_LOGIN).build();

            facebookInstance();

            registerGCM();
            printKeyHash();
        }


    }
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }



    private void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            Log.e("g+ isConnecting","g+ isConnecting");
            mSignInClicked = true;
            resolveSignInError();
        }
    }
    private void facebookInstance()
    {
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        RequestData();
                    }

                    @Override
                    public void onCancel() {
                        Log.e(TAG, "cancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.e("On error", exception.toString());
                    }
                });
    }


    private void initialize()

    {
        client = new AsyncHttpClient();
        client.setTimeout(Constants.connection_timeout);
        dialog = new ProgressDialog(getApplicationContext());
        dialog.setMessage("Loading..");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);


        sharedPreferences = getSharedPreferences(Constants.PreferenceData, Context.MODE_PRIVATE);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading..");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        setmGoogleApiClient();



        facebook_btn=(Button)findViewById(R.id.facebook_btn);
        google_btn=(Button)findViewById(R.id.google_btn);
        permission_policy=(TextView)findViewById(R.id.permission_policy);
        logo_img = (ImageView) findViewById(R.id.logo_img);
        linear_layout = (LinearLayout) findViewById(R.id.linear_layout);
        relative_layout = (RelativeLayout) findViewById(R.id.relative_layout);


        facebook_btn.setOnClickListener(this);
        google_btn.setOnClickListener(this);
        permission_policy.setOnClickListener(this);

    }


    private void setmGoogleApiClient()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();
    }

    public void RequestData() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                JSONObject json = response.getJSONObject();
                try {
                    if (json != null) {
                        Log.e("Facebook Login Data", json.toString());
                        String email = "";
                        String name = "";
                        String profile = "";
                        String id = "";
                        String FB_PIC_URL = "";
                        if (json.has("name"))
                            name = json.getString("name");
                        if (json.has("email"))
                            email = json.getString("email");
                        if (json.has("link"))
                            profile = json.getString("link");
                        if (json.has("id"))
                            id = json.getString("id");


                        FB_PIC_URL = "https://graph.facebook.com/" + id + "/picture?type=large";

                        Constants.FB_NAME = name;
                        Constants.FB_EMAIL = email;
                        Constants.FB_PIC_URL = FB_PIC_URL;
                        Constants.FB_ID = id;

                        SharedPreferences.Editor e = sp.edit();
                        e.putString("name", Constants.FB_NAME);
                        e.putString("email", Constants.FB_EMAIL);
                        e.putString("pic_url", Constants.FB_PIC_URL);
                        e.putString("id", Constants.FB_ID);
                        e.commit();


                        String bday = "";

                        CallLoginAPI(Constants.FB_ID,Constants.FB_NAME,Constants.FB_PIC_URL,bday,"");
                        LoginManager.getInstance().logOut();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,email,picture,age,sex,dob");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void printKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.i("KeyHash:",
                        Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.i(TAG, "Exception(NameNotFoundException) : " + e);

        } catch (NoSuchAlgorithmException e) {
            Log.i(TAG, "Exception(NoSuchAlgorithmException) : " + e);
        }
    }

    private void registerGCM() {
        if (checkPlayServices()) {
            regid = getRegistrationId(this);
            if (regid.isEmpty()) {
                new GCMRegistration().execute();
            } else {
                Log.e(TAG, "reg id saved : " + regid);
            }
        } else {
            return;
        }
    }


    public boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {

                finish();
            }
            return false;
        }
        return true;
    }
    private class GCMRegistration extends AsyncTask<String, Void, Void> {
        private String[] params;

        @Override
        protected Void doInBackground(String... params) {
            String msg = "";
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(LoginActivity.this);
                    // logDebug("GCMRegistration  gcm "+gcm);
                }
                regid = gcm.register(com.aligned.utility.Constants.SENDER_ID);
                String regidfoundseccessfully = "getGoogleRegistrationId";
                msg = "GCMRegistration doInBackground Device registered, registration ID="
                        + regid;
                storeRegistrationId(LoginActivity.this, regid);
                Log.e("regid", regid + "----");
            } catch (IOException ex) {
                msg = "Error :" + ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    private void storeRegistrationId(Context context, String regId) {
        int appVersion = getAppVersion(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(com.aligned.utility.Constants.REG_ID, regId);
        editor.putInt(com.aligned.utility.Constants.APP_VERSION, appVersion);
        editor.commit();
        Log.e("regid", regid + "----");
    }


    public static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (Exception e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private String getRegistrationId(Context context) {
        String registrationId = sharedPreferences.getString(com.aligned.utility.Constants.REG_ID, "");
        if (registrationId.isEmpty()) {
            return "";
        }
        int registeredVersion = sharedPreferences.getInt(com.aligned.utility.Constants.APP_VERSION,
                Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            return "";
        }
        return registrationId;
    }

    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                Log.e(TAG,"hasResolution");
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.facebook_btn:
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends", "public_profile email"));
                break;

            case R.id.google_btn:
                signInWithGplus();
                break;

            case R.id.permission_policy:
                break;

            default:
                break;
        }


    }


    private void getProfileInformation() {

        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String personId = currentPerson.getId();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                String personGooglePlusProfile = currentPerson.getUrl();
                String bday = currentPerson.getBirthday();
                String sex ="" ;
                if(currentPerson.getGender()==0){
                    sex = "Male";
                } else if(currentPerson.getGender()==1){
                    sex = "Female";
                }

               /* if(bday.equals("null")){
                    bday = "";
                }*/
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

                Log.e("info==>>", "Name: " + personName + ", plusProfile: "
                        + personGooglePlusProfile + ", email: " + email
                        + ", Image: " + personPhotoUrl+", bday: "+bday+", sex: "+sex);


                // by default the profile url gives 50x50 px image only
                // we can replace the value with whatever dimension we want by
                // replacing sz=X
                personPhotoUrl = personPhotoUrl.substring(0,
                        personPhotoUrl.length() - 2)
                        + PROFILE_PIC_SIZE;

                //****** signOutFromGPlus ******** //

                if (mGoogleApiClient.isConnected()) {
                    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                    mGoogleApiClient.disconnect();
                    mGoogleApiClient.connect();

                    Constants.GOOGLE_NAME = personName;
                    Constants.GOOGLE_EMAIL = email;
                    Constants.GOOGLE_PIC_URL = personPhotoUrl;
                    Constants.GOOGLE_ID = personId;

                    SharedPreferences.Editor e = sp.edit();
                    e.putString("name",Constants.GOOGLE_NAME);
                    e.putString("email",Constants.GOOGLE_EMAIL);
                    e.putString("pic_url",Constants.GOOGLE_PIC_URL);
                    e.putString("id", Constants.GOOGLE_ID);
                    e.commit();

                    Log.e(TAG, "name==>" + Constants.GOOGLE_NAME + " email==>" + Constants.GOOGLE_EMAIL + " pic==>" + Constants.GOOGLE_PIC_URL);

                    CallLoginAPI(Constants.GOOGLE_ID, Constants.GOOGLE_NAME, Constants.GOOGLE_PIC_URL,bday,sex);
                }

            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onConnected(Bundle bundle) {
        mSignInClicked = false;

        getProfileInformation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), LoginActivity.this,
                    0).show();
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = result;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Log.e(TAG,"RC_SIGN_IN");
            if (resultCode != RESULT_OK) {
                Log.e(TAG,"RESULT_OK");
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                Log.e(TAG,"connect");
                mGoogleApiClient.connect();

            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


// ************************ Call Login APi *********************************//

    private void CallLoginAPI(String id , String name , String pic_url,String bday,String sex) {

                        /*"ent_fbid";
                        "ent_first_name";
                        "ent_last_name";
                        "ent_profile_pic";
                        "ent_sex";
                        "ent_push_token";
                        "ent_curr_lat";
                        "ent_curr_long";
                        "ent_dob";
                        "ent_device_type";
                        "ent_image_id";*/


        RequestParams params = new RequestParams();
        params.put("ent_fbid",id);
        params.put("ent_first_name",name);
        params.put("ent_profile_pic",pic_url);
        params.put("ent_last_name","");
        params.put("ent_sex",sex);
        params.put("ent_push_token", regid);
        params.put("ent_curr_lat", String.valueOf(lat));
        params.put("ent_curr_long",String.valueOf(lng));
        params.put("ent_dob",bday);
        params.put("ent_device_type","2");


        Log.e("parameters", params.toString());
        Log.e("URL", Constants.login_url + "?" + params.toString());
        client.post(getApplicationContext(), Constants.login_url, params, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                dialog.show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {

                    Log.e(TAG, "response ::-->" + response);
                    String pic = response.getString("profilePic");
                    String age = response.getString("age");
                    String errMsg = response.getString("errMsg");

                    if(errMsg.equalsIgnoreCase("Login completed successfully")){
                        Snackbar snackbar = Snackbar
                                .make(relative_layout, "User is connected!", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, responseString + "/" + statusCode);
                if (headers != null && headers.length > 0) {
                    for (int i = 0; i < headers.length; i++)
                        Log.e("here", headers[i].getName() + "//" + headers[i].getValue());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, "/" + statusCode);
                if (headers != null && headers.length > 0) {
                    for (int i = 0; i < headers.length; i++)
                        Log.e("here", headers[i].getName() + "//" + headers[i].getValue());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.e(TAG, "/" + statusCode);
                if (headers != null && headers.length > 0) {
                    for (int i = 0; i < headers.length; i++)
                        Log.e("here", headers[i].getName() + "//" + headers[i].getValue());
                }
            }
        });
    }

    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(LoginActivity.this);
        localBuilder
                .setMessage(
                        "GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface paramAnonymousDialogInterface,
                                    int paramAnonymousInt) {

                                Intent localIntent2 = new Intent(
                                        "android.settings.LOCATION_SOURCE_SETTINGS");
                                startActivityForResult(localIntent2, 1);

                            }
                        });
        localBuilder.create().show();
    }

}
