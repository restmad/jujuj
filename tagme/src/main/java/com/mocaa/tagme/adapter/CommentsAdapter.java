package com.mocaa.tagme.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import android.app.Activity;
import android.content.Intent;

import com.mocaa.tagme.R;
import com.mocaa.tagme.activity.PersonalPageActivity;
import com.mocaa.tagme.db.UserDao;
import com.mocaa.tagme.download.AsyncLoader;
import com.mocaa.tagme.download.AsyncLoader.ImageCallback;
import com.mocaa.tagme.download.AsyncLoader.NetworkCallback;
import com.mocaa.tagme.entity.Comments;
import com.mocaa.tagme.entity.User;
import com.mocaa.tagme.friends.MyFollow;
import com.mocaa.tagme.friends.UserPool;
import com.mocaa.tagme.friends.UserPool.OnUserAddListener;
import com.mocaa.tagme.global.GlobalDefs;
import com.mocaa.tagme.transport.GetUserTransport;
import com.mocaa.tagme.util.BitmapUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mocaa.tagme.view.AnimClickableView;
import com.mocaa.tagme.view.MainView;
import com.mocaa.tagme.view.PortraitView;

public class CommentsAdapter extends BaseAdapter{

	private TreeSet<Comments> comments;
	private Context context;
	private UserPool userPool;
	private AsyncLoader aLoader;
	
	public CommentsAdapter(Context context, UserPool userPool){
		this.context = context;
        this.userPool = userPool;
		aLoader = new AsyncLoader(context);
	}

	public void setComments(TreeSet<Comments> comments){
		this.comments = comments;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		int count = comments == null ? 0:comments.size();
		System.out.println("getCount:"+count);
		return count;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder tag = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.layout_comments, null);
			tag = new ViewHolder();
			tag.portraitIv = (PortraitView) convertView.findViewById(R.id.comments_portrait);
			tag.userNameTv = (TextView) convertView.findViewById(R.id.comments_user_name);
			tag.timeTv = (TextView) convertView.findViewById(R.id.comments_time);
			tag.contentTv = (TextView) convertView.findViewById(R.id.comments_content);
			convertView.setTag(tag);
		}else{
			tag = (ViewHolder) convertView.getTag();
		}
		
		final Comments comment = (Comments) comments.toArray()[position];
		final String userAccount = comment.getUserAccount();

		final ViewHolder holder = tag;
		User user = userPool.get(userAccount, new OnUserAddListener(){

			@Override
			public void onUserAdded(User user) {
				System.out.println("download user in comments");
				System.out.println(position + " "+ user.getUserName() + " "+ comment.getContent());
				setComments(holder, user, comment);
				notifyDataSetChanged();
			}
			
		});
		
		if(user != null){
			System.out.println("load user in comments");
			System.out.println(position + " "+ user.getUserName() + " "+ comment.getContent());
			setComments(holder, user, comment);
		}
		
		return convertView;
	}  
	
	private void setComments(final ViewHolder holder, final User user, Comments comment){
		Bitmap portrait = user.getPortraitThumb();
		if(portrait == null){
			holder.portraitIv.setImageResource(R.drawable.portrait_default_round);
			aLoader.downloadImage(user.getPortraitUrl(), true, new ImageCallback(){

				@Override
				public void onImageLoaded(Bitmap bitmap, String imageUrl) {
					if(bitmap != null){
						bitmap = BitmapUtil.getPortraitBitmap(context, bitmap);
						
						user.setPortraitThumb(bitmap);
						holder.portraitIv.setImageBitmap(bitmap);
					}
				}
				
			});
		}else{
			holder.portraitIv.setImageBitmap(portrait);
		}
        holder.portraitIv.setOnAnimClickListener(new AnimClickableView.OnAnimClickListener() {
            @Override
            public void onClick(View v, int flag) {
                if (flag == AnimClickableView.FLAG_CLICK) {
                    Intent intent = new Intent(context, PersonalPageActivity.class);
                    intent.putExtra(GlobalDefs.EXTRA_USER, user);
                    ((Activity) context).startActivityForResult(intent, MainView.REQUEST_VIEW_PERSONAL_PAGE);
                }
            }
        });

		holder.userNameTv.setText(user.getUserName());
		holder.timeTv.setText(comment.getTime(context));
		String content = comment.getContent();
		String replyAccount = comment.getReplyAccount();
		if(replyAccount.equals("")){
			holder.contentTv.setText(content);
		}else{
			String replyUserName = replyAccount;
			User u = userPool.get(replyAccount, null);
			if(u != null){
				replyUserName = u.getUserName();
			}
			String text = "@"+replyUserName + ":"+content;
			SpannableStringBuilder style = new SpannableStringBuilder(text); 
			style.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.dark)),
					replyAccount.length()+2, text.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			style.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.link)),
					0, replyAccount.length()+2, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			holder.contentTv.setText(style);
		}
	}

	class ViewHolder{
        PortraitView portraitIv;
		TextView userNameTv, timeTv, contentTv;
	}
}
