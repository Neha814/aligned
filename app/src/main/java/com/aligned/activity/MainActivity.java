package com.aligned.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.aligned.R;
import com.aligned.fragments.MessageInboxFragment;
import com.aligned.fragments.QuestionsFragment;
import com.aligned.fragments.SearchPairFragment;
import com.aligned.utility.Constants;
import com.loopj.android.http.AsyncHttpClient;

import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {


    private static final String TAG = "MainActivity";
    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    private AsyncHttpClient client;
    private JSONObject object;
    private ProgressDialog dialog;
    private SharedPreferences sp;
    Boolean isAllQuesAnsGiven = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor e = sp.edit();
        e.putBoolean("inHome", true);
        e.commit();

        Constants.USER_NAME = sp.getString("name","");
        Constants.USER_EMAIL = sp.getString("email","");
        Constants.USER_PIC_URL = sp.getString("pic_url","");
        Constants.USER_LOGIN_ID = sp.getString("id","");
        isAllQuesAnsGiven = sp.getBoolean("isAllQuesAnsGiven",false);

        Log.e(TAG,"name==>"+Constants.USER_NAME+" email==>"+Constants.USER_EMAIL+" pic==>"+Constants.USER_PIC_URL);


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);


       // getResponse();

        iniTView();

    }

    private void iniTView() {
        Fragment fragment = null;
        if(isAllQuesAnsGiven){
            fragment = new SearchPairFragment();
        } else {
            fragment = new QuestionsFragment();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, fragment);
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentTransaction.commit();
        getSupportActionBar().setTitle("Home");
    }


    /*private void getResponse()
    {
        client = new AsyncHttpClient();
        client.setTimeout(Constants.connection_timeout);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Checking...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        RequestParams params=new RequestParams();
        params.add(Constants.KEY_FACEBOOK_ID, "12");

        client.post(this,Constants.user_exist,params,new JsonHttpResponseHandler()
        {
            @Override
            public void onStart() {
                dialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e("onSuccess","onsuccess");
                Log.e("response",response.toString());
            }


            @Override
            public void onFinish() {
                dialog.dismiss();
                Log.e("finish","onFinishhhhhh");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("failure", "onfailureeeeee");
            }
        });
    }*/



    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }
    private void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                if(isAllQuesAnsGiven){
                    fragment = new SearchPairFragment();
                } else {
                    fragment = new QuestionsFragment();
                }
                title = getString(R.string.title_home);
                break;

            case 1:
                fragment = new MessageInboxFragment();
                title = "Messages";
                break;

            case 5:
                sp.edit().clear();
                Intent i = new Intent(MainActivity.this , LoginActivity.class);
                startActivity(i);
                finish();
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
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
        else if(id== R.id.action_chat){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void ShowSearchPairInfo(){

    }
}
