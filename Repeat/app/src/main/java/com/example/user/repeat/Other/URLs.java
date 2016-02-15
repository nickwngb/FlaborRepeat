package com.example.user.repeat.Other;

/**
 * Created by v on 2015/12/12.
 */
public class URLs {
    private static final String serverip = "http://140.131.115.42:8080/gbDormitory/AppProcess/";
    /**
     *   Login
     */
    public static final String url_login = serverip + "pb_login";
    /**
     *   Announcement
     */
    public static final String url_allannouncement = serverip + "pb_allannouncements";
    /**
     *   Response
     */
    public static final String url_response = serverip + "pb_response";
    public static final String url_allresponse = serverip + "pb_allresponse";
    /**
     *   Image
     */
    public static final String url_loadimage = serverip + "pb_loadimage";

    public static final String url_allproblem = serverip + "pb_allproblem";
    public static final String url_addproblem = serverip + "pb_addproblem";
    public static final String url_updatestart = serverip + "pb_updatestar";
    public static final String url_gcm_register = serverip + "gcm_register";
}
