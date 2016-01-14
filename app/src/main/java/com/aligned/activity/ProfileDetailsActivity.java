package com.aligned.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aligned.R;
import com.aligned.utility.Constants;
import com.aligned.utility.Funtion;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class ProfileDetailsActivity extends AppCompatActivity {

    Toolbar mToolbar;
    TextView name_tv,age_tv,location_tv, active_tv, about_name_tv, about_tv;
    ViewPager pager;
    private PicsAdapter adapter;
    ArrayList<String> picsList = new ArrayList<String>();
    String TAG = "ProfileDetailsActivity";
    String fbid;
    String comma_sep_values;
    String name;
    private AsyncHttpClient client;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);

         mToolbar = (Toolbar) findViewById(R.id.toolbar);


        initialize();
    }

    private void initialize() {

        client = new AsyncHttpClient();
        client.setTimeout(Constants.connection_timeout);
        dialog = new ProgressDialog(getApplicationContext());
        dialog.setMessage("Loading..");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);


        Intent i = getIntent();
         name = i.getStringExtra("name");
        String like = i.getStringExtra("like");
        String no_of_pics = i.getStringExtra("no_of_pics");
         comma_sep_values = i.getStringExtra("comma_sep_values");
         fbid = i.getStringExtra("fbid");

        List<String> items = Arrays.asList(comma_sep_values.split("\\s*,\\s*"));

        picsList.addAll(items);

        Funtion.set_title_to_actionbar(name, this, mToolbar, false);

        name_tv = (TextView) findViewById(R.id.name_tv);
        age_tv = (TextView) findViewById(R.id.age_tv);
        location_tv = (TextView) findViewById(R.id.location_tv);
        active_tv = (TextView) findViewById(R.id.active_tv);
        about_name_tv = (TextView) findViewById(R.id.about_name_tv);
        about_tv = (TextView) findViewById(R.id.about_tv);
        pager = (ViewPager) findViewById(R.id.pager);

        name_tv.setText(name+",");
        about_name_tv.setText(name);

        adapter = new PicsAdapter(picsList);
        pager.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile_details, menu);
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
        } else if (id == R.id.profile_like){
            Constants.LIKE_DISLIKE = "1";
            Log.e(TAG,"like");
            BackGroundTaskForInviteAction();

        } else if (id == R.id.profile_dislike){
            Constants.LIKE_DISLIKE = "2";
            Log.e(TAG,"dislike");
            BackGroundTaskForInviteAction();
        }

        return super.onOptionsItemSelected(item);
    }


    //**************************** Pics Adapter ************************************//
    public class PicsAdapter extends PagerAdapter {
        private static final String TAG = "PicsAdapter";
        private ArrayList<String> list;


        public PicsAdapter(ArrayList<String> list) {
            this.list = list;
        }

        public int getCount() {
            return list.size();
        }

        private View currentView;

        public Object instantiateItem(View collection, final int position) {
            final Holder holder = new Holder();
            View v = collection;
            LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.pics_item, null);
            holder.pic = (ImageView) v.findViewById(R.id.pic);
            holder.linear_layout = (LinearLayout) v.findViewById(R.id.linear_layout);

            Log.e(TAG,"pos= "+position+" url= "+this.list.get(position));

            Picasso.with(getApplicationContext())
                    .load(this.list.get(position))
                    .placeholder(com.andtinder.R.drawable.profilepic).error(com.andtinder.R.drawable.profilepic).fit()
                    .into(holder.pic);

            ((ViewPager) collection).addView(v);
            collection.setTag(holder);

            final int N = this.list.size(); // total number of textviews to add

            final TextView[] myTextViews = new TextView[N]; // create an empty array;

            for (int i = 0; i < N; i++) {
                // create a new textview
                final TextView rowTextView = new TextView(getApplicationContext());

                // set some properties of rowTextView or something
                if(i==position){
                    rowTextView.setBackgroundResource(R.drawable.pink_solid_circle);
                } else {
                    rowTextView.setBackgroundResource(R.drawable.pink_blank_circle);
                }

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, 20);
                params.setMargins(5, 5,5, 5);
                rowTextView.setLayoutParams(params);

                // add the textview to the linearlayout
                holder.linear_layout.addView(rowTextView);

                // save a reference to the textview for later
                myTextViews[i] = rowTextView;
            }

            currentView = v;
            return v;
        }

        public void destroyItem(View collection, int position, Object view) {
            ((ViewPager) collection).removeView((View) view);
        }

        public boolean isViewFromObject(View view, Object object) {
            return view == ((View) object);
        }


        class Holder {
            ImageView pic;
            LinearLayout linear_layout;
        }
    }

    // *************************** background srvice for like or dislike ***************************//

    private void BackGroundTaskForInviteAction() {

        RequestParams params = new RequestParams();

        params.put("ent_user_fbid",Constants.USER_LOGIN_ID);
        params.put("ent_invitee_fbid",fbid);
        params.put("ent_user_action",Constants.LIKE_DISLIKE);

        Log.e("parameters", params.toString());
        Log.e("URL", Constants.inviteaction_url + "?" + params.toString());
        client.post(getApplicationContext(), Constants.inviteaction_url, params, new JsonHttpResponseHandler() {

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
                    Log.e(TAG,"response= "+response);
                    String errNum = response.getString("errNum");
                    //  if(errNum.equals("55")){
                    Intent i = new Intent(getApplicationContext(), ItsAMatch.class);

                    String commaSepPics = comma_sep_values;
                    List<String> items = Arrays.asList(commaSepPics.split("\\s*,\\s*"));

                    i.putExtra("name",name);
                    i.putExtra("user_image",items.get(0));


                    startActivity(i);
                    // }

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
}
