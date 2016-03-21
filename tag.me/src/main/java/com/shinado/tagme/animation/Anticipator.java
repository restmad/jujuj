package com.shinado.tagme.animation;

public class Anticipator extends ScrollAnimation{

    private float mTension = 2.0f;
    public Anticipator(float fromXDelta, float toXDelta, float fromYDelta,
			float toYDelta) {
		super(fromXDelta, toXDelta, fromYDelta, toYDelta);
	}

    public float getInterpolation(float t) {
        // a(t) = t * t * ((tension + 1) * t - tension)
        return t * t * ((mTension + 1) * t - mTension);
    }
    
    public void setTension(float t){
    	mTension = t;
    }
}
