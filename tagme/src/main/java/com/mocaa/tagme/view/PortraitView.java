package com.mocaa.tagme.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import com.mocaa.tagme.util.BitmapUtil;

public class PortraitView extends AnimClickableView{

	public PortraitView(Context context, AttributeSet attrs) {
		super(context, attrs);
        initAnim();
	}

//    @Override
//    public void setImageBitmap(Bitmap bm){
//        if(bm != null){
//            bm = BitmapUtil.roundBitmap(bm);
//        }
//        super.setImageBitmap(bm);
//    }

	private Animation animDown, animUp;
	
	private void initAnim(){
		animDown = new ScaleAnimation(
				1, 0.9f,
                1, 0.9f,
                Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		
		animDown.setDuration(80);
		animDown.setFillAfter(true);
		
		animUp = new ScaleAnimation(
                0.9f, 1,
                0.9f, 1,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
		animUp.setDuration(80);
		animUp.setFillAfter(true);
	}

    @Override
    public Animation getAnimDown() {
        return animDown;
    }

    @Override
    public Animation getAnimUp() {
        return animUp;
    }

	
}
