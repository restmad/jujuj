package com.shinado.tagme.animation;

public class Accelerator extends ScrollAnimation{

    public Accelerator(float fromXDelta, float toXDelta, float fromYDelta,
			float toYDelta) {
		super(fromXDelta, toXDelta, fromYDelta, toYDelta);
	}

	private float mFactor = 1.0f;
    private double mDoubleFactor = 2.0f;
    
    @Override
    public float getInterpolation(float input) {
        if (mFactor == 1.0f) {
            return input * input;
        } else {
            return (float)Math.pow(input, mDoubleFactor);
        }
    }

	public void setFactor(float factor) {
        mFactor = factor;
        mDoubleFactor = 2 * mFactor;
    }
}
