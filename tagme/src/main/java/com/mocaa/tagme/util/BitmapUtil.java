package com.mocaa.tagme.util;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.net.URL;

import com.mocaa.tagme.R;
import com.mocaa.tagme.download.ImageLoaderImpl;
import com.mocaa.tagme.global.GlobalDefs;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;

public class BitmapUtil {
	

	public static byte[] bmpToByteArray(final Bitmap bmp) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, output);
		
		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static Bitmap getBitmapFromUri(Context context, Uri uri) {
		Bitmap bitmap = null;
		try {
		   BitmapFactory.Options options = new BitmapFactory.Options();
		   options.inJustDecodeBounds = true;
//		   bitmap = BitmapFactory.decodeStream(context.getContentResolver()
//				   .openInputStream(uri), null, options);
		   int picWidth = options.outWidth;
		   int picHeight = options.outHeight;
		   int screenWidth = GlobalDefs.getScreenWidth();
		   int screenHeight = GlobalDefs.getScreenHeight();
		   options.inSampleSize = 1;
		   if (picWidth > picHeight) {
			   if (picWidth > screenWidth)
				   options.inSampleSize = picWidth / screenWidth;
		   } else {
			   if (picHeight > screenHeight)
				   options.inSampleSize = picHeight / screenHeight;
		   }
		   options.inJustDecodeBounds = false;
		   bitmap = BitmapFactory.decodeStream(context.getContentResolver()
		     .openInputStream(uri), null, options);
		   System.gc();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}
	
	public static Bitmap getPortraitBitmap(Context context, Bitmap bitmap){
		int newWidth = (int) context.getResources().getDimension(R.dimen.portrait_width);
		bitmap = roundBitmap(bitmap);
		return resizeBitmap(bitmap, newWidth);
	}
	
	public static Bitmap resizeBitmap(Bitmap bitmap, int newWidth) { 
	     int width = bitmap.getWidth(); 
	     int height = bitmap.getHeight(); 
	     float temp = ((float) height) / ((float) width); 
	     int newHeight = (int) ((newWidth) * temp); 
	     float scaleWidth = ((float) newWidth) / width; 
	     float scaleHeight = ((float) newHeight) / height; 
	     Matrix matrix = new Matrix(); 
	     // resize the bit map 
	     matrix.postScale(scaleWidth, scaleHeight); 
	     // matrix.postRotate(45); 
	     Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true); 
	     bitmap.recycle(); 
	     return resizedBitmap; 
	}
	
	private static Bitmap roundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left,top,right,bottom,dst_left,dst_top,dst_right,dst_bottom;
        if (width <= height) {
                roundPx = width / 2;
                top = 0;
                bottom = width;
                left = 0;
                right = width;
                height = width;
                dst_left = 0;
                dst_top = 0;
                dst_right = width;
                dst_bottom = width;
        } else {
                roundPx = height / 2;
                float clip = (width - height) / 2;
                left = clip;
                right = width - clip;
                top = 0;
                bottom = height;
                width = height;
                dst_left = 0;
                dst_top = 0;
                dst_right = height;
                dst_bottom = height;
        }
         
        Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
         
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int)left, (int)top, (int)right, (int)bottom);
        final Rect dst = new Rect((int)dst_left, (int)dst_top, (int)dst_right, (int)dst_bottom);
        final RectF rectF = new RectF(dst);

        paint.setAntiAlias(true);
         
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);
        
        return output;
	}
	
	public static Bitmap drawImageOnImage(Bitmap background, Bitmap foreground) {
        if( background == null ) {   
           return null;   
        }   
  
        int bgWidth = background.getWidth();   
        int bgHeight = background.getHeight();   
        //int fgWidth = foreground.getWidth();   
        //int fgHeight = foreground.getHeight();   
        //create the new blank bitmap ����һ���µĺ�SRC���ȿ��һ���λͼ    
        Bitmap newbmp = Bitmap.createBitmap(bgWidth, bgHeight, Config.ARGB_8888);  
        Canvas cv = new Canvas(newbmp);   
        //draw bg into   
        cv.drawBitmap(background, 0, 0, null);//�� 0��0��꿪ʼ����bg   
        //draw fg into   
        cv.drawBitmap(foreground, 0, 0, null);//�� 0��0��꿪ʼ����fg �����Դ�����λ�û���
        //save all clip   
        cv.save(Canvas.ALL_SAVE_FLAG);//����   
        //store   
        cv.restore();//�洢   
        return newbmp;   
   }
}
