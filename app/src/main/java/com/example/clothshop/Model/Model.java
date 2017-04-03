package com.example.clothshop.Model;

import com.example.clothshop.Info.UserInfo;

/**
 * Created by 一凡 on 2017/3/25.
 */

public class Model {

    //http
    public static final String IMAGE_SAVE_PATH = "http://122.112.247.7:8080/Order/";
    public static final String PATH = "http://122.112.247.7:8080/Order/Main/";
    public static final String IMAGE_PATH = PATH + "Image.php";
    public static final String REGISTER_PATH = PATH + "Register.php";
    public static final String LOGIN_PATH = PATH + "Login.php";
    public static final String PUBLISH_PATH = PATH + "SaveMessage.php";
    public static final String USER_INFO_UPLOAD_PATH = PATH + "UserInfoUpload.php";
    public static final String HOME_PATH = PATH + "ShowHome.php";
    public static final String POST_IMAGE_PATH = PATH + "PostImage.php";
    //DB
    //user
    public static final String USER_NAME_ATTR="username";
    public static final String USER_PASSWORD_ATTR="password";
    public static final String USER_PHONE_ATTR="phone";
    public static final String USER_EMAIL_ATTR="email";
    public static final String USER_AGE_ATTR="age";
    public static final String USER_SEX_ATTR="sex";
    public static final String USER_WEIGHT_ATTR="weight";
    public static final String USER_HEIGHT_ATTR="height";
    public static final String USER_AVATAR_ATTR="avatar";
    //post
    public static final String TITLE_ATTR="title";
    public static final String CONTENT_ATTR="content";
    public static final String UID_ATTR="uid";
    public static final String UAGE_ATTR="uage";
    public static final String USEX_ATTR="usex";
    public static final String UHEIGHT_ATTR="uheight";
    public static final String UWEIGHT_ATTR="uweight";
    public static final String IMAGE_ATTR="image";
    public static final String DAY_TIME_ATTR="day_time";

    public static final String FEMALE_TEXT="女";
    public static final String MALE_TEXT="男";

    public static int SCREEMWIDTH;
    public static int LISTMARGIN;
    //islogin
    public static boolean ISLOGIN=false;
    public static final String LOGIN_MODE="login_mode";
    public static final String AUTO_LOGIN_MODE="auto_login";
    public static final String USER_LOGIN_MODE="user_login";
    //my user
    public static UserInfo MYUSER=null;

    public static final String SP_NAME_PASSWD="name_passwd";
}
