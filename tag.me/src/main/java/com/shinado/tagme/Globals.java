package com.shinado.tagme;

import android.util.Log;

public class Globals {
    public static final String URL_MAIN = "http://tagme.sinaapp.com/jujuj/";
    public static final String URL_TAG_ME = "http://tagme.sinaapp.com/";

    public static int IMAGE_WIDTH = 400;
    private static final String TAG = "Tag.me";
    public static void o(String s){
        Log.v(TAG, s);
    }

    private static int width;
    private static int height;
    private static float density;

    public static void setScreen(int w, int h, float d){
        width = w;
        height = h;
        density = d;
    }

    public static int getScreenWidth(){
        return width;
    }
    public static int getScreenHeight(){
        return height;
    }
    public static float getScreenDensity(){
        return density;
    }

}
