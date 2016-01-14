package com.aligned.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aligned.Model.AnswerModel;
import com.aligned.Model.QuestionModel;
import com.aligned.R;
import com.aligned.utility.Constants;
import com.aligned.utility.NetConnection;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by sandeep on 1/6/16.
 */
public class QuestionsFragment extends Fragment implements ViewPager.OnPageChangeListener, View.OnClickListener {
    View rootView;
    TextView no_tv, yes_tv;
    ViewPager pagerQuestions;
    private ArrayList<QuestionModel> QuestionList;
    private ArrayList<AnswerModel> AnswerList = new ArrayList<AnswerModel>();
    Boolean isConnected;
    private AsyncHttpClient client;
    private ProgressDialog dialog;
    public String TAG = "QuestionsFragment";
    private QuestionAdapter adapter;
    SharedPreferences sp;

    public QuestionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_questions, container, false);
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


        no_tv = (TextView) rootView.findViewById(R.id.no_tv);
        yes_tv = (TextView) rootView.findViewById(R.id.yes_tv);
        pagerQuestions = (ViewPager) rootView.findViewById(R.id.pagerQuestions);

        no_tv.setOnClickListener(this);
        yes_tv.setOnClickListener(this);

        if(isConnected){
            getQuestionData();
        }

        // ************ Disable pager on swipping **************************//
        pagerQuestions.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return true;
            }
        });

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    // ******************************** get Questions from backend *****************************//




    private void getQuestionData() {
        RequestParams params = new RequestParams();
        params.put("ent_user_fbid",Constants.USER_LOGIN_ID);
        Log.e("parameters", params.toString());
        Log.e("URL", Constants.getQuestion_url + "?" + params.toString());
        client.post(getActivity(), Constants.getQuestion_url, params, new JsonHttpResponseHandler() {

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
                    QuestionList = parseQuestionData(response);
                    Log.e(TAG, "QuestionSize ::-->" + QuestionList.size());
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            adapter = new QuestionAdapter(QuestionList);
                            pagerQuestions.setAdapter(adapter);
                            pagerQuestions.setOnPageChangeListener((ViewPager.OnPageChangeListener) getActivity());
                        }
                    });
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

    //************************** parse question data ******************************************//

    public  ArrayList<QuestionModel> parseQuestionData(JSONObject json) {
        Log.e(TAG, "QuestionData:" + json);
        ArrayList<QuestionModel> list = new ArrayList<QuestionModel>();
        try {
            JSONObject object = json;
            if (object.getInt("errFlag") == 0) {
                JSONArray questionArray = object
                        .getJSONArray("detail_que");
                for (int i = 0; i < questionArray.length(); i++) {
                    JSONObject jsonObject = questionArray.getJSONObject(i);
                    QuestionModel model = new QuestionModel();
                    model.setQuestionId(jsonObject.getInt("q_id"));
                    model.setQuestion(jsonObject.getString("question"));
                   /* ArrayList<AnswerModel> answerModelList = new ArrayList<AnswerModel>();
                    JSONArray answerArray = jsonObject
                            .getJSONArray("options");
                    for (int j = 0; j < answerArray.length(); j++) {
                        JSONObject answerObject = answerArray.getJSONObject(j);
                        AnswerModel answerModel = new AnswerModel();
                        answerModel.setQuestionId(answerObject
                                .getInt("q_id"));
                        answerModel.setAnswerId(answerObject
                                .getInt("ans_id"));
                        answerModel.setAnswer(answerObject
                                .getString("option"));
                        answerModel.setFlag(answerObject.getInt("flag"));
                        answerModelList.add(answerModel);
                    }
                    model.setAnswer(answerModelList);*/
                    list.add(model);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View view) {
        if(view==no_tv){
            if(pagerQuestions.getCurrentItem()+1==QuestionList.size()){
                AnswerModel model = new AnswerModel();
                model.setAnswer("No");
                AnswerList.add(model);
                Log.e(TAG, "model size==>" + AnswerList.size() + " Ans=" + AnswerList.get(AnswerList.size() - 1).getAnswer()
                        + " pos==>" + pagerQuestions.getCurrentItem());
                CallSendAnsAPI();
            } else {
                AnswerModel model = new AnswerModel();
                model.setAnswer("No");
                AnswerList.add(model);
                Log.e(TAG, "model size==>" + AnswerList.size() + " Ans=" + AnswerList.get(AnswerList.size() - 1).getAnswer()
                        +" pos==>"+pagerQuestions.getCurrentItem());

                pagerQuestions.setCurrentItem(getItem(+1), true);
            }

        } else if(view==yes_tv){



            if(pagerQuestions.getCurrentItem()+1==QuestionList.size()){
                AnswerModel model = new AnswerModel();
                model.setAnswer("No");
                AnswerList.add(model);
                Log.e(TAG, "model size==>" + AnswerList.size() + " Ans=" + AnswerList.get(AnswerList.size() - 1).getAnswer()
                        + " pos==>" + pagerQuestions.getCurrentItem());
                CallSendAnsAPI();
            } else {
                AnswerModel model = new AnswerModel();
                model.setAnswer("Yes");
                AnswerList.add(model);
                Log.e(TAG, "model size==>" + AnswerList.size() + " Ans=" + AnswerList.get(AnswerList.size() - 1).getAnswer()
                        +" pos==>"+pagerQuestions.getCurrentItem());

                pagerQuestions.setCurrentItem(getItem(+1), true);
            }
        }
    }

    private void CallSendAnsAPI() {
        Log.e("Ans size==>",""+AnswerList.size());
        for(int i =0;i<AnswerList.size();i++) {
            AnswerModel ansModel = AnswerList.get(i);
            Log.e("Ans==>",""+ansModel.getAnswer());
        }

        submitQuestion();

        SharedPreferences.Editor e = sp.edit();
        e.putBoolean("isAllQuesAnsGiven",true);
        e.commit();

        ChangeFragment();
    }



    private void ChangeFragment() {

        Fragment fragment = new SearchPairFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, fragment);
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentTransaction.commit();

    }

    protected int getItem(int i) {
        return pagerQuestions.getCurrentItem() + i;
    }

    //**************************** Question Adapter ************************************//
    public class QuestionAdapter extends PagerAdapter {
        private static final String TAG = "QuestionAdapter";
        private ArrayList<QuestionModel> list;


        public QuestionAdapter(ArrayList<QuestionModel> list) {
            this.list = list;
        }

        public int getCount() {
            return list.size();
        }

        private View currentView;

        public Object instantiateItem(View collection, final int position) {
            final Holder holder = new Holder();
            View v = collection;
            LayoutInflater inflater = (LayoutInflater) getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.question_item, null);
            holder.question_tv = (TextView) v
                    .findViewById(R.id.question_tv);

            final QuestionModel model = list.get(position);
            holder.question_tv.setText(model.getQuestion());

            ((ViewPager) collection).addView(v);
            collection.setTag(holder);
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
            TextView question_tv;

        }
    }


    //*************************** end of question adapter ******************************//


    // ************************** submit question answers *****************************//
    private void submitQuestion() {

        JSONArray jsonArray = getSelectedAnswerArray();

        RequestParams params = new RequestParams();
        params.put("ent_user_fbid",Constants.USER_LOGIN_ID);
        params.put("ent_json",jsonArray.toString());

        Log.e("","");

        Log.e("parameters", params.toString());
        Log.e("URL", Constants.updateAnswer_url + "?" + params.toString());
        client.post(getActivity(), Constants.updateAnswer_url, params, new JsonHttpResponseHandler() {

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
                   // {"errNum":"63","errFlag":"0","errMsg":"update answer Successfully"}
                    Log.e(TAG,""+response);

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

    private JSONArray getSelectedAnswerArray() {
        JSONArray array = new JSONArray();
        for (int i = 0; i < QuestionList.size(); i++) {
            QuestionModel model = QuestionList.get(i);
            AnswerModel modelAns = AnswerList.get(i);
            //if (model.getSelectedAnswerId() != -1) {
                JSONObject object = new JSONObject();
                try {
                    object.put("ans_id", modelAns.getAnswer());
                    object.put("q_id", model.getQuestionId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e(TAG, "OBJECT" + object.toString());
                array.put(object);
           // }
        }
        return array;
    }
}
