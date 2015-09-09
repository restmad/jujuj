package com.mocaa.tagme.view;

import com.mocaa.tagme.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

public class LikeAnimView extends View{
	
	public static final float MARGIN = 0.18f;
	private float mScale = 0.2f;
	private Bitmap bm;
	PaintFlagsDrawFilter mSetfil = new PaintFlagsDrawFilter(0, Paint.FILTER_BITMAP_FLAG);
	
	public void flow(float f){
		System.out.println("float:"+f);
		if(f > 1){
			f = 1;
		}
		if(f < 0){
			f = 0;
		}
		this.mScale = f;
		invalidate();
	}

	public LikeAnimView(Context context, AttributeSet attrs) {
		super(context, attrs);
		getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				bm = BitmapFactory.decodeResource(
						getResources(), R.drawable.like_solid);
				Matrix m = new Matrix();
				float scale = (float)getWidth() / (float)bm.getWidth();
				m.postScale(scale, scale);
				bm = Bitmap.createBitmap(
						bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
			}
		});
	}

	@Override
	public void onDraw(Canvas c){
		c.save();
		c.setDrawFilter(mSetfil);
		c.clipRect(0, (int) (getHeight() - getHeight()*mScale),
				getWidth(), getHeight());

		c.drawBitmap(bm, 0, 0, null); 
		c.restore();
		
	}
}
