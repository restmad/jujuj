package com.shinado.tagme.jujuj;

import android.content.Context;
import android.view.View;

import com.example.tagimageview.ITag;
import com.example.tagimageview.TagViewGroup;

import java.util.HashMap;

import framework.inj.impl.ViewBinder;

public class TagViewBinder extends ViewBinder{

    @Override
    public String addParams(View view, HashMap<String, String> params, Object bean, String fieldName, String packageName) throws Exception {
        return null;
    }

    @Override
    public boolean setContent(Context context, View view, Object bean, String name, Object value, String packageName) {
        if (view instanceof TagViewGroup){
            if (bean instanceof ITag){
                ((TagViewGroup) view).setTag((ITag) bean);
                return true;
            }
        }
        return false;
    }
}
