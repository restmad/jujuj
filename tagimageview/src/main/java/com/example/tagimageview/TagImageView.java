package com.example.tagimageview;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.List;

public class TagImageView extends ImageView {

    private final String TAG = "TagImageView";

    //the root tag
    private ITag mTag;

    private final static int MAX_DRAG_ON_CANCEL = 50;
    private final static int MAX_MOVE_COUNT = 5;
    private final static int MAX_CLICK_COUNT = 10;
    private final static float SCALE_DOWN_RATIO = 0.75f;

    private ITag mCurrentTag;
    private HashMap<Long, Rect> mBoundsMap = new HashMap<>();
    private ITag mTagOnDrag = null;
    private boolean mEditable = true;

    private Paint mPaint;
    private float mPadding;
    private float mTextSize;

    private Context mContext;
    private NinePatchDrawable mTextBackgroundLeft;
    private NinePatchDrawable mTextBackgroundRight;

    private boolean mInAnimation = false;

    private Animation mTagInAnim, mTagOutAnim;

    private OnTagClickListener mOnTagClickListener;
    private OnTagDragListener mOnTagDragListener;

    public TagImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TagImageView);

        int textColor = typedArray.getColor(R.styleable.TagImageView_textColor, 0x00FFFFFF);
        mTextSize = typedArray.getDimension(R.styleable.TagImageView_textSize, 12);
        mPadding = typedArray.getDimension(R.styleable.TagImageView_padding, 0);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(textColor);
        mPaint.setTextSize(mTextSize);

        initResources();
    }

    @SuppressWarnings("deprecation")
    private void initResources() {
        Resources res = mContext.getResources();
        mTextBackgroundLeft = (NinePatchDrawable) res.getDrawable(R.drawable.tag_left);
        mTextBackgroundRight = (NinePatchDrawable) res.getDrawable(R.drawable.tag_right);

        Animation.AnimationListener listener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mInAnimation = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mInAnimation = false;
                invalidate();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };

        mTagInAnim = AnimationUtils.loadAnimation(mContext, R.anim.tag_in_foreground);
        mTagInAnim.setAnimationListener(listener);
        mTagOutAnim = AnimationUtils.loadAnimation(mContext, R.anim.tag_back_foreground);
        mTagOutAnim.setAnimationListener(listener);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!mInAnimation){
            drawTags(canvas);
        }
    }

    private void drawTags(Canvas canvas) {
        ITag current = getCurrentTag();
        if (current == null) {
            return;
        }
        List<? extends ITag> currentTags = current.getTags();
        if (currentTags != null) {
            for (ITag tag : currentTags) {
                int headPadding = (int) mTextSize;
                int tailPadding = headPadding / 2;

                String text = tag.getText();

                Point point;
                if (mEditable){
                    point = tag.getLocation();
                }else{
                    point = TagUtils.getPointInScreen(getWidth(), getHeight(), tag);
                }

                //measure text
                Rect textBounds = new Rect();
                mPaint.setTextSize(mTextSize * (mTagOnDrag == tag ? SCALE_DOWN_RATIO : 1));
                mPaint.getTextBounds(text, 0, text.length(), textBounds);

                int textWidth = textBounds.width();
                int textHeight = textBounds.height();

                int left, right;
                int top = point.y;
                int bottom = (int) (point.y + textHeight + mPadding * 2);

                if (tag.getDirection() == ITag.Direction.LEFT){
                    left = point.x;
                    right = (int) (point.x + textWidth + mPadding * 2 + headPadding + tailPadding);
                }else{
                    left = (int) (point.x - (textWidth + mPadding * 2 + tailPadding + headPadding));
                    right = point.x;
                }

                Rect bounds = new Rect(left, top, right, bottom);
                Log.d(TAG, "bounds of " + tag.getText() + ": " + bounds.left + ", " + bounds.top + ", " + bounds.right + ", " + bounds.bottom);

                mBoundsMap.put(tag.getTagId(), bounds);

                trySwitchDirection(tag, bounds);

                Drawable background = tag.getDirection() == ITag.Direction.LEFT ?
                        mTextBackgroundLeft : mTextBackgroundRight;

                background.setBounds(bounds);
                background.draw(canvas);

                if (tag.getDirection() == ITag.Direction.LEFT) {
                    canvas.drawText(text, bounds.left + mPadding + headPadding, bounds.top + textBounds.height() + mPadding, mPaint);
                }else {
                    canvas.drawText(text, bounds.left + mPadding, bounds.top + textBounds.height() + mPadding, mPaint);
                }
            }
        }
    }

    private void trySwitchDirection(ITag tag, Rect bounds){
        if (tag.getDirection() == ITag.Direction.LEFT){
            if (bounds.right > getWidth()){
                tag.setDirection(ITag.Direction.RIGHT);
            }
        }else{
            if (bounds.left < 0){
                tag.setDirection(ITag.Direction.LEFT);
            }
        }
    }

    private float startX, startY;
    private Point startTagLocation;
    private boolean cancelDrag = false;
    private int moveCount = 0;
    private boolean inDragging = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        ITag current = getCurrentTag();
        if (current == null) {
            return super.onTouchEvent(event);
        }

        List<? extends ITag> currentTags = current.getTags();
        if (currentTags == null) {
            return super.onTouchEvent(event);
        }

        float x = event.getX();
        float y = event.getY();

        Log.d(TAG, "---------start---------");
        Log.d(TAG, "event: " + event.getAction() + ": " + x + ", " + y);

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                startX = x;
                startY = y;
                for (ITag child : currentTags) {
                    Rect rect = mBoundsMap.get(child.getTagId());
                    if (rect != null) {
                        if (rect.contains((int) x, (int) y)) {
                            mTagOnDrag = child;
                            startTagLocation = new Point(
                                    mTagOnDrag.getLocation().x, mTagOnDrag.getLocation().y);
                            touchDownTag(child);
                            break;
                        }
                    }
                }

                break;
            case MotionEvent.ACTION_MOVE:
                if (mEditable) {
                    if (++moveCount >= MAX_MOVE_COUNT){
                        inDragging = true;

                        dragTag(x, y);
                    }
                }else{
                    if (!cancelDrag){
                        cancelDrag = dragOnCancel(x, y);
                        if (cancelDrag){
                            ITag tag = mTagOnDrag.getCopy();
                            mTagOnDrag = null;
                            touchCancelTag(tag);
                        }
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                if (mTagOnDrag != null && !cancelDrag){
                    ITag tag = mTagOnDrag.getCopy();
                    mTagOnDrag = null;
                    touchUpTag(tag);
                }else {
                    if (mTagOnDrag == null){
                        if (++moveCount <= MAX_CLICK_COUNT){
                            clickOnImage();
                        }
                    }
                }


                resetTouchStatus();
                break;
        }

        return true;
    }

    private void resetTouchStatus(){
        moveCount = 0;
        inDragging = false;
        cancelDrag = false;
        mTagOnDrag = null;
    }

    private void clickOnImage(){
        ITag parent = getCurrentTag().getParent();
        if (parent != null){
            setCurrentTag(parent, AnimationType.OUT);
        }
    }

    //true if cancel
    private boolean dragOnCancel(float x, float y) {
        if (mTagOnDrag != null) {
            if (Math.sqrt(Math.pow(x-startX, 2) + Math.pow(y-startY, 2)) > MAX_DRAG_ON_CANCEL){
                return true;
            }
        }
        return false;
    }

    private void touchUpTag(ITag tag){
        if (inDragging){
            if (mOnTagDragListener != null){
                mOnTagDragListener.onDrag(tag, MotionEvent.ACTION_UP);
            }
        }else{
            onClick(tag);
        }
        invalidate();
    }

    private void onClick(ITag tag){
        setCurrentTag(tag, AnimationType.IN);

        if (mOnTagClickListener != null){
            mOnTagClickListener.onClick(tag);
        }
    }

    private void touchDownTag(ITag tag){
        invalidate();

        if (mOnTagDragListener != null){
            mOnTagDragListener.onDrag(tag, MotionEvent.ACTION_DOWN);
        }
    }

    private void touchCancelTag(ITag tag){
        invalidate();
    }

    private void dragTag(float x, float y) {
        if (mTagOnDrag != null) {

            float xOffset = x - startX;
            float yOffset = y - startY;

            mTagOnDrag.getLocation().x = (int) (startTagLocation.x + xOffset);
            mTagOnDrag.getLocation().y = (int) (startTagLocation.y + yOffset);

            invalidate();

            if (mOnTagDragListener != null){
                mOnTagDragListener.onDrag(mTagOnDrag, MotionEvent.ACTION_MOVE);
            }
        }
    }

    private ITag getCurrentTag() {
        return mCurrentTag;
    }

    private void setCurrentTag(ITag tag, AnimationType animationType){
        if (tag.getTags() == null || tag.getTags().isEmpty()){
            return;
        }
        mCurrentTag = tag;

        switch (animationType){
            case IN:
                startTagInAnimation();
                break;
            case OUT:
                startTagOutAnimation();
                break;
            case NONE:
                break;
        }
    }

    private void startTagInAnimation(){
        startAnimation(mTagInAnim);
    }

    private void startTagOutAnimation(){
        startAnimation(mTagOutAnim);
    }

    public void setEditable(boolean b){
        mEditable = b;
    }

    public void setTag(ITag tag) {
        this.mTag = tag;
        setCurrentTag(tag, AnimationType.NONE);
    }

    public interface OnTagClickListener{
        void onClick(ITag tag);
    }

    public interface OnTagDragListener{
        void onDrag(ITag tag, int event);
    }

    enum AnimationType{
        IN, OUT, NONE;
    }
}
