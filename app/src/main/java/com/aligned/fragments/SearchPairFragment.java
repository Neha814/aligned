package com.aligned.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aligned.Model.MatchesData;
import com.aligned.R;
import com.aligned.activity.ItsAMatch;
import com.aligned.utility.Constants;
import com.aligned.utility.NetConnection;
import com.andtinder.model.CardModel;
import com.andtinder.view.CardContainer;
import com.andtinder.view.SimpleCardStackAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by sandeep on 1/7/16.
 */
public class SearchPairFragment extends Fragment implements View.OnClickListener {

    View rootView;
    private CardContainer mCardContainer;
    private AsyncHttpClient client;
    private ProgressDialog dialog;
    public String TAG = "SearchPairFragment";
    Boolean isConnected;
    SharedPreferences sp;
    private MatchesData mFindMatchData;
    ArrayList<HashMap<String , String>> MatchedList = new ArrayList<HashMap<String , String>>();
    ImageView no_img , yes_img ;

    public SearchPairFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search_pair, container, false);

        isConnected = NetConnection.checkInternetConnectionn(getActivity());
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

        Initialise();

        return rootView;
    }

    private void Initialise() {

        client = new AsyncHttpClient();
        client.setTimeout(Constants.connection_timeout);
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading..");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        mCardContainer = (CardContainer) rootView.findViewById(R.id.layoutview);
        no_img = (ImageView) rootView.findViewById(R.id.no_img);
        yes_img = (ImageView) rootView.findViewById(R.id.yes_img);


        no_img.setOnClickListener(this);
        yes_img.setOnClickListener(this);

        BackGroundTaskForFindMatch();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    // **************************** Find Matches *********************************//
    private void BackGroundTaskForFindMatch() {


        RequestParams params = new RequestParams();
        params.put("ent_user_fbid", Constants.USER_LOGIN_ID);

        Log.e("parameters", params.toString());

        Log.e("URL", Constants.findMatch_url + "?" + params.toString());
        client.post(getActivity(), Constants.findMatch_url, params, new JsonHttpResponseHandler() {

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
                    Log.e(TAG, "" + response);

                   /* Gson gson = new Gson();
                    mFindMatchData = gson.fromJson(response,
                            FindMatchData.class);*/

                    JSONArray matches = response.getJSONArray("matches");
                    for (int i = 0; i < matches.length(); i++) {
                        HashMap<String, String> localHashmap = new HashMap<String, String>();
                        localHashmap.put("firstName", matches.getJSONObject(i).getString("firstName"));
                        localHashmap.put("status", matches.getJSONObject(i).getString("status"));
                        localHashmap.put("fbId", matches.getJSONObject(i).getString("fbId"));
                        localHashmap.put("pPic", matches.getJSONObject(i).getString("pPic"));
                        localHashmap.put("mutualLikecount", matches.getJSONObject(i).getString("mutualLikecount"));
                        localHashmap.put("mutualFriendcout", matches.getJSONObject(i).getString("mutualFriendcout"));
                        localHashmap.put("sex", matches.getJSONObject(i).getString("sex"));
                        localHashmap.put("persDesc", matches.getJSONObject(i).getString("persDesc"));
                        localHashmap.put("age", matches.getJSONObject(i).getString("age"));
                        localHashmap.put("lat", matches.getJSONObject(i).getString("lat"));
                        localHashmap.put("long", matches.getJSONObject(i).getString("long"));
                        localHashmap.put("matchPercentage", matches.getJSONObject(i).getString("matchPercentage"));
                        localHashmap.put("lastActive", matches.getJSONObject(i).getString("lastActive"));


                        JSONArray images = matches.getJSONObject(i).getJSONArray("image");
                        String image[] = new String[images.length()];


                        for (int j = 0; j < images.length(); j++) {
                            image[j] = images.get(j).toString();
                        }

                        String asString = android.text.TextUtils.join(",", image);
                        //String asString = Arrays.toString(image);
                        localHashmap.put("image", asString);
                        localHashmap.put("no_of_images", "" + images.length());
                        MatchedList.add(localHashmap);
                    }

                    SetCardAdapter();

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

    private void SetCardAdapter() {

        //Resources r = getResources();
//  adapter.add(new CardModel("Surbhi", "4","3", r.getDrawable(R.drawable.picture3)));

        SimpleCardStackAdapter adapter = new SimpleCardStackAdapter(getActivity());
        String picUrl_0 = "";

        for(int i=0;i<MatchedList.size();i++) {
            String name = MatchedList.get(i).get("firstName");
            String likes = MatchedList.get(i).get("mutualLikecount");
            String no_of_pics = MatchedList.get(i).get("no_of_images");
            String commaSepPics = MatchedList.get(i).get("image");
            String fbId = MatchedList.get(i).get("fbId");
            List<String> items = Arrays.asList(commaSepPics.split("\\s*,\\s*"));


            Log.e("name==>",""+name);
            Log.e("likes==>",""+likes);
            Log.e("no_of_pics==>", "" + no_of_pics);
            Log.e("pics==>", "" + MatchedList.get(i).get("image"));

             picUrl_0 = items.get(0);

            // adapter.add(new CardModel(name, likes, no_of_pics, picUrl_0,commaSepPics));
            final CardModel cardModel = new CardModel(name, likes, no_of_pics, picUrl_0,commaSepPics,fbId);
            adapter.add(cardModel);

            cardModel.setOnCardDismissedListener(new CardModel.OnCardDismissedListener() {
                @Override
                public void onLike() {
                    Log.i("Swipeable Cards", "I like the card");
                    int pos = mCardContainer.getLastVisiblePosition();
                    Constants.LIKE_DISLIKE = "1";
                    BackGroundTaskForInviteAction(pos);
                    Log.i("Swipeable Cards", "I like the card="+pos);

                }

                @Override
                public void onDislike() {
                    int pos = mCardContainer.getLastVisiblePosition();
                    Constants.LIKE_DISLIKE = "2";
                    BackGroundTaskForInviteAction(pos);
                    Log.i("Swipeable Cards", "I dislike the card="+pos);
                }
            });

        }

        mCardContainer.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {

       // isLikde = "1", isDisliked = "2";

        if(view==no_img) {
           int pos = mCardContainer.getLastVisiblePosition();
            Constants.LIKE_DISLIKE = "2";
            BackGroundTaskForInviteAction(pos);


        } else if(view==yes_img) {
            int pos = mCardContainer.getLastVisiblePosition();
            Constants.LIKE_DISLIKE = "1";
            //BackGroundTaskForInviteAction(pos);
            Intent i = new Intent(getActivity(), ItsAMatch.class);

            String commaSepPics = MatchedList.get(pos).get("image");
            List<String> items = Arrays.asList(commaSepPics.split("\\s*,\\s*"));

            i.putExtra("name", MatchedList.get(pos).get("name"));
            i.putExtra("user_image",items.get(0));


            startActivity(i);

        }
    }

    private void BackGroundTaskForInviteAction(final int pos) {
        int position = pos;
        RequestParams params = new RequestParams();

        params.put("ent_user_fbid",Constants.USER_LOGIN_ID);
        params.put("ent_invitee_fbid",MatchedList.get(position).get("fbId"));
        params.put("ent_user_action",Constants.LIKE_DISLIKE);

        Log.e("parameters", params.toString());
        Log.e("URL", Constants.inviteaction_url + "?" + params.toString());
        client.post(getActivity(), Constants.inviteaction_url, params, new JsonHttpResponseHandler() {

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
                        Intent i = new Intent(getActivity(), ItsAMatch.class);

                        String commaSepPics = MatchedList.get(pos).get("image");
                        List<String> items = Arrays.asList(commaSepPics.split("\\s*,\\s*"));

                        i.putExtra("name", MatchedList.get(pos).get("name"));
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
