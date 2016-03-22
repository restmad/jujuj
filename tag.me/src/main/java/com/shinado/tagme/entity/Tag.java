package com.shinado.tagme.entity;


import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import provider.database.Entity;

@Table(name = "Tags")
public class Tag extends Entity implements Serializable{

	@Column(name = "likes")
	public int likes;

	@Column(name = "comments")
	public int comments;

	@Column(name = "tagId")
	public int id;

	@Column(name = "userAccount")
	public String user_account = "";

	@Column(name = "title")
	public String title = "";

	@Column(name = "content")
	public String content = "";

	@Column(name = "place")
	public String place = "";

	@Column(name = "type")
	public int t_type;

	@Column(name = "uploadTime")
	public String upload_time;

	@Column(name = "imgUrl")
	public String img_url = "";

	@Column(name = "x")
	public int x;

	@Column(name = "y")
	public int y;

	@Column(name = "width")
	public int width;

	@Column(name = "height")
	public int height;

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

}
