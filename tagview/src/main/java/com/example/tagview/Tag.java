package com.example.tagview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;

import com.example.tagimageview.ITag;
import com.example.tagimageview.download.AsyncLoader;

import java.util.List;

/**
 * Created by shinado on 2016/4/26.
 */
public class Tag implements ITag, Cloneable{

    private long id;
    private List<? extends ITag> tags;
    private ITag parent;
    private String imageUrl;
    private String text;
    private Point location;
    private Point screen;
    private Direction direction;

    public void setTags(List<Tag> tags){
        this.tags = tags;
        for (Tag tag : tags){
            tag.setParent(this);
        }
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    public void setScreen(Point screen) {
        this.screen = screen;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Tag(long id, String imageUrl, String text, Point location, Point screen, Direction direction) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.text = text;
        this.location = location;
        this.screen = screen;
        this.direction = direction;
    }

    @Override
    public long getTagId() {
        return id;
    }

    @Override
    public List<? extends ITag> getTags() {
        return tags;
    }

    @Override
    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public Point getLocation() {
        return location;
    }

    @Override
    public Point getScreenSize() {
        return screen;
    }

    @Override
    public Direction getDirection() {
        return direction;
    }

    @Override
    public Tag getCopy() {
        try {
            return (Tag) clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ITag getParent() {
        return parent;
    }

    public void setParent(ITag tag){
        this.parent = tag;
    }
}
