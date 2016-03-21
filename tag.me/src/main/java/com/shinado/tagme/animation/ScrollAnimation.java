package com.shinado.tagme.animation;


public abstract class ScrollAnimation {

    private float mFromXDelta;
    private float mToXDelta;
    private float mFromYDelta;
    private float mToYDelta;
    
	private int mDuration;
	private long mStartTime;
	
	public ScrollAnimation(float fromXDelta, float toXDelta, 
			float fromYDelta, float toYDelta) {
		mFromXDelta = fromXDelta;
		mToXDelta = toXDelta;
		mFromYDelta = fromYDelta;
		mToYDelta = toYDelta;

    }
	public void setDuration(int d){
		this.mDuration = d;
	}
	
	public Point applyTransformation(float interpolatedTime) {
        float dx = mFromXDelta;
        float dy = mFromYDelta;
        if (mFromXDelta != mToXDelta) {
            dx = mFromXDelta + ((mToXDelta - mFromXDelta) * interpolatedTime);
        }
        if (mFromYDelta != mToYDelta) {
            dy = mFromYDelta + ((mToYDelta - mFromYDelta) * interpolatedTime);
        }
        return new Point(dx, dy);
    }
	
	public abstract float getInterpolation(float t);
	
	public void start(){
		mStartTime = System.currentTimeMillis();
	}
	
	public boolean isEnding(long currentTime){
		return mStartTime+mDuration < currentTime;
	}

	public boolean isEnding(){
		return mStartTime+mDuration < System.currentTimeMillis();
	}
	
	public Point getMoving(){
		return getMoving(System.currentTimeMillis());
	}
	
	public Point getMoving(long currentTime){
		final long duration = mDuration;
        float normalizedTime;
        if (duration != 0) {
            normalizedTime = ((float) (currentTime - mStartTime)) /
                    (float) duration;
        } else {
            // time is a step-change with a zero duration
            normalizedTime = currentTime < mStartTime ? 0.0f : 1.0f;
        }
		Point pt = applyTransformation(getInterpolation(normalizedTime));
		return pt;
	}
}
