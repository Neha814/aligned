package com.aligned.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aligned.R;
import com.aligned.utility.CircleTransform;
import com.squareup.picasso.Picasso;

/**
 * Created by sandeep on 1/12/16.
 */
public class ItsAMatch extends Activity implements View.OnClickListener{

    Button tell_frnd_bt, keep_playing_bt, send_message_bt;
    TextView name ;
    ImageView matched_img, my_img;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.its_a_match);

        initialise();
    }

    private void initialise() {

        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        Intent i = getIntent();
        String userName = i.getStringExtra("name");
        String user_image = i.getStringExtra("user_image");

        tell_frnd_bt = (Button) findViewById(R.id.tell_frnd_bt);
        keep_playing_bt = (Button) findViewById(R.id.keep_playing_bt);
        send_message_bt = (Button) findViewById(R.id.send_message_bt);
        name = (TextView) findViewById(R.id.name);
        matched_img = (ImageView) findViewById(R.id.matched_img);
        my_img = (ImageView) findViewById(R.id.my_img);


        keep_playing_bt.setOnClickListener(this);
        tell_frnd_bt.setOnClickListener(this);
        send_message_bt.setOnClickListener(this);

        name.setText("you and " + userName + " have aligned with each other !");

        Picasso.with(getApplicationContext())
                .load(sp.getString("pic_url",""))
                .placeholder(R.drawable.ic_logo).error(R.drawable.ic_logo).transform(new CircleTransform(true)).fit()
                .into(my_img);

        Picasso.with(getApplicationContext())
                .load(user_image)
                .placeholder(R.drawable.ic_logo).error(R.drawable.ic_logo).transform(new CircleTransform(true)).fit()
                .into(matched_img);

    }

    @Override
    public void onClick(View view) {
        if(view==keep_playing_bt){
            finish();
        } else if(view==tell_frnd_bt){
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "#Aligned..Checkout this amazing app!");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }
    }
}
