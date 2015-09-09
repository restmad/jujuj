package com.mocaa.tagme.animation;

public class AcceleraDecelerator extends ScrollAnimation{

    public AcceleraDecelerator(float fromXDelta, float toXDelta, float fromYDelta,
			float toYDelta) {
		super(fromXDelta, toXDelta, fromYDelta, toYDelta);
	}

    @Override
    public float getInterpolation(float input) {
        return (float)(Math.cos((input + 1) * Math.PI) / 2.0f) + 0.5f;
    }

}
