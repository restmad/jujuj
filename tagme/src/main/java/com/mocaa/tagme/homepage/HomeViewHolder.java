package com.mocaa.tagme.homepage;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.*;

import com.mocaa.tagme.entity.Tag;
import com.mocaa.tagme.view.PortraitView;

public class HomeViewHolder {

    public Tag rootTag;
    public PortraitView  portrait;
    public ImageView background;
    public ImageView foreground;
    public View likeBtn, shareBtn, commentBtn;
    public ImageView likeIcon;
    public TextView likeText, commentText;
	public TextView userName, subtitle;
	public AbsoluteLayout tagGroup;
//	public ViewGroup loadingGroup;
	public ToggleButton followToggle;
	
}
