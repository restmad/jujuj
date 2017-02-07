package com.example.tagimageview;

import android.graphics.Point;

public class TagUtils {

    public static Point getPointInScreen(float width, float height, ITag tag){
        float tagWidth = tag.getScreenSize().x;
        float tagHeight = tag.getScreenSize().y;

        float ratioX = width / tagWidth;
        float ratioY = height / tagHeight;

        float x = ratioX * tag.getLocation().x;
        float y = ratioY * tag.getLocation().y;

        return new Point((int) x, (int) y);
    }

}
