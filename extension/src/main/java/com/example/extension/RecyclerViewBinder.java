package com.example.extension;

import android.content.Context;
import android.view.View;

import java.util.HashMap;

import framework.inj.impl.ViewBinder;

public class RecyclerViewBinder extends ViewBinder{
    @Override
    public String addParams(View view, HashMap<String, String> params, Object bean, String fieldName, String packageName) throws Exception {
        return null;
    }

    @Override
    public boolean setContent(Context context, View view, Object bean, String name, Object value, String packageName) {
        return false;
    }
}
