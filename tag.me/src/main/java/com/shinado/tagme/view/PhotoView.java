package com.shinado.tagme.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.GridView;

public class PhotoView extends GridView{

    public PhotoView(Context context, AttributeSet attrs){
        super(context, attrs);
    }
 
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
    	// Calculate entire height by providing a very large height hint.
     	// View.MEASURED_SIZE_MASK represents the largest height possible.
    	int expandSpec = MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK,
                    MeasureSpec.AT_MOST);
    	super.onMeasure(widthMeasureSpec, expandSpec);
 
     	ViewGroup.LayoutParams params = getLayoutParams();
    	params.height = getMeasuredHeight();
    }
 
}
