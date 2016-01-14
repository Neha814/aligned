package com.aligned.utility;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.Toast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class Funtion {


    private AsyncHttpClient client;
    private JSONObject object;
    private ProgressDialog dialog;

    public static void set_title_to_actionbar(String Title, Context ctx, Toolbar mToolbar, Boolean value) {

        ((AppCompatActivity) ctx).setSupportActionBar(mToolbar);
        ((AppCompatActivity) ctx).getSupportActionBar().setDisplayShowHomeEnabled(value);
        ((AppCompatActivity) ctx).getSupportActionBar().setDisplayUseLogoEnabled(value);
        ((AppCompatActivity) ctx).getSupportActionBar().setDisplayHomeAsUpEnabled(value);

        if (!Title.equals(null) && Title.length() > 0) {
            SpannableStringBuilder builder = new SpannableStringBuilder();

            Spannable WordtoSpan = new SpannableString(Title.substring(0,
                    1));
            WordtoSpan.setSpan(
                    new ForegroundColorSpan(Color.parseColor("#ffffff")), 1, 1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            Spannable WordtoSpan2 = new SpannableString(Title.subSequence(
                    1, Title.length()));
            WordtoSpan2.setSpan(
                    new ForegroundColorSpan(Color.parseColor("#ffffff")), 0,
                    WordtoSpan2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            builder.append(WordtoSpan);
            builder.append(WordtoSpan2);

            ((AppCompatActivity) ctx).getSupportActionBar().setTitle(builder);
        }
    }

    public static void toast(Context ctx, String message) {
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
    }


    public JSONObject getResponse(String url,RequestParams params,Context context)
    {
        Log.e("Urlll",url);
        client = new AsyncHttpClient();
        client.setTimeout(Constants.connection_timeout);
        dialog = new ProgressDialog(context);
        dialog.setMessage("Checking...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);


        client.post(context,url,params,new JsonHttpResponseHandler()
        {
            @Override
            public void onStart() {
                dialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e("onSuccess","onsuccess");
                object=response;
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

            return object;

    }



}
