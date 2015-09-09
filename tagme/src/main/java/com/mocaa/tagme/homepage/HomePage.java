package com.mocaa.tagme.homepage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.view.animation.*;
import android.widget.*;

import com.mocaa.tagme.R;
import com.mocaa.tagme.activity.PersonalPageActivity;
import com.mocaa.tagme.activity.ViewTagActivity;
import com.mocaa.tagme.db.LikeDao;
import com.mocaa.tagme.db.TagDao;
import com.mocaa.tagme.db.UserDao;
import com.mocaa.tagme.db.UserPref;
import com.mocaa.tagme.dialog.LoadingDialog;
import com.mocaa.tagme.dialog.ShareSelectDialog;
import com.mocaa.tagme.download.AsyncLoader;
import com.mocaa.tagme.download.AsyncLoader.ImageCallback;
import com.mocaa.tagme.download.AsyncLoader.NetworkCallback;
import com.mocaa.tagme.entity.Tag;
import com.mocaa.tagme.entity.User;
import com.mocaa.tagme.friends.MyFollow;
import com.mocaa.tagme.friends.MyFollow.OnFollowUpdateListener;
import com.mocaa.tagme.friends.UserPool;
import com.mocaa.tagme.friends.UserPool.OnUserAddListener;
import com.mocaa.tagme.global.GlobalDefs;
import com.mocaa.tagme.homepage.MyLikes.OnLikeChangedListener;
import com.mocaa.tagme.mypage.MyPageView.OnTagChangedListener;
import com.mocaa.tagme.share.Share;
import com.mocaa.tagme.share.ShareController;
import com.mocaa.tagme.test.Debug;
import com.mocaa.tagme.transport.DeleteTag;
import com.mocaa.tagme.transport.FollowRequest;
import com.mocaa.tagme.transport.GetMyLikes;
import com.mocaa.tagme.transport.GetUserTransport;
import com.mocaa.tagme.transport.LikeRequest;
import com.mocaa.tagme.transport.UploadTagTransport;
import com.mocaa.tagme.util.BitmapUtil;
import com.mocaa.tagme.view.AnimClickableView;
import com.mocaa.tagme.view.MainView;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import com.mocaa.tagme.view.PortraitView;
import com.mocaa.tagme.view.TagView;

public class HomePage {

	private final int DURATION_FADING = 120;
	private final int FADING_OFFSET = 60;
	private HashSet<Tag> showedTags = new HashSet<Tag>();
	private User mUser;
	private Context context;
	private AsyncLoader aLoader;
	private UserDao userDao;
	private UserPool userPool;
	private MyFollow myFollow;
	private MyLikes myLikes;
	private ShareController shareController;
	private Bitmap defaultPortrait, defaultImage;

	public HomePage(Context context, User u) {
		this.context = context;
		this.mUser = u;
		defaultPortrait = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.portrait_default_round);
		defaultImage = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.img_default);
		aLoader = new AsyncLoader(context);
		myLikes = MyLikes.getInstance(context, u, aLoader);
		userDao = new UserDao(context);
		userPool = new UserPool(context);
		myFollow = MyFollow.getInstance(context, u, aLoader,
				mOnFollowUpdateListener);
		shareController = new ShareController(context);
	}

	public void updateData() {
		myFollow.updateFollows();
	}

	public UserPool getUserPool() {
		return userPool;
	}

	public User get(String account, NetworkCallback callback) {
		User user = userDao.getUserByAccount(account);
		if (user == null) {
			aLoader.downloadMessage(new GetUserTransport(), null,
					new String[] { account }, callback);
		}
		return user;
	}

	// private BaseAdapter adapter;
	// public void setAdapter(BaseAdapter adapter){
	// this.adapter = adapter;
	// }

	private OnFollowUpdateListener mOnFollowUpdateListener = new OnFollowUpdateListener() {

		@Override
		public void onFollowUpdated() {
			if (mOnHomePageChangedListener != null) {
				mOnHomePageChangedListener.onHomePageChange(TYPE_FOLLOW, null);
			}
		}

	};

	public HomeViewHolder findView(View convertView) {

		HomeViewHolder holder = new HomeViewHolder();
		holder.tagGroup = (AbsoluteLayout) convertView
				.findViewById(R.id.view_tag_tags);
		holder.portrait = (PortraitView) convertView
				.findViewById(R.id.view_tag_head);
		holder.followToggle = (ToggleButton) convertView
				.findViewById(R.id.view_tag_follow);
		holder.shareBtn =  convertView.findViewById(R.id.view_tag_share_btn);
		holder.commentBtn =  convertView.findViewById(R.id.view_tag_comments_btn);
		holder.commentText = (TextView) holder.commentBtn
				.findViewById(R.id.view_tag_comments_figure);
		
		holder.likeBtn = convertView.findViewById(R.id.view_tag_likes_btn);
		holder.likeIcon = (ImageView) holder.likeBtn
				.findViewById(R.id.view_tag_likes_icon);
		holder.likeText = (TextView) holder.likeBtn
				.findViewById(R.id.view_tag_likes_figure);
		
		holder.foreground = (ImageView) convertView
				.findViewById(R.id.view_tag_img_frg);
		holder.background = (ImageView) convertView
				.findViewById(R.id.view_tag_img_bcg);
		holder.userName = (TextView) convertView
				.findViewById(R.id.view_tag_user_name);
		holder.subtitle = (TextView) convertView
				.findViewById(R.id.view_tag_subtitle);
		convertView.setTag(holder);

		resetImageView(convertView.findViewById(R.id.view_tag_img_group));

		return holder;
	}

	public MyFollow getMyFollow() {
		return myFollow;
	}

	private void setUser(HomeViewHolder holder, User user) {
		if (user != null) {
			Bitmap portrait = user.getPortraitThumb();
			if (portrait == null) {
				/*
				 * notice that userPool doesn't load portrait
				 */
				GlobalDefs.o("set portrait 10 " + holder.rootTag.getServerId());
				holder.portrait.setImageBitmap(defaultPortrait);
				loadUserPortrait(user, holder);
			} else {
				GlobalDefs.o("set portrait 1 " + holder.rootTag.getServerId());
				holder.portrait.setImageBitmap(portrait);
			}
			holder.userName.setText(user.getUserName());
		}
	}

	public User get(String account, OnUserAddListener l) {
		return userPool.get(account, l);
	}

	public static final int FLAG_SET_VIEW = 0;
	public static final int FLAG_CHANGE_VIEW = 1;
	public static final int FLAG_RETURN_VIEW = 2;

	public void animateTags(final int duration, final HomeViewHolder holder,
			final Tag tag) {
		/**
		 * TODO bottleneck
		 */
		
		setTags(holder, tag.getTags(), tag.getTags().size());

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(duration);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						showedTags.add(tag);
						doFadingInAnimation(tag.getTags().size(), holder);
						holder.tagGroup.setVisibility(View.VISIBLE);
						// if(tag.getType() != Tag.TYPE_ROOT){
						// Animation alphaInAnim = new AlphaAnimation(0, 1);
						// alphaInAnim.setDuration(200);
						// holder.subtitle.startAnimation(alphaInAnim);
						// }
					}
				});
			}
		}).start();
	}

	private boolean animating = false;

	public void animateTagsOnChange() {
		animating = true;
		new Thread() {
			public void run() {
				try {
					sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				animating = false;
			}
		}.start();
	}
	
	private void returnToParent(final Tag tag, final HomeViewHolder holder){

		final int duration = doFadingOutAnimation(tag.getTags().size(), holder);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(duration);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						Tag p = findTagById(tag.getPId(), holder.rootTag);
						if (p != null) {
							setView(holder, p, FLAG_RETURN_VIEW, false);
						}
					}
				});
			}
		}).start();
	}

	public void setView(final HomeViewHolder holder, final Tag tag,
			final int flag, boolean fling) {
		if (tag == null) {
			return;
		}
		/*
		 * who can tell what on earth it is doing?
		 * it appears to keep this tag as the root tag
		 */
		if (flag == FLAG_SET_VIEW){
			holder.rootTag = tag;
		}

		/*
		 * design to skip setting view when fast flinging 
		 */
		if(fling && !animating){
			hideViewGroup(holder.tagGroup);
			holder.foreground.setVisibility(View.GONE);
			holder.background.setImageBitmap(defaultImage);
			return;
		}

		User user = userPool.get(tag.getUserAccount(),
				new UserPool.OnUserAddListener() {

					@Override
					public void onUserAdded(User user) {
						setUser(holder, user);
						if (mOnHomePageChangedListener != null) {
							mOnHomePageChangedListener.onHomePageChange(TYPE_USER, user);
						}
					}
				});
		setUser(holder, user);

		if (flag == FLAG_CHANGE_VIEW) {
			/*
			 *  set a child tag
			 *  set foreground first, before setting background(in thread)
			 */
			Bitmap bitmap = tag.getImg(context);
			if(bitmap == null){
				holder.foreground.setImageBitmap(defaultImage);
				loadTagImage(tag, holder, flag);
			}else{
				holder.foreground.setImageBitmap(bitmap);
			}
		} 
		else if (flag == FLAG_RETURN_VIEW){
			holder.foreground.setImageDrawable(holder.background.getDrawable());
			Bitmap bitmap = tag.getImg(context);
			if(bitmap == null){
				holder.background.setImageBitmap(defaultImage);
				loadTagImage(tag, holder, flag);
			}else{
				holder.background.setImageBitmap(bitmap);
			}
		}
		else if (flag == FLAG_SET_VIEW){
			// set view from outside
			holder.foreground.setVisibility(View.GONE);
			holder.background.setImageBitmap(defaultImage);
			loadTagImage(tag, holder, flag);
		}

		holder.background.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v.getAnimation() != null) {
					// animating
					return;
				}
				if (tag.getType() == Tag.TYPE_ROOT) {
					if (showedTags.contains(tag)) {
						doFadingOutAnimation(tag.getTags().size(), holder);
					} else {
						doFadingInAnimation(tag.getTags().size(), holder);
					}
				} else {
					/*
					 * notice that it's fading out this tag's children here
					 */
					returnToParent(tag, holder);
				}
			}
		});

		if (flag != FLAG_SET_VIEW) {
			animateChangingImage(holder, tag, flag);
		}

		Debug.o("setView "+tag.getTitle());
		//hide all tags first
		hideViewGroup(holder.tagGroup);
		/*
		 * animate tags when:
		 * 1. during the tag switching
		 * 2. animateTagsOnChange() is called by users
		 */
		if (flag != FLAG_SET_VIEW || animating) {
			Debug.o("animate "+tag.getTitle());
			animateTags(0, holder, tag);
		}

		holder.subtitle.setText(tag.getTitle());
		BtnClickListener l = new BtnClickListener(tag, holder);
		holder.commentBtn.setOnClickListener(l);
		holder.likeBtn.setOnClickListener(l);
		holder.shareBtn.setOnClickListener(l);
		holder.portrait
				.setOnAnimClickListener(new AnimClickableView.OnAnimClickListener() {
					@Override
					public void onClick(View v, int flag) {
						if (flag == AnimClickableView.FLAG_CLICK) {
							Intent intent = new Intent(context,
									PersonalPageActivity.class);
							/*
							 * so fucking weird here
							 * It somehow crashes down trying to pass a User with Bitmap
							 * But it works perfectly well when jumping into ViewTagActivity
							 * So here, I put a User with only user account, 
							 * leaving PersonalPageActivity to load the info
							 */
							User u = userPool.get(tag.getUserAccount(), null);
							if(u == null){
								u = new User(tag.getUserAccount());
								u.setLoaded(false);
							}
							intent.putExtra(GlobalDefs.EXTRA_USER, u);
							((Activity) context).startActivityForResult(intent,
									MainView.REQUEST_VIEW_PERSONAL_PAGE);
						}
					}
				});

		if (myLikes.getLikes().contains(tag.getServerId())) {
			holder.likeIcon.setImageResource(R.drawable.ic_like);
		} else {
			holder.likeIcon.setImageResource(R.drawable.ic_not_like);
		}
		final String userAccount = tag.getUserAccount();
		if (userAccount.equals(mUser.getUserAccount())) {
			holder.followToggle.setVisibility(View.GONE);
		} else {
			holder.followToggle.setVisibility(View.VISIBLE);
			if (myFollow.followed(userAccount)) {
				holder.followToggle.setChecked(false);
			} else {
				holder.followToggle.setChecked(true);
			}
		}
		holder.followToggle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				myFollow.asyncFollow(tag.getUserAccount(), ((CompoundButton) v)
						.isChecked() ? FollowRequest.M_UNFOLLOW
						: FollowRequest.M_FOLLOW, mOnFollowUpdateListener);
			}

		});
		holder.likeText.setText(""+tag.getLikes());
		holder.commentText.setText(""+tag.getComments());
	}

	private void setTags(final HomeViewHolder holder, ArrayList<Tag> tags, final int size) {
		final int tagSize = tags.size();

		for (int i = tagSize; i < holder.tagGroup.getChildCount(); i++) {
			View view = holder.tagGroup.getChildAt(i);
			view.setVisibility(View.GONE);
		}

		for (int i = 0; i < tagSize; i++) {
			View view = holder.tagGroup.getChildAt(i);
			Tag child = tags.get(i);
			TagView tagView = null;
			if (view == null) {
				tagView = new TagView(context);
				view = tagView.getView();
				view.setTag(tagView);
				holder.tagGroup.addView(view);
			} else {
				tagView = (TagView) view.getTag();
			}
			view.setVisibility(View.VISIBLE);
			tagView.resetTag(child, false);
			tagView.setOnTagClickListener(new TagView.OnTagClickListener() {
				@Override
				public void onTagClicked(final Tag tag) {
					if (tag.getType() == Tag.TYPE_IMAGE) {
						final int duration = doFadingOutAnimation(size, holder);
						System.out.println("fading out:" + duration);
						new Thread(new Runnable() {
							@Override
							public void run() {
								try {
									Thread.sleep(duration);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								mHandler.post(new Runnable() {
									@Override
									public void run() {
										setView(holder, tag, FLAG_CHANGE_VIEW,
												false);
									}
								});
							}
						}).start();
					}
				}
			});
		}
		GlobalDefs.o("tag size:" + tagSize + " group size:"
				+ holder.tagGroup.getChildCount());
	}

	private Tag findTagById(int id, Tag tag) {
		if (tag.getServerId() == id) {
			return tag;
		}
		for (Tag vo : tag.getTags()) {
			if (vo.getServerId() == id) {
				return vo;
			} else {
				// grandson
				Tag son = findTagById(id, vo);
				if (son != null) {
					return son;
				}
			}
		}
		return null;
	}

	private Handler mHandler = new Handler();
	
	private int animateChangingImage(final HomeViewHolder holder, Tag tag, final  int flag){

		final int duration = 600;
		Interpolator it = new DecelerateInterpolator();

		AnimationSet frgAnim = new AnimationSet(context, null);
		frgAnim.setDuration(duration);
		frgAnim.setFillAfter(true);
		frgAnim.setInterpolator(it);

		Animation frgAlphaAnim = new AlphaAnimation(0, 1);
		if(flag == FLAG_RETURN_VIEW){
			frgAlphaAnim = new AlphaAnimation(1, 0);
		}
		frgAnim.addAnimation(frgAlphaAnim);

		ScaleAnimation frgScaleAnim = new ScaleAnimation(0, 1, 0, 1,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		if(flag == FLAG_RETURN_VIEW){
			frgScaleAnim = new ScaleAnimation(1, 0, 1, 0,
					Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
					0.5f);
		}
		frgAnim.addAnimation(frgScaleAnim);

		holder.foreground.setVisibility(View.VISIBLE);

		AnimationSet bcgAnim = new AnimationSet(context, null);
		bcgAnim.setDuration(duration);
		bcgAnim.setFillAfter(true);
		bcgAnim.setInterpolator(it);

//		Animation bcgAlphaAnim = new AlphaAnimation(1, 0.5f);
//		bcgAnim.addAnimation(bcgAlphaAnim);

		ScaleAnimation bcgScaleAnim = new ScaleAnimation(1, 2f, 1, 2f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		if(flag == FLAG_RETURN_VIEW){
			bcgScaleAnim = new ScaleAnimation(2, 1, 2, 1,
					Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
					0.5f);
		}
		bcgAnim.addAnimation(bcgScaleAnim);

//		if(flag == FLAG_CHANGE_VIEW){
			holder.foreground.startAnimation(frgAnim);
			holder.background.startAnimation(bcgAnim);
//		}else if(flag == FLAG_RETURN_VIEW){
//			holder.foreground.startAnimation(bcgAnim);
//			holder.background.startAnimation(frgAnim);
//		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(duration);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						if(flag == FLAG_CHANGE_VIEW){
							holder.background.setImageDrawable(holder.foreground
									.getDrawable());
						}
						holder.background.clearAnimation();
						holder.foreground.setImageBitmap(null);
						holder.foreground.setVisibility(View.GONE);
						GlobalDefs.o("frg gone");
					}
				});
			}
		}).start();

		return duration / 2;
	}

	/*
	 * old animation
	 */
	private int doChangingImageAnim(final HomeViewHolder holder, Tag tag, final int flag) {

		final int duration = 1000;
		Interpolator it = new DecelerateInterpolator(3f);

		AnimationSet frgAnim = new AnimationSet(context, null);
		frgAnim.setDuration(duration);
		frgAnim.setFillAfter(true);
		frgAnim.setInterpolator(it);

		Animation frgAlphaAnim = new AlphaAnimation(0, 1);
		frgAnim.addAnimation(frgAlphaAnim);

		ScaleAnimation frgScaleAnim = new ScaleAnimation(2, 1, 2, 1,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		frgAnim.addAnimation(frgScaleAnim);

		holder.foreground.setVisibility(View.VISIBLE);

		AnimationSet bcgAnim = new AnimationSet(context, null);
		bcgAnim.setDuration(duration);
		bcgAnim.setFillAfter(true);
		bcgAnim.setInterpolator(it);

		Animation bcgAlphaAnim = new AlphaAnimation(1, 0);
		bcgAnim.addAnimation(bcgAlphaAnim);

		ScaleAnimation bcgScaleAnim = new ScaleAnimation(1, 2f, 1, 2f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		bcgAnim.addAnimation(bcgScaleAnim);

		holder.foreground.startAnimation(frgAnim);
		holder.background.startAnimation(bcgAnim);

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(duration);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						if(flag == FLAG_CHANGE_VIEW){
							holder.background.setImageDrawable(holder.foreground
									.getDrawable());
						}
						holder.background.clearAnimation();
						holder.foreground.setImageBitmap(null);
						holder.foreground.setVisibility(View.GONE);
						GlobalDefs.o("frg gone:");
					}
				});
			}
		}).start();

		return duration / 2;
	}

	private int doFadingOutAnimation(int size, HomeViewHolder holder) {
		showedTags.remove(holder.rootTag);
		int duration = DURATION_FADING;

		DecelerateInterpolator it = new DecelerateInterpolator();
		for (int i = 0; i < size; i++) {
			View view = holder.tagGroup.getChildAt(i);
			if (view != null) {
				Animation fadingAnim = new AlphaAnimation(1, 0);
				fadingAnim.setDuration(100);
				fadingAnim.setInterpolator(it);
				fadingAnim.setFillAfter(true);
				fadingAnim.setStartOffset(FADING_OFFSET * i);
				view.startAnimation(fadingAnim);
				duration += FADING_OFFSET;
			}
		}

		return duration - FADING_OFFSET;
	}

	private void doFadingInAnimation(int size, HomeViewHolder holder) {
		showedTags.add(holder.rootTag);
		DecelerateInterpolator it = new DecelerateInterpolator();
		for (int i = 0; i < size; i++) {
			Animation fadingAnim = new AlphaAnimation(0, 1);
			fadingAnim.setDuration(DURATION_FADING);
			fadingAnim.setInterpolator(it);
			fadingAnim.setFillBefore(true);
			fadingAnim.setFillAfter(true);
			fadingAnim.setStartOffset(FADING_OFFSET * i);
			View view = holder.tagGroup.getChildAt(i);
			if (view != null) {
				view.startAnimation(fadingAnim);
			}
		}
	}

	private void hideViewGroup(ViewGroup group) {
		// Animation hidingAnim = new AlphaAnimation(0, 0);
		// hidingAnim.setFillAfter(true);

		int size = group.getChildCount();
		for (int i = 0; i < size; i++) {
			group.getChildAt(i).clearAnimation();
		}

		group.clearAnimation();
		group.setVisibility(View.GONE);
	}

	private void loadTagImage(final Tag tag, final HomeViewHolder holder,
			final int flag) {
		GlobalDefs.o("get bitmap "+tag.getTitle());
		aLoader.downloadImage(tag.getImgUrl(), true, new ImageCallback() {

			@Override
			public void onImageLoaded(Bitmap bitmap, String imageUrl) {
				/*
				 * in order not to misplace image
				 * it should have been 
				 * holder.rootTag.equals(tag)
				 * but here is contains, as long as they're in the same group
				 * image or tags of image
				 */
				if(contains(holder.rootTag, tag)){
					holder.background.setImageBitmap(bitmap);
//					tag.setImg(bitmap);
//					if(flag == FLAG_CHANGE_VIEW){
//						holder.foreground.setImageBitmap(bitmap);
//					}
				}else{
					GlobalDefs.o("bitmap wrong " + tag.getTitle() + 
							" "+holder.rootTag.getTitle());
				}
			}
		});
	}

	private boolean contains(Tag t1, Tag t2){
		if(t1.equals(t2)){
			return true;
		}
		
		for(Tag child:t1.getTags()){
			boolean b = contains(child, t2);
			if(b){
				return b;
			}
		}
		
		return false;
	}
	
	private void loadUserPortrait(final User user, final HomeViewHolder holder) {
		
		aLoader.downloadImage(user.getPortraitUrl(), true, new ImageCallback() {

			@Override
			public void onImageLoaded(Bitmap bitmap, String imageUrl) {
				if (bitmap != null) {
					/*
					 * in order not to misplace portrait
					 */
					if(user.getUserAccount().equals(holder.rootTag.getUserAccount())){
						GlobalDefs.o("set portrait 2 " + holder.rootTag.getServerId());
						bitmap = BitmapUtil.getPortraitBitmap(context, bitmap);
						user.setPortraitThumb(bitmap);
						holder.portrait.setImageBitmap(bitmap);
					}else{
						GlobalDefs.o("set portrait -2 " + holder.rootTag.getServerId());
					}
				} else {
					GlobalDefs.o("set portrait -1 " + holder.rootTag.getServerId());
					user.setPortraitThumb(defaultPortrait);
				}
			}

		});
	}

	private void resetImageView(View v) {
		ViewGroup.LayoutParams params = v.getLayoutParams();
		int width = GlobalDefs.getScreenWidth()
				- (int) context.getResources()
						.getDimension(R.dimen.tag_padding) * 2;
		params.height = width;
		params.width = width;
		v.setLayoutParams(params);
	}

	private void startAnimation(ViewGroup groupView) {
		int size = groupView.getChildCount();
		for (int i = 0; i < size; i++) {
			View v = groupView.getChildAt(i);
			Animation anim = new ScaleAnimation(1, 0, 1, 0,
					Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
			anim.setDuration(900 - i * 250);
			anim.setStartOffset(i * 250);
			anim.setInterpolator(new AccelerateDecelerateInterpolator());
			anim.setRepeatMode(Animation.REVERSE);
			anim.setRepeatCount(Animation.INFINITE);
			v.startAnimation(anim);
		}
	}

	private void clearAnimation(ViewGroup groupView) {
		int size = groupView.getChildCount();
		for (int i = 0; i < size; i++) {
			View v = groupView.getChildAt(i);
			v.clearAnimation();
		}
	}

	private Animation likeAnim, dislikeAnim;

	private void toggleLike(HomeViewHolder holder, Tag tag) {
		
		if (myLikes.getLikes().contains(tag.getServerId())) {
			// unlike
			if (dislikeAnim == null) {
				dislikeAnim = new ScaleAnimation(1f, 0.9f, 1f, 0.9f,
						Animation.RELATIVE_TO_SELF, 0.5f,
						Animation.RELATIVE_TO_SELF, 0.5f);
				dislikeAnim.setDuration(150);
				dislikeAnim.setInterpolator(new AccelerateInterpolator());
				dislikeAnim.setFillAfter(false);
			}
			holder.likeIcon.startAnimation(dislikeAnim);
			holder.likeIcon.setImageResource(R.drawable.ic_not_like);
			myLikes.asyncLike(tag, LikeRequest.M_DISLIKE,
					new OnLikeChangedListener() {
						@Override
						public void onLikeChanged(String modi) {
							if (mOnHomePageChangedListener != null) {
								mOnHomePageChangedListener.onHomePageChange(TYPE_LIKE, 
										LikeRequest.M_DISLIKE);
							}
						}
					});
		} else {
			// like
			if (likeAnim == null) {
				likeAnim = new ScaleAnimation(0, 1.1f, 0, 1.1f,
						Animation.RELATIVE_TO_SELF, 0.5f,
						Animation.RELATIVE_TO_SELF, 0.5f);
				likeAnim.setInterpolator(new AccelerateInterpolator());
				likeAnim.setDuration(250);
				likeAnim.setFillAfter(false);
			}
			holder.likeIcon.startAnimation(likeAnim);

			holder.likeIcon.setImageResource(R.drawable.ic_like);
			myLikes.asyncLike(tag, LikeRequest.M_LIKE,
					new OnLikeChangedListener() {
						@Override
						public void onLikeChanged(String modi) {
							if (mOnHomePageChangedListener != null) {
								mOnHomePageChangedListener.onHomePageChange(TYPE_LIKE, 
										LikeRequest.M_LIKE);
							}
						}
					});
		}
		for (int i : myLikes.getLikes()) {
			System.out.println(i + "");
		}
	}

	private class BtnClickListener implements OnClickListener {

		private Tag tag;
		private HomeViewHolder holder;

		public BtnClickListener(Tag tag, HomeViewHolder holder) {
			this.tag = tag;
			this.holder = holder;
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.view_tag_comments_btn:
				Intent intent = new Intent(context, ViewTagActivity.class);
				intent.putExtra(GlobalDefs.EXTRA_TAG, tag);
				intent.putExtra(GlobalDefs.EXTRA_USER,
						userPool.get(UserPref.getUserAccount(context), null));
				intent.putExtra(GlobalDefs.EXTRA_COMMENTING, true);
				((Activity) context).startActivityForResult(intent,
						MainView.REQUEST_VIEW_TAG);
				break;
			case R.id.view_tag_likes_btn:
				toggleLike(holder, tag);
				break;
			case R.id.view_tag_share_btn:
				selectShare(holder, tag);
				break;
			}
		}
	};

	@SuppressWarnings("static-access")
	private void selectShare(final HomeViewHolder holder, final Tag tag) {
		ShareSelectDialog.createDialog(context)
				.setOnListener(new ShareSelectDialog.BtnListener() {

					@Override
					public void onClicked(int id) {
						if(id == R.id.admin_delete){
							adminDeleteTag(tag);
						}
						else{
							shareController.share(id, holder);
						}
					}
				}).show();
	}
	
	private void adminDeleteTag(final Tag tag){
		aLoader.downloadMessage(new DeleteTag(), null, 
				new String[]{tag.getServerId()+"", mUser.getUserAccount()}, 
				new NetworkCallback(){

			@Override
			public void onLoaded(Object obj) {
				if((Integer) obj > 0){
					TagDao tagDao = new TagDao(context);
					tagDao.deleteTagByServerId(tag);
					Toast.makeText(context, "Object eliminated", 
							Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(context, R.string.toast_delete_fail, 
							Toast.LENGTH_LONG).show();
				}
			}
			   
		});
	}

	public HashSet<Integer> getLikes() {
		return myLikes.getLikes();
	}

	// private OnUserAddListener mOnUserAddListener;
	// public void setOnUserAddListener(OnUserAddListener l){
	// this.mOnUserAddListener = l;
	// }
	// public interface OnUserAddListener{
	// public void onUserAdded(User user);
	// }
	// private OnDataChangeListener mOnDataChangeListener;
	// public void setOnDataChangeListener(OnDataChangeListener l){
	// this.mOnDataChangeListener = l;
	// }
	// public interface OnDataChangeListener{
	// public void onDataChanged();
	// }

	public void asyncFollow(String followingAccount, String modi) {
		myFollow.asyncFollow(followingAccount, modi, mOnFollowUpdateListener);
	}

	public void updateUser(User u) {
		userPool.putUser(u);
	}
	
	public static final int TYPE_USER = 1;
	public static final int TYPE_LIKE = 2;
	public static final int TYPE_FOLLOW = 2;

	private OnHomePageChangedListener mOnHomePageChangedListener;

	public void setOnHomePageChangedListener(OnHomePageChangedListener l) {
		this.mOnHomePageChangedListener = l;
	}

	public interface OnHomePageChangedListener {
		public void onHomePageChange(int type, Object value);
	}
}
