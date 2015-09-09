package com.mocaa.tagme.homepage;
import java.util.HashSet;
import java.util.TreeSet;

import com.mocaa.tagme.R;
import com.mocaa.tagme.entity.Tag;
import com.mocaa.tagme.entity.User;
import com.mocaa.tagme.homepage.HomePage.OnHomePageChangedListener;
import com.mocaa.tagme.view.MainViewAdapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HomePageAdapter extends MainViewAdapter{

	private final int FLING_TIME = 150;
	private TreeSet<Tag> tags = new TreeSet<Tag>();
	private Context context; 
	private HomePage mHomePage;
	private SparseArray<HomeViewHolder> viewHolders =
			new SparseArray<HomeViewHolder>();
	private HashSet<Integer> flings = new HashSet<Integer>();
	
	public HomePageAdapter(Context context, User u){
		this.context = context;
		mHomePage = new HomePage(context, u);
		mHomePage.setOnHomePageChangedListener(new OnHomePageChangedListener() {
			
			@Override
			public void onHomePageChange(int type, Object value) {
				//TODO bug here
//				notifyDataSetChanged();
				animateTags();
			}
		});
	}
	
	public void updateUser(User u){
		mHomePage.updateUser(u);
	}
	
	public void updateData(){
		mHomePage.updateData();
	}
	
	public void asyncFollow(String followingAccount, String modi){
		
		mHomePage.asyncFollow(followingAccount, modi);
	}
	
	public void setTags(TreeSet<Tag> tags){
		this.tags = tags;
	}
	
	@Override
	public int getCount() {
		return tags == null ? 0:tags.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	private long prevTime = 0;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		boolean fling = false;
		flings.remove((Integer)position);
		if(System.currentTimeMillis() - prevTime < FLING_TIME){
			//head and tail
			if(position < 2 || position > tags.size()-2){
			}else{
				flings.add(position);
				fling = true;
			}
		}
		HomeViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.layout_view_tag, parent, false);
			holder = mHomePage.findView(convertView);
		}else{
			holder = (HomeViewHolder) convertView.getTag();
		}
		viewHolders.put(position, holder);
        convertView.setId(position);
		if(block){
			
		}else{
			Tag tag = (Tag) tags.toArray()[position];
			mHomePage.setView(holder, tag, HomePage.FLAG_SET_VIEW, fling);
		}
		prevTime = System.currentTimeMillis();
		return convertView;
	}
	
	public void animateTags(int p){
		mHomePage.animateTags(0, viewHolders.get(p), (Tag)tags.toArray()[p]);
	}

	/**
	 * setView when 
	 * @param p
	 */
	public void setViewIfFastFling(int p){
		if(flings.contains(p)){
			mHomePage.setView(viewHolders.get(p), (Tag)tags.toArray()[p],
					HomePage.FLAG_SET_VIEW, false);
		}
	}

	public void animateTags(){
		mHomePage.animateTagsOnChange();
	}
	
	private boolean block = false;

	@Override
	public void startMoving() {
		block = true;
	}

	@Override
	public void stopMoving() {
		block = false;
	}

	@Override
	public void notifyDataSetChanged(){
		prevTime = 0;
		super.notifyDataSetChanged();
	}
}
