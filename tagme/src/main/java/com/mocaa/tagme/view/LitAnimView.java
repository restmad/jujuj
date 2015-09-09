package com.mocaa.tagme.view;

import com.mocaa.tagme.R;

import android.R.integer;
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

public class LitAnimView extends View{
	
	public static final float MARGIN = 0.2f;
	private float mScale = 0.2f;
	private Bitmap bm;
	PaintFlagsDrawFilter mSetfil = new PaintFlagsDrawFilter(0, Paint.FILTER_BITMAP_FLAG);
	
	public void skew(float f){
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

	public LitAnimView(Context context, AttributeSet attrs) {
		super(context, attrs);
		getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				bm = BitmapFactory.decodeResource(
						getResources(), R.drawable.trash_hook);
				Matrix m = new Matrix();
				float sx = (float)getWidth() / (float)bm.getWidth();
				m.postScale(sx, sx);
				bm = Bitmap.createBitmap(
						bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
			}
		});
	}
	
	private Matrix matrix = new Matrix();
    
	@Override
	public void onDraw(Canvas c){
		matrix.reset();
		matrix.postRotate(-40 * mScale, bm.getHeight()/2, bm.getHeight()/2);
		matrix.postTranslate(0, getHeight()-bm.getHeight());

		c.drawBitmap(bm, matrix, null);
		
	}
}
