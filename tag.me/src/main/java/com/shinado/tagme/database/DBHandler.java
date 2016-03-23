package com.shinado.tagme.database;

import com.activeandroid.query.Select;
import com.shinado.tagme.URLs;
import com.shinado.tagme.entity.Tag;
import com.shinado.tagme.entity.Tags;
import com.shinado.tagme.user.User;

import java.util.List;
import java.util.Map;

import provider.database.AbsDBHandler;

public class DBHandler implements AbsDBHandler{

    public Object query(Map<String, String> params, String uri){
        if (uri.endsWith(URLs.GET_TAGS)){
            String account = params.get("account");
            Tags tags = new Tags();
            List<Tag> list = new Select().all().from(Tag.class).where("userAccount = ?", account).execute();
            for (Tag item : list){
                tags.tags.add(item);
            }
            return tags;
        } else if (uri.endsWith(URLs.GET_USERS)) {
            String account = params.get("user_account");
            return new Select().from(User.class).where("account = ?", account).executeSingle();
        }
        return null;
    }

}
