package com.mocaa.tagme.activity;

import java.util.ArrayList;

import android.content.res.Resources;
import com.mocaa.tagme.R;
import com.mocaa.tagme.adapter.GuideViewAdapter;
import com.mocaa.tagme.db.UserPref;
import com.mocaa.tagme.entity.Point;
import com.mocaa.tagme.entity.Tag;
import com.mocaa.tagme.global.GlobalDefs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

public class GuideActivity extends Activity{

    private Resources res;
	private ArrayList<Tag> mTags = new ArrayList<Tag>();
	private final int[] IDs = {R.id.activity_guide_dot_1,
			R.id.activity_guide_dot_2,
			R.id.activity_guide_dot_3,
			R.id.activity_guide_dot_4};

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);

        res = getResources();
		mTags.add(loadData0());
		mTags.add(loadData1());
		mTags.add(loadData2());
		mTags.add(loadData3());

		resetImageView(findViewById(R.id.activity_guide_fake_group));
		View hintView = findViewById(R.id.activity_guide_hint);
		hintView.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				v.setVisibility(View.GONE);
			}

		});

		resetImageView(hintView);

		final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.activity_guide_dot_group);
		ViewPager pager = (ViewPager) findViewById(R.id.activity_guide_pager);
		pager.setAdapter(new GuideViewAdapter(this, mTags, (TextView) hintView));
		pager.setCurrentItem(0);
		pager.setOnPageChangeListener(new OnPageChangeListener(){

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageSelected(int index) {
				radioGroup.check(IDs[index]);
			}

		});
	}

	public void start(View v){
		new UserPref(this).firstTime();
		startActivity(new Intent(this, SignInActivity.class));
		finish();
	}

    private String getStringById(int id){
        return res.getString(id);
    }

    private Tag loadData3(){
        Tag pTag = new Tag(Tag.TYPE_ROOT, 480);
        pTag.setTitle(getStringById(R.string.guide_31));
        pTag.resId = R.drawable.demo_start;
        pTag.setServerId(31);

        return pTag;
    }
    private Tag loadData2(){
        Tag pTag = new Tag(Tag.TYPE_ROOT, 480);
        pTag.setTitle(getStringById(R.string.guide_21));
        pTag.resId = R.drawable.demo_food_1;
        pTag.setServerId(21);

        ArrayList<Tag> tt_1 = new ArrayList<Tag>();
        Tag tag0 = new Tag(Tag.TYPE_IMAGE, 480);
        tag0.setTitle(getStringById(R.string.guide_22));
        tag0.resId = R.drawable.demo_food_2;
        tag0.setLocation(new Point(274, 175));
        tag0.setDirection(-1);
        tag0.setServerId(22);

        ArrayList<Tag> tt_2 = new ArrayList<Tag>();
        Tag tag1 = new Tag(Tag.TYPE_IMAGE, 480);
        tag1.resId = R.drawable.demo_food_3;
        tag1.setTitle(getStringById(R.string.guide_23));
        tag1.setLocation(new Point(18, 280));
        tag1.setDirection(1);
        tag1.setServerId(23);

        ArrayList<Tag> tt_3 = new ArrayList<Tag>();
        Tag tag2 = new Tag(Tag.TYPE_IMAGE, 480);
        tag2.resId = R.drawable.demo_food_4;
        tag2.setTitle(getStringById(R.string.guide_24));
        tag2.setLocation(new Point(295, 144));
        tag2.setDirection(-1);
        tag2.setServerId(24);
        tt_3.add(tag2);
        tag1.setTags(tt_3);

        tt_2.add(tag1);
        tag0.setTags(tt_2);

        tt_1.add(tag0);
        pTag.setTags(tt_1);

        return pTag;
    }

    private Tag loadData1(){
        Tag pTag = new Tag(Tag.TYPE_ROOT, 480);
        pTag.setTitle(getStringById(R.string.guide_11));
        pTag.resId = R.drawable.demo_hand;
        pTag.setServerId(11);

        Tag tag0 = new Tag(Tag.TYPE_LOCATION, 480);
        tag0.setTitle(getStringById(R.string.guide_12));
        tag0.setLocation(new Point(340, 55));
        tag0.setDirection(1);
        tag0.setServerId(12);

        Tag tag1 = new Tag(Tag.TYPE_IMAGE, 480);
        tag1.resId = R.drawable.demo_penang;
        tag1.setTitle(getStringById(R.string.guide_13));
        tag1.setLocation(new Point(12, 366));
        tag1.setDirection(-1);
        tag1.setServerId(13);

        ArrayList<Tag> tt = new ArrayList<Tag>();
        Tag tag2 = new Tag(Tag.TYPE_IMAGE, 480);
        tag2.resId = R.drawable.demo_penang_1;
        tag2.setTitle(getStringById(R.string.guide_14));
        tag2.setLocation(new Point(32, 163));
        tag2.setDirection(-1);
        tag2.setServerId(14);
        tt.add(tag2);
        tag1.setTags(tt);

        Tag tag3 = new Tag(Tag.TYPE_IMAGE, 480);
        tag3.resId = R.drawable.demo_eye;
        tag3.setTitle(getStringById(R.string.guide_15));
        tag3.setLocation(new Point(56, 133));
        tag3.setDirection(-1);
        tag3.setServerId(15);

        Tag tag4 = new Tag(Tag.TYPE_TEXT, 480);
        tag4.setTitle(getStringById(R.string.guide_16));
        tag4.setLocation(new Point(312, 244));
        tag4.setDirection(1);
        tag4.setServerId(16);

        ArrayList<Tag> tags = new ArrayList<Tag>();
        tags.add(tag0);
        tags.add(tag1);
        tags.add(tag3);
        tags.add(tag4);

        pTag.setTags(tags);
        return pTag;
    }

    private Tag loadData0(){
        Tag pTag = new Tag(Tag.TYPE_ROOT, 480);
        pTag.setTitle(getStringById(R.string.guide_1));
        pTag.resId = R.drawable.demo_ls;
        pTag.setServerId(1);

        Tag tag0 = new Tag(Tag.TYPE_LOCATION, 480);
        tag0.setTitle(getStringById(R.string.guide_2));
        tag0.setLocation(new Point(49, 338));
        tag0.setDirection(1);
        tag0.setServerId(2);

        Tag tag1 = new Tag(Tag.TYPE_IMAGE, 480);
        tag1.resId = R.drawable.demo_fl;
        tag1.setTitle(getStringById(R.string.guide_3));
        tag1.setLocation(new Point(47, 41));
        tag1.setDirection(1);
        tag1.setServerId(3);

        Tag tag2 = new Tag(Tag.TYPE_IMAGE, 480);
        tag2.resId = R.drawable.demo_bdl;
        tag2.setTitle(getStringById(R.string.guide_4));
        tag2.setLocation(new Point(311, 166));
        tag2.setDirection(1);
        tag2.setServerId(4);

        ArrayList<Tag> tags = new ArrayList<Tag>();
        tags.add(tag0);
        tags.add(tag1);
        tags.add(tag2);

        pTag.setTags(tags);
        return pTag;
    }

    private void resetImageView(View v){
		ViewGroup.LayoutParams params = v.getLayoutParams();
		params.height = GlobalDefs.getScreenWidth();
		params.width = GlobalDefs.getScreenWidth();
		v.setLayoutParams(params);
	}

}
