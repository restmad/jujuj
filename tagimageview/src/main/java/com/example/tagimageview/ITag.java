package com.example.tagimageview;

import android.graphics.Bitmap;
import android.graphics.Point;

import java.util.List;

public interface ITag{

    long getTagId();
    List<? extends ITag> getTags();
    String getImageUrl();
    String getText();
    Point getLocation();
    Point getScreenSize();
    Direction getDirection();
    void setDirection(Direction direction);
    ITag getCopy();
    ITag getParent();

    enum Direction{
        LEFT, RIGHT
    }

}
