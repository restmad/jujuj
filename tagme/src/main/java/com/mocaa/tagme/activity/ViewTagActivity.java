package com.mocaa.tagme.activity;

import java.util.TreeSet;

import android.view.ViewTreeObserver;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import com.mocaa.tagme.R;
import com.mocaa.tagme.adapter.CommentsAdapter;
import com.mocaa.tagme.db.TagDao;
import com.mocaa.tagme.download.AsyncLoader;
import com.mocaa.tagme.download.AsyncLoader.NetworkCallback;
import com.mocaa.tagme.entity.Comments;
import com.mocaa.tagme.entity.Tag;
import com.mocaa.tagme.entity.User;
import com.mocaa.tagme.friends.UserPool;
import com.mocaa.tagme.friends.UserPool.OnUserAddListener;
import com.mocaa.tagme.global.GlobalDefs;
import com.mocaa.tagme.homepage.HomePage;
import com.mocaa.tagme.homepage.HomePage.OnHomePageChangedListener;
import com.mocaa.tagme.homepage.HomeViewHolder;
import com.mocaa.tagme.homepage.LikesAdapter;
import com.mocaa.tagme.transport.CommentRequest;
import com.mocaa.tagme.transport.GetComments;
import com.mocaa.tagme.transport.GetSingleTag;
import com.mocaa.tagme.transport.LikeRequest;
import com.mocaa.tagme.view.KeyboardLayout;
import com.mocaa.tagme.view.KeyboardLayout.OnKeyBoardStateChangeListener;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewDebug.IntToString;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

public class ViewTagActivity extends Activity{

	private HomePage mHomePage;
	private Tag mTag;
	private User mUser;
	private AsyncLoader aLoader;
	private CommentsAdapter commentAdapter;
	private EditText commentEt;
    private GridView likeList;
    private View likeGroup;
    public LikesAdapter likeAdapter;
    private ListView commentListView;
    private View commentGroup, comment;
	private ScrollView scrollview;
	private InputMethodManager inputMethodManager;
	private TreeSet<Comments> comments;
	private UserPool userPool;
	private String replyAccount = "";
	private HomeViewHolder holder;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_tag);

		userPool = new UserPool(this);
		aLoader = new AsyncLoader(this);
		inputMethodManager = 
				(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		
		Intent intent = this.getIntent();
		
		Object tag = intent.getParcelableExtra(GlobalDefs.EXTRA_TAG);
		if(tag != null){
			mTag = (Tag) tag;
		}else{
			return;
		}
		
		Object user = intent.getParcelableExtra(GlobalDefs.EXTRA_USER);
		if(user != null){
			mUser = (User) user;
		}else{
			return;
		}
		
		mHomePage = new HomePage(this, mUser);
		mHomePage.setOnHomePageChangedListener(new OnHomePageChangedListener() {
			
			@Override
			public void onHomePageChange(int type, Object value) {
				if(type == HomePage.TYPE_LIKE){
					if(LikeRequest.M_DISLIKE.equals((String) value)){
						likeAdapter.dislike(mUser.getUserAccount());
					}else{
						likeAdapter.like(mUser.getUserAccount());
					}
					nofityDateChanged();
				}
			}
		});

		if(!mTag.isLoaded()){
			getTag();
		}else{
			findViewAndSetContent();
		}

		boolean bCommenting = intent.getBooleanExtra(GlobalDefs.EXTRA_COMMENTING, false);
		if(bCommenting){
			showComment();
		}

	}
	
	private void getTag(){
		TagDao dao = new TagDao(this);
		Tag tag = dao.getTagByServerId(mTag.getServerId());
		if(tag == null){
			aLoader.downloadMessage(new GetSingleTag(), mTag.getServerId()+"", null, 
					new NetworkCallback(){

						@Override
						public void onLoaded(Object obj) {
							Tag tag = (Tag) obj;
							mTag.copy(tag);

							findViewAndSetContent();
						}
				
			});
		}else{
			mTag.copy(tag);
			findViewAndSetContent();
		}
	}
	
	private void findViewAndSetContent(){
        commentGroup = findViewById(R.id.view_tag_comments_group);
		scrollview = (ScrollView) findViewById(R.id.view_tag_comments_scrollview);
		final View buttons = findViewById(R.id.view_tag_buttons);
		buttons.getViewTreeObserver()
				.addOnGlobalLayoutListener(new OnGlobalLayoutListener(){

			@Override
			public void onGlobalLayout() {
				if(setScrollViewPadding){
					scrollview.setPadding(0, 0, 0, buttons.getHeight());
					setScrollViewPadding = false;
				}
			}
					
		});
		
		holder = mHomePage.findView(findViewById(R.id.view_tag_root));
		mHomePage.setView(holder, mTag, HomePage.FLAG_SET_VIEW, false);
		mHomePage.animateTags(0, holder, mTag);
		resetImageView(findViewById(R.id.view_tag_img_group));
		
//		mHomePage.animateTags(0, holder, mTag);
		holder.commentBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				showComment();
			}
		});
		
		initComments();
        initLikeListView();
	}

	private void resetImageView(View v){
		ViewGroup.LayoutParams params = v.getLayoutParams();
        int width = GlobalDefs.getScreenWidth();
		params.height = width;
		params.width = width;
		v.setLayoutParams(params);
	}

	private boolean setScrollViewPadding = true;
	private boolean resetHeight = false;
	
	private void resetHeight(){
		resetHeight = true;
	}

    private void initLikeListView(){
        likeGroup = findViewById(R.id.view_tag_likes_group);
        likeList = (GridView) findViewById(R.id.view_tag_likes_list);
        final int portraitWidth = (int)getResources().getDimension(R.dimen.like_portrait);
        likeList.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            private int col = 0;

            @Override
            public void onGlobalLayout() {
                if(col == 0){
                    int width = likeList.getWidth();
                    col = width / portraitWidth;
                    likeList.setNumColumns(col);
                }
            }
        });
        likeAdapter = new LikesAdapter(this, portraitWidth, mHomePage.getUserPool());
        likeList.setAdapter(likeAdapter);
        likeAdapter.loadLikes(mTag.getServerId(), mTag.getLikes());

        int likes = mTag.getLikes();
        if(likes == 0){
            likeGroup.setVisibility(View.GONE);
        }else{
            likeGroup.setVisibility(View.VISIBLE);
            TextView likeTv = (TextView) findViewById(R.id.view_tag_like_tv);
            likeTv.setText(likes + getResources().getString(R.string.like_num));
        }
    }

    private void initComments(){
    	KeyboardLayout root = (KeyboardLayout) findViewById(R.id.view_tag_content);
    	root.setOnKeyBoardStateChangeListener(new OnKeyBoardStateChangeListener() {
			
			@Override
			public void onKeyBoardStateChange(int state) {
				if(state == KeyboardLayout.KEYBOARD_STATE_HIDE){
					comment.setVisibility(View.GONE);
				}
			}
		});
		
		commentEt = (EditText) findViewById(R.id.view_tag_comment_et);
		comment = findViewById(R.id.view_tag_comment);
		
        commentGroup.setVisibility(View.GONE);
        commentListView = (ListView) findViewById(R.id.view_tag_comments_list);
		commentAdapter = new CommentsAdapter(this, userPool);
		commentListView.setAdapter(commentAdapter);
		commentGroup.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				if(!resetHeight){
					return;
				}
				int height = 0;
				//TODO bug here
				for(int i=0; i<comments.size(); i++){
					View v = commentListView.getChildAt(i);
					if(v == null){
						v = commentListView.getChildAt(0);
					}
					height += v.getHeight();
				}
				ViewGroup.LayoutParams params = commentListView.getLayoutParams();
				params.height = height;
				commentListView.setLayoutParams(params);
				resetHeight = false;
			}
		});
		commentListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				replyAccount = ((Comments)comments.toArray()[position])
						.getUserAccount();
				commentEt.setHint(getResources().getString(R.string.reply) + 
						userPool.get(replyAccount, null).getUserName()
						);
				showComment();
			}
		});
		aLoader.downloadMessage(new GetComments(), mTag.getServerId(), 
				null, new NetworkCallback(){

					@SuppressWarnings("unchecked")
					@Override
					public void onLoaded(Object obj) {
                        comments = (TreeSet<Comments>) obj;
                        if(comments.size() == 0){
                            commentGroup.setVisibility(View.GONE);
                        }else{
                            commentGroup.setVisibility(View.VISIBLE);
                        }
                        commentAdapter.setComments(comments);
						resetHeight();
					}
			
		});
	}
	
	public void comment(View v){
		
		String content = commentEt.getText().toString();
		String myAccount = mUser.getUserAccount();
		int tagId = mTag.getServerId();
		final Comments comment = new Comments(tagId, mTag.getUserAccount(),
				myAccount, content, replyAccount);
		aLoader.downloadMessage(new CommentRequest(), comment, null, new NetworkCallback(){
 
			@Override
			public void onLoaded(Object obj) {
				int result = (Integer) obj;
				if(result > 0){
					comments.add(comment);
                    commentGroup.setVisibility(View.VISIBLE);
					int c = mTag.getComments()+1;
					mTag.setComments(c);
					resetHeight();
					commentAdapter.notifyDataSetChanged();

					nofityDateChanged();
				}
			}
			
		});

		commentEt.setText("");
		hideComment();
		scrollview.fullScroll(ScrollView.FOCUS_DOWN);
	}
	
	private void nofityDateChanged(){
		holder.likeText.setText(""+mTag.getLikes());
		holder.commentText.setText(""+mTag.getComments());
		mHomePage.animateTags(0, holder, mTag);
		
		if(mTag.getLikes() == 0){
			likeGroup.setVisibility(View.GONE);
		}else{
			if(likeGroup.getVisibility() != View.VISIBLE){
				likeGroup.setVisibility(View.VISIBLE);
			}
		}
	}
	
	private void showComment(){
		comment.setVisibility(View.VISIBLE);
		commentEt.requestFocus();
		inputMethodManager.showSoftInput(commentEt, 0);
	}
	
	private void hideComment(){
		inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken() ,
				InputMethodManager.HIDE_NOT_ALWAYS);
		comment.setVisibility(View.GONE);
	}
	
	@Override
	public boolean onKeyDown(int keyCode,KeyEvent event){
		if(keyCode == KeyEvent.KEYCODE_BACK){
			onBack();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public void back(View v){
		onBack();
		finish();
	}
	
	private void onBack(){
		Intent data = new Intent();
		data.putExtra(GlobalDefs.EXTRA_TAG, mTag);
		setResult(0, data);
	}
}
