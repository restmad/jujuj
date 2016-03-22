package com.shinado.tagme.database;

import com.activeandroid.query.Select;
import com.shinado.tagme.URLs;
import com.shinado.tagme.entity.Tag;
import com.shinado.tagme.entity.Tags;

import java.util.List;
import java.util.Map;

import provider.database.AbsDBHandler;

public class DBHandler implements AbsDBHandler{

    public Object query(Map<String, String> params, String uri){
        if (uri.endsWith(URLs.GET_TAGS)){
            String account = params.get("account");
            Tags tags = new Tags();
            tags.tags = new Select().all().from(Tag.class).where("userAccount = ?", account).execute();
            return tags;
        }
        return null;
    }

}
