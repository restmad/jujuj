package com.mocaa.tagme.animation;

public class Overshoot extends ScrollAnimation{
	private float mTension = 2.0f;
	
	public Overshoot(float fromXDelta, float toXDelta, float fromYDelta,
			float toYDelta) {
		super(fromXDelta, toXDelta, fromYDelta, toYDelta);
	}

	public void setTension(float t) {
		mTension = t;
    }

    public float getInterpolation(float t) {
        // _o(t) = t * t * ((tension + 1) * t + tension)
        // o(t) = _o(t - 1) + 1
        t -= 1.0f;
        return t * t * ((mTension + 1) * t + mTension) + 1.0f;
    }


}
