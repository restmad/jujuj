package com.mocaa.tagme.homepage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.mocaa.tagme.R;
import com.mocaa.tagme.activity.PersonalPageActivity;
import com.mocaa.tagme.download.AsyncLoader;
import com.mocaa.tagme.download.AsyncLoader.ImageCallback;
import com.mocaa.tagme.entity.User;
import com.mocaa.tagme.friends.MyFollow;
import com.mocaa.tagme.friends.UserPool;
import com.mocaa.tagme.global.GlobalDefs;
import com.mocaa.tagme.transport.GetLikes;
import com.mocaa.tagme.util.BitmapUtil;
import com.mocaa.tagme.view.AnimClickableView;
import com.mocaa.tagme.view.MainView;
import com.mocaa.tagme.view.PortraitView;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Created by dell on 2014/10/12.
 */
public class LikesAdapter extends BaseAdapter{

    private int width;
    private Context context;
    private UserPool userPool;
    private AsyncLoader aLoader;
    private int size;
    private ArrayList<String> likes = new ArrayList<String>();

    public LikesAdapter(Context context, int width, UserPool userPool){
        this.context = context;
        this.width = width;
        this.userPool = userPool;
        aLoader = new AsyncLoader(context);
    }

    public void loadLikes(int tagId, int size){
        //already loaded
        if(this.size == size){
            return;
        }
        this.size = size;
        notifyDataSetChanged();
        aLoader.downloadMessage(new GetLikes(), ""+tagId, null, new AsyncLoader.NetworkCallback() {
            @Override
            public void onLoaded(Object obj) {
                likes = (ArrayList<String>) obj;
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getCount() {
        return size;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null){
            view = new PortraitView(context, null);
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(width, width);
            view.setLayoutParams(params);
        }
        final PortraitView imgView = (PortraitView) view;


        if(likes.size() <= i){
            imgView.setImageResource(R.drawable.portrait_default_round);
            return view;
        }

        String account = likes.get(i);
        User user = userPool.get(account, new UserPool.OnUserAddListener() {
            @Override
            public void onUserAdded(User user) {
                imgView.setImageBitmap(user.getPortraitThumb());
                notifyDataSetChanged();
            }
        });
        if(user == null){
            imgView.setImageResource(R.drawable.portrait_default_round);
        }else{
            if(user.getPortraitThumb() == null){
                imgView.setImageResource(R.drawable.portrait_default_round);
                loadUserPortrait(user, imgView);
            }else {
                imgView.setImageBitmap(user.getPortraitThumb());
            }
        }

        final User u = user;
        imgView.setOnAnimClickListener(new AnimClickableView.OnAnimClickListener() {
            @Override
            public void onClick(View v, int flag) {
                if (flag == AnimClickableView.FLAG_CLICK) {
                    Intent intent = new Intent(context, PersonalPageActivity.class);
                    intent.putExtra(GlobalDefs.EXTRA_USER, u);
                    ((Activity) context).startActivityForResult(intent, MainView.REQUEST_VIEW_PERSONAL_PAGE);
                }
            }
        });
        return imgView;
    }
    
    public void like(String account){
    	likes.add(account);
        size = likes.size();
    	notifyDataSetChanged();
    }

    public void dislike(String account){
    	likes.remove(account);
        size = likes.size();
    	notifyDataSetChanged();
    }
    
	private void loadUserPortrait(final User user, final ImageView imageView){
		aLoader.downloadImage(user.getPortraitUrl(), true, new ImageCallback(){

			@Override
			public void onImageLoaded(Bitmap bitmap, String imageUrl) {
				if(bitmap != null){
					bitmap = BitmapUtil.getPortraitBitmap(context, bitmap);
					
					user.setPortraitThumb(bitmap);
					imageView.setImageBitmap(bitmap);
				}
			}
			
		});
	}
	
}
