package com.shinado.tagme.entity;


import android.graphics.Point;
import android.support.annotation.NonNull;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.example.tagimageview.ITag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import provider.database.Entity;

@Table(name = "Tags")
public class Tag extends Entity implements Serializable, Comparable<Tag>, ITag, Cloneable{

	@Column(name = "tagId")
	public int id;

	@Column(name = "userAccount")
	public String user_account = "";

	@Column(name = "title")
	public String title = "";

	@Column(name = "content")
	public String content = "";

	@Column(name = "type")
	public int t_type;

	@Column(name = "imgUrl")
	public String img_url = "";

	@Column(name = "direction")
	public Direction direction;

	@Column(name = "x")
	public int x;

	@Column(name = "y")
	public int y;

	@Column(name = "width")
	public int width;

	@Column(name = "height")
	public int height;

	@Column(name = "uploadTime")
	public String upload_time;

	@Column(name = "likes")
	public int likes;

	@Column(name = "comments")
	public int comments;

	@Column(name = "place")
	public String place = "";

	public Tag parent;

	public ArrayList<Tag> children = new ArrayList<>();

	public Tag(){
		super();
		upload_time = new TimeUtil().getTimeString();
	}

	public Tag(int id){
		this.id = id;
	}

	@Override
	public Entity query() {
		Tag rootTag = (Tag) new Select().from(Tag.class).where("tagId = " + id).execute();
		if (rootTag == null){
			return null;
		}

		List<TagDependency> tagDependencies = new Select().all().from(TagDependency.class).where("tagId = " + id).execute();
		if (tagDependencies != null){
			for (TagDependency dependency : tagDependencies){
				Tag child = (Tag) new Tag(dependency.hasTagId).query();
				rootTag.children.add(child);
			}
		}

		return rootTag;
	}

	@Override
	public Long save(){
		for (Tag child : children){
			new TagDependency(id, child.id).save();
		}
		return super.save();
	}

	@Override
	public int compareTo(@NonNull Tag another) {
		return another.id - id;
	}

	@Override
	public long getTagId() {
		return id;
	}

	@Override
	public List<? extends ITag> getTags() {
		return children;
	}

	@Override
	public String getImageUrl() {
		return img_url;
	}

	@Override
	public String getText() {
		return title;
	}

	@Override
	public Point getLocation() {
		return new Point(x, y);
	}

	@Override
	public Point getScreenSize() {
		return new Point(width, height);
	}

	@Override
	public Direction getDirection() {
		return direction;
	}

	@Override
	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	@Override
	public ITag getCopy() {
		try {
			return (Tag) clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Tag getParent() {
		return parent;
	}

	public void setParent(Tag tag){
		this.parent = tag;
	}

}
