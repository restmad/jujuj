package com.mocaa.tagme.view;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.NinePatch;
import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.AbsoluteLayout;
import android.widget.TextView;

import com.mocaa.tagme.R;
import com.mocaa.tagme.entity.Point;
import com.mocaa.tagme.entity.Tag;
import com.mocaa.tagme.global.GlobalDefs;
import com.mocaa.tagme.util.NinePatchChunk;

import java.util.Random;

/**
 * Created by dell on 2014/10/12.
 */
public class TagView {

    private Tag mTag;
    private View view, left, right;
    private TextView textView;
    private Context context;
    private float width;
	private NinePatchDrawable mBcgLeft, mBcgRight;
	private NinePatchDrawable mBcgLocLeft, mBcgLocRight;
	private Animation anim;

    public TagView(Context context){
        this.context = context;
        
        mBcgLeft = getNinePatch(R.drawable.tag_left); 
        mBcgRight = getNinePatch(R.drawable.tag_right); 
        mBcgLocLeft = getNinePatch(R.drawable.tag_loc_left); 
        mBcgLocRight = getNinePatch(R.drawable.tag_loc_right); 

        anim = AnimationUtils.loadAnimation(context, R.anim.scale_in_out);
        anim.setInterpolator(new DecelerateInterpolator(2.3f));
		
        width = GlobalDefs.getScreenWidth() - 
        		context.getResources().getDimension(R.dimen.tag_padding) * 2;
        
        generateTag();
    }
    
    private NinePatchDrawable getNinePatch(int res){
    	Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), res);
    	byte[] chunk = bitmap.getNinePatchChunk();
    	if (NinePatch.isNinePatchChunk(chunk)) {
    	    return new NinePatchDrawable(context.getResources(),
    	          bitmap, chunk, NinePatchChunk.deserialize(chunk).mPaddings, null);
    	}
    	return null;
    }
    
    public int getServerId(){
        return mTag.getServerId();
    }

    public int getId(){
        return mTag.getId();
    }

    public TextView getTextView(){
        return textView;
    }

    public View getView(){
        return view;
    }
    
    public void resetTagDirection(Tag vo){
    	left.setVisibility(View.GONE);
    	right.setVisibility(View.GONE);
        
        switch (vo.getType()){
            case Tag.TYPE_LOCATION:
                if(vo.getDirection() == Tag.DIR_RIGHT){
                	textView.setBackgroundDrawable(mBcgLocLeft);
                }else{
                	textView.setBackgroundDrawable(mBcgLocRight);
                }
                break;
            case Tag.TYPE_IMAGE:
            	View dotView = null;
            	View goneView = null;
            	if(vo.getDirection() == Tag.DIR_RIGHT){
                	textView.setBackgroundDrawable(mBcgRight);
                	dotView = right;
                	goneView = left;
                }else{
                	textView.setBackgroundDrawable(mBcgLeft);
                	dotView = left;
                	goneView = right;
                }
                if(dotView != null){
                	dotView.setVisibility(View.VISIBLE);
                	dotView.findViewById(R.id.layout_dot_frg).startAnimation(anim);
                }	
                goneView.setVisibility(View.GONE);
                break;
            default:
                if(vo.getDirection() == Tag.DIR_RIGHT){
                	textView.setBackgroundDrawable(mBcgRight);
                }else{
                	textView.setBackgroundDrawable(mBcgLeft);
                }
                break;
        }
    }
    
    public void resetTag(Tag vo, boolean needIdSet){
    	this.mTag = vo;
    	left.setVisibility(View.GONE);
    	right.setVisibility(View.GONE);
        
        switch (vo.getType()){
            case Tag.TYPE_LOCATION:
                if(vo.getDirection() == Tag.DIR_RIGHT){
                	textView.setBackgroundDrawable(mBcgLocLeft);
                }else{
                	textView.setBackgroundDrawable(mBcgLocRight);
                }
                break;
            case Tag.TYPE_IMAGE:
            	View dotView = null;
            	View goneView = null;
            	if(vo.getDirection() == Tag.DIR_RIGHT){
                	textView.setBackgroundDrawable(mBcgRight);
                	dotView = right;
                	goneView = left;
                }else{
                	textView.setBackgroundDrawable(mBcgLeft);
                	dotView = left;
                	goneView = right;
                }
                if(dotView != null){
                	dotView.setVisibility(View.VISIBLE);
                	dotView.findViewById(R.id.layout_dot_frg).startAnimation(anim);
                }	
                goneView.setVisibility(View.GONE);
                break;
            default:
                if(vo.getDirection() == Tag.DIR_RIGHT){
                	textView.setBackgroundDrawable(mBcgRight);
                }else{
                	textView.setBackgroundDrawable(mBcgLeft);
                }
                break;
        }

        textView.setText(vo.getTitle());
        textView.setTag(vo);
        if(needIdSet){
        	/*
        	 * called by EditImgActivity
        	 * View v =  holderView.findViewById(tag.getId());
        	 */
        	view.setId(vo.getId());
        }
        
        if(vo.getType() == Tag.TYPE_IMAGE){
        	textView.setOnTouchListener(mOnTouchListener);
        }
        
        Point location = calculateLocation(vo);
        AbsoluteLayout.LayoutParams params = (AbsoluteLayout.LayoutParams) view.getLayoutParams();
        if(params == null){
        	params = new AbsoluteLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                    location.x, location.y);
        }else{
        	params.x = location.x;
        	params.y = location.y;
        }
        view.setLayoutParams(params);
    }
    
    private void generateTag(){
        view = LayoutInflater.from(context).inflate(
        		R.layout.layout_tag_img, null);
        textView = (TextView) view.findViewById(R.id.tag_text);
        left = view.findViewById(R.id.tag_dot_left);
        right = view.findViewById(R.id.tag_dot_right);
    	
//        resetTag(vo);
    }

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {

        private Animation touchDownAnim, touchUpAnim;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            System.out.println("touch tag");
            switch(event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    startCounting(v);
                    onTouchDown(v);
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    stopCounting();
                    onTouchUp(v);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    onTouchUp(v);
                    break;
            }
            return true;
        }

        private void touchDownTag(View v){
            if(touchDownAnim == null){
                touchDownAnim = new ScaleAnimation(
                        1, 0.9f,
                        1, 0.9f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                touchDownAnim.setFillAfter(true);
                touchDownAnim.setDuration(80);
            }
            v.startAnimation(touchDownAnim);
        }

        private void touchUpTag(View v){
            if(touchUpAnim == null){
                touchUpAnim = new ScaleAnimation(
                        0.9f, 1,
                        0.9f, 1,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                touchUpAnim.setFillAfter(true);
                touchUpAnim.setDuration(80);
            }
            v.startAnimation(touchUpAnim);
        }

        private void onTouchDown(View v){
            touchDownTag(v);
        }

        private void onTouchUp(View v){
            touchUpTag(v);
        }

        private final long CLICK_TIME = 200;
        private long mStartTime;
        private boolean counting = false;

        private void stopCounting(){
            counting = false;
        }

        private void startCounting(final View v){
            mStartTime = System.currentTimeMillis();
            counting = true;
            new Thread(){
                public void run(){
                    while(counting){
                    }
                    if(System.currentTimeMillis() - mStartTime <= CLICK_TIME){
                        Message msg = new Message();
                        msg.what = WHAT_CLICK;
                        msg.obj = v;
                        mHandler.sendMessage(msg);
                    }
                }
            }.start();
        }
    };

    private final int WHAT_CLICK = 2;

    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg){
            switch(msg.what){
                case WHAT_CLICK:
                    View v = (View) msg.obj;
                    Tag tag = (Tag) v.getTag();
                    if(mOnTagClickListener != null){
                        mOnTagClickListener.onTagClicked(tag);
                    }
                    break;
            }
        }
    };

    private OnTagClickListener mOnTagClickListener;
    public void setOnTagClickListener(OnTagClickListener l){
        this.mOnTagClickListener = l;
    }
    public interface OnTagClickListener{
        public void onTagClicked(Tag tag);
    }

    public Point calculateLocation(Tag tag){
        float rate = width / (float) tag.getWidth();
        int x = (int) (rate * tag.getLocation().x);
        int y = (int) (rate * tag.getLocation().y);
        Point pt = new Point(x, y);
        return pt;
    }

}
