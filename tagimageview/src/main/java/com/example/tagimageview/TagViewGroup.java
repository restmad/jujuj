package com.example.tagimageview;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;

public class TagViewGroup extends FrameLayout{

    private static final String TAG = "TagViewGroup";


    private SimpleDraweeView mBackgroundView;
    private SimpleDraweeView mForegroundView;
    private TagsView mTagsView;

    private Context mContext;
    private Animation mTagInForegroundAnim, mTagOutBackgroundAnim, mTagBackForegroundAnim, mTagBackBackgroundAnim;

    public TagViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;

        initViews(context, attrs);
        initAnimation();
        setTagListener();
    }

    private void initViews(Context context, AttributeSet attrs){
        mBackgroundView = new SimpleDraweeView(context, attrs);
        GenericDraweeHierarchy bkgHierarchy = mBackgroundView.getHierarchy();
        bkgHierarchy.setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);

        mForegroundView = new SimpleDraweeView(context, attrs);
        GenericDraweeHierarchy fgrHierarchy = mForegroundView.getHierarchy();
        fgrHierarchy.setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);

        mTagsView = new TagsView(context, attrs);

        FrameLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        addView(mBackgroundView, params);
        addView(mForegroundView, params);
        addView(mTagsView, params);

    }

    private void initAnimation(){

        Animation.AnimationListener listener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (mTagsView != null){
                    mTagsView.hideTags();
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mTagsView != null){
                    mTagsView.showTags();
                }

                if (animation == mTagOutBackgroundAnim){
                    mBackgroundView.setVisibility(GONE);
                }
                if (animation == mTagBackForegroundAnim){
                    mForegroundView.setVisibility(GONE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };

        mTagInForegroundAnim = AnimationUtils.loadAnimation(mContext, R.anim.tag_in_foreground);
        mTagInForegroundAnim.setAnimationListener(listener);

        mTagOutBackgroundAnim = AnimationUtils.loadAnimation(mContext, R.anim.tag_out_background);
        mTagOutBackgroundAnim.setAnimationListener(listener);

        mTagBackForegroundAnim = AnimationUtils.loadAnimation(mContext, R.anim.tag_back_foreground);
        mTagBackForegroundAnim.setAnimationListener(listener);

        mTagBackBackgroundAnim = AnimationUtils.loadAnimation(mContext, R.anim.tag_back_background);
        mTagBackBackgroundAnim.setAnimationListener(listener);
    }

    private void setTagListener(){
        mTagsView.setOnTagClickListener(new TagsView.OnTagClickListener() {
            @Override
            public void onClick(ITag tag, TagsView.ClickType type) {
                if (tag.getTags() == null || tag.getTags().isEmpty()){
                    return;
                }

                switch (type){
                    case IN:

                        mForegroundView.setVisibility(VISIBLE);
                        mBackgroundView.setImageURI(Uri.parse(mTagsView.getCurrentTag().getImageUrl()));
                        mForegroundView.setImageURI(Uri.parse(tag.getImageUrl()));

                        mForegroundView.startAnimation(mTagInForegroundAnim);
                        mBackgroundView.startAnimation(mTagOutBackgroundAnim);
                        break;
                    case OUT:

                        mBackgroundView.setVisibility(VISIBLE);
                        mForegroundView.startAnimation(mTagBackForegroundAnim);
                        mBackgroundView.startAnimation(mTagBackBackgroundAnim);
                        break;
                }
            }
        });
    }

    public void setTag(ITag tag) {
        mTagsView.setTag(tag);
        mBackgroundView.setVisibility(GONE);
        mForegroundView.setImageURI(Uri.parse(tag.getImageUrl()));
    }

    public TagsView getTagsView(){
        return mTagsView;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Log.d(TAG, "onLayout: " + changed + ", " + left + ", " + top + ", " + right + ", " + bottom);
        super.onLayout(changed, left, top, right, right);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);

        Log.d(TAG, "onMeasure: " + width + ", " + height);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
