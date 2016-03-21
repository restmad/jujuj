package com.shinado.tagme.animation;

public class Decelerator extends ScrollAnimation{
    
    public Decelerator(float fromXDelta, float toXDelta, float fromYDelta,
			float toYDelta) {
		super(fromXDelta, toXDelta, fromYDelta, toYDelta);
	}

	public float getInterpolation(float input) {
        float result;
        if (mFactor == 1.0f) {
            result = (float)(1.0f - (1.0f - input) * (1.0f - input));
        } else {
            result = (float)(1.0f - Math.pow((1.0f - input), 2 * mFactor));
        }
        return result;
    }
	
	public void setFactor(float f){
		mFactor = f;
	}
    
    private float mFactor = 1.0f;
}
