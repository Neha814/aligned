package com.aligned.utility;

public class Constants {


    public static final String PreferenceData="PreferenceData";
    public static final String REG_ID="regid";
    public static final String APP_VERSION="app_version";
    public static final String SENDER_ID="188284807000";

    /** Async client connection timeout time */
    public static final int connection_timeout = 40 * 1000;

    private static final String urlPath = "https://phphosting.osvin.net/aligned/";
    public static String hostUrl = urlPath + "process.php/";
    public static final String user_exist=hostUrl+"fbUserExist";
    public static final String getQuestion_url = hostUrl + "get_quetion";
    public static final String login_url = hostUrl + "login";
    public static final String updateAnswer_url = hostUrl + "get_question_ans_insert";
    public static final String findMatch_url = hostUrl + "findMatches";
    public static final String inviteaction_url = hostUrl + "inviteAction";
    public static final String getliked_url = hostUrl + "getProfileMatches";

    public static final String KEY_FACEBOOK_ID = "ent_user_fbid";
    public static final String JSON = "ent_json";

    //************ Login ********************//
    public static String FB_NAME ;
    public static String FB_ID;
    public static String FB_PIC_URL;
    public static String FB_EMAIL;

    public static String GOOGLE_NAME ;
    public static String GOOGLE_PIC_URL;
    public static String GOOGLE_EMAIL;
    public static String GOOGLE_ID;

    //********* Home *********************//
    public static String USER_NAME ;
    public static String USER_PIC_URL;
    public static String USER_EMAIL;
    public static String USER_LOGIN_ID;

    //********* like dislike *************//
    public static String LIKE_DISLIKE;

}
