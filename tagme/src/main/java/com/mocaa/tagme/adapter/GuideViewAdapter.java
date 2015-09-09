package com.mocaa.tagme.adapter;

import java.util.ArrayList;

import android.graphics.Interpolator;
import android.os.Handler;
import android.os.Message;
import android.view.animation.*;

import com.mocaa.tagme.R;
import com.mocaa.tagme.entity.Tag;
import com.mocaa.tagme.global.GlobalDefs;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.mocaa.tagme.view.TagView;

public class GuideViewAdapter extends PagerAdapter{
	
	private Context context;
	private ArrayList<Tag> mTags;
	private ArrayList<View> mViews = new ArrayList<View>();
	private TextView hintView;
	private boolean flag = true;
	
	public GuideViewAdapter(Context context, ArrayList<Tag> tags, TextView hintView){
		this.context = context;
		this.mTags = tags;
		this.hintView = hintView;
        for(int i=0; i<mTags.size(); i++){
            mViews.add(getView(i));
        }
	}

	@Override
	public int getCount(){
		return mTags.size();
	}

	private View getView(int position){
        ViewHolder holder = new ViewHolder();
        View convertView = LayoutInflater.from(context).inflate(R.layout.layout_guide, null);
        resetImageView(convertView.findViewById(R.id.layout_guide_img_group));
        holder.bcg = (ImageView) convertView.findViewById(R.id.layout_guide_bcg);
        holder.frg = (ImageView) convertView.findViewById(R.id.layout_guide_frg);
        holder.group = (AbsoluteLayout) convertView.findViewById(R.id.layout_guide_tags);
        holder.text = (TextView) convertView.findViewById(R.id.layout_guide_text);
        convertView.setTag(holder);
        holder.index = position;
        setView(holder, position, mTags.get(position));
		return convertView;
	}
	
	class ViewHolder{
		ImageView bcg, frg;
		AbsoluteLayout group;
		TextView text;
		int index;
	}
	
	private void resetImageView(View v){
		ViewGroup.LayoutParams params = v.getLayoutParams();
		params.height = GlobalDefs.getScreenWidth();
		params.width = GlobalDefs.getScreenWidth();
		v.setLayoutParams(params);
	}

	private Tag findTagById(int id, int position){
		return findTagById(id, mTags.get(position));
	}
	
	private Tag findTagById(int id, Tag tag){
		if(tag.getServerId() == id){
			return tag;
		}
		for(Tag vo:tag.getTags()){
			if(vo.getServerId() == id){
				return vo;
			}else{
				//grandson
				Tag son = findTagById(id, vo);
				if(son != null){
					return son;
				}
			}
		}
		return null;
	}

	private void setView(final ViewHolder holder, final int position, final Tag tag){
        System.out.println("set view:"+tag.getType()+":"+tag.getTitle());
        if(tag.getType() == Tag.TYPE_ROOT){
            holder.text.setText(tag.getTitle());
        }
        holder.frg.setImageResource(tag.resId);
        final int duration = doChangingImageAnim(holder.bcg, holder.frg, tag);
        if(tag.getType() != Tag.TYPE_ROOT){
            holder.bcg.setOnClickListener(new OnClickListener(){

                @Override
                public void onClick(View v) {
                	if(v.getAnimation() != null){
                		return;
                	}
                    Tag p = findTagById(tag.getPId(), position);
                    if(p != null){
                        setView(holder, position, p);
                    }
                }

            });
        }else{
            holder.bcg.setOnClickListener(null);
        }
        clearAnimation(holder.group);
        holder.group.removeAllViews();
        holder.group.setVisibility(View.GONE);
        for(Tag vo:tag.getTags()){
            final TagView tagView = new TagView(context);
            tagView.resetTag(vo, false);
            tagView.setOnTagClickListener(new TagView.OnTagClickListener(){

                @Override
                public void onTagClicked(final Tag vo) {
                    if(vo.getType() == Tag.TYPE_IMAGE){

                        final int duration = doFadingOutAnimation(holder.group, tagView);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(duration);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(flag){
                                            hintView.setVisibility(View.VISIBLE);
                                            hintView.setText(context.getResources().getString(
                                            		R.string.hint_touch));
                                            flag = false;
                                        }
                                        setView(holder, position, vo);
                                    }
                                });
                            }
                        }).start();

                    }
                }

            });
            holder.group.addView(tagView.getView());
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(duration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        holder.group.setVisibility(View.VISIBLE);
                        doFadingInAnimation(holder.group);
                    }
                });
            }
        }).start();
	}

    private void clearAnimation(ViewGroup group){
        int size = group.getChildCount();
        for(int i=0; i<size; i++) {
            group.getChildAt(i).clearAnimation();
        }
    }

    private Handler mHandler = new Handler();

    private int doChangingImageAnim(final ImageView bcg, final ImageView frg, Tag tag){

        final int duration = 1000;
        DecelerateInterpolator it = new DecelerateInterpolator(3f);
        
        AnimationSet frgAnim = new AnimationSet(context, null);
        frgAnim.setDuration(duration);
        frgAnim.setFillAfter(true);
        frgAnim.setInterpolator(it);

        Animation frgAlphaAnim = new AlphaAnimation(0, 1);
        frgAnim.addAnimation(frgAlphaAnim);
        
        ScaleAnimation frgScaleAnim = new ScaleAnimation(2, 1, 2, 1,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        frgAnim.addAnimation(frgScaleAnim);

        frg.setVisibility(View.VISIBLE);
        frg.startAnimation(frgAnim);
        
        AnimationSet bcgAnim = new AnimationSet(context, null);
        bcgAnim.setDuration(duration);
        bcgAnim.setFillAfter(true);
        bcgAnim.setInterpolator(it);

        Animation bcgAlphaAnim = new AlphaAnimation(1, 0);
        bcgAnim.addAnimation(bcgAlphaAnim);
        
//        Animation bcgTransAnim = new TranslateAnimation(0, tag.getLocation().x, 
//        		0, tag.getLocation().y);
//        bcgAnim.addAnimation(bcgTransAnim);

        ScaleAnimation bcgScaleAnim = new ScaleAnimation(1, 2f, 1, 2f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        bcgAnim.addAnimation(bcgScaleAnim);
        
        bcg.startAnimation(bcgAnim);
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(duration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                    	bcg.setImageDrawable(frg.getDrawable());
                    	bcg.clearAnimation();
                    	frg.setImageBitmap(null);
                    	frg.setVisibility(View.GONE);
                    	GlobalDefs.o("frg gone:");
                    }
                });
            }
        }).start();

        return duration/2;
    }

    private int doFadingOutAnimation(ViewGroup group, TagView tagView){
        int duration = 150;

        DecelerateInterpolator it = new DecelerateInterpolator();
        int size = group.getChildCount();
        for(int i=0; i<size; i++){
            View view = group.getChildAt(i).findViewById(R.id.tag_text);
            if(view != null){
                Object obj = view.getTag();
                if(obj != null){
                    if(((Tag) obj).getServerId() != tagView.getServerId()){
                        Animation fadingAnim = new AlphaAnimation(1, 0);
                        fadingAnim.setDuration(150);
                        fadingAnim.setInterpolator(it);
                        fadingAnim.setFillAfter(true);
                        fadingAnim.setStartOffset(70*i);
                        group.getChildAt(i).startAnimation(fadingAnim);
                        duration += 70;
                    }
                }
            }
        }

        return duration - 100;
    }

    private void doFadingInAnimation(ViewGroup group){

        DecelerateInterpolator it = new DecelerateInterpolator();
        int size = group.getChildCount();
        for(int i=0; i<size; i++){
            Animation fadingAnim = new AlphaAnimation(0, 1);
                        fadingAnim.setDuration(150);
                        fadingAnim.setInterpolator(it);
                        fadingAnim.setFillAfter(true);
                        fadingAnim.setStartOffset(70 * i);
                        group.getChildAt(i).startAnimation(fadingAnim);
        }

    }

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}
	
	@Override
    public void destroyItem(View container, int position, Object object) {
        ((ViewPager)container).removeView(mViews.get(position));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }

    @Override
    public Object instantiateItem(View container, int position) {
    	View v = mViews.get(position);
        ((ViewPager)container).addView(v);
        return v;
    }
}
