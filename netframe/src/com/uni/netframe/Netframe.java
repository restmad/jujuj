package com.uni.netframe;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.activeandroid.Model;
import com.android.volley.Response;
import com.google.gson.Gson;

import framework.inj.ActivityInj;
import framework.inj.GroupViewInj;
import framework.inj.ViewInj;
import framework.inj.entity.ArrayDownloadable;
import framework.inj.entity.ArrayGetable;
import framework.inj.entity.ArrayPostable;
import framework.inj.entity.Downloadable;
import framework.inj.entity.Entity;
import framework.inj.entity.Getable;
import framework.inj.entity.Multipleable;
import framework.inj.entity.MutableEntity;
import framework.inj.entity.Postable;
import framework.inj.impl.AbsListViewInjector;
import framework.inj.impl.CheckBoxInjector;
import framework.inj.impl.CheckBoxInjector.Transfer;
import framework.inj.impl.ImageViewInjector;
import framework.inj.impl.SpinnerInjector;
import framework.inj.impl.TextViewInjector;
import framework.inj.impl.ViewInjector;
import framework.net.impl.NetworkRequest;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
public class Netframe {

	protected static String encoder = "gbk";
	protected static String charset = "utf-8";
	
	/**
	 * set encoder for URL
	 * @param e utf-8, gbk...etc
	 */
	public static void setEncoder(String e){
		encoder = e;
	}
	
	public static void setCharset(String c){
		charset = c;
	}
	
	public static void setTransfer(Transfer transfer){
		for(ViewInjector inj:injectors){
			if(inj instanceof CheckBoxInjector){
				((CheckBoxInjector) inj).setTransfer(transfer);
			}
		}
	}
	
	private static ArrayList<ViewInjector> injectors
			= new ArrayList<ViewInjector>();
	
	static{
		injectors.add(new ImageViewInjector());
		injectors.add(new CheckBoxInjector());
		injectors.add(new AbsListViewInjector());
		injectors.add(new SpinnerInjector());
		injectors.add(new TextViewInjector());
	}
	
	public static void inject(Context context, Multipleable mtp){
		if (mtp == null) {
			return;
		}

		View view = setContentView(context, mtp);
		inject(context, view, mtp);
	}

	public static void inject(Context context, View view, Multipleable mtp) {
		//set request and content
		if (mtp instanceof Postable) {
			setRequest(context, view, (Postable) mtp);
		}else if (mtp instanceof Getable){
			setRequest(context, view, (Getable) mtp);
		}
		setContent(context, view, mtp);
		
		//inject for objects
		for (Field field : mtp.getClass().getDeclaredFields()) {
			try {
				Object value = field.get(mtp);
				if(value instanceof MutableEntity<?>){
					inject(context, view, (MutableEntity<?>) value);
				}else{
					inject(context, view, value);
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	public static void inject(Context context, MutableEntity<?> m) {
		if (m == null || m.getEntity() == null) {
			return;
		}

		View view = setContentView(context, m.getEntity());
		inject(context, view, m);
	}

	public static void inject(Context context, Object bean){
		if (bean == null) {
			return;
		}

		View view = setContentView(context, bean);
		inject(context, view, bean);
	}

	/**
	 * the entrance of injection
	 * for immutable
	 */
	public static boolean inject(Context context, View view, Object bean) {
		if (bean == null) {
			return false;
		}

		if (bean instanceof Postable) {
			setRequest(context, view, (Postable) bean);
		}else if (bean instanceof Getable){
			setRequest(context, view, (Getable) bean);
		}
		setContent(context, view, bean);
		return false;
	}

	/**
	 * the entrance of injection
	 * for mutable
	 */
	public static boolean inject(Context context, View view, MutableEntity<?> m) {
		if (m == null || m.getEntity() == null) {
			return false;
		}

		Object bean = m.getEntity();
		if (bean instanceof Postable) {
			setRequest(context, view, (Postable) m.getEntity());
		}else if (bean instanceof Getable){
			setRequest(context, view, (Getable) m.getEntity());
		}
		
		/**
		 * if the state is stored
		 */
		if (m.isStateStored()){
			setContent(context, view, bean);
			return true;
		}
		/**
		 * an Entity is the data stored in database
		 */
		if (bean instanceof Entity) {
			/**
			 * inject an Entity from database
			 * returns false if nothing is found
			 */
			boolean b = injectEntity(context, view, (MutableEntity<Entity>) m);
			if(!b){
				/**
				 * download from server and set view
				 */
				if (bean instanceof Downloadable){
					downloadEntity(context, view, (MutableEntity<Downloadable>) m);
					return true;
				}
			}else{
				m.onStoring();
				return true;
			}
		}else{
			/**
			 * directly download from server and set view
			 */
			if (bean instanceof Downloadable){
				downloadEntity(context, view, (MutableEntity<Downloadable>) m);
				return true;
			}
		}
		
		setContent(context, view, bean);
		return false;
	}
	
	public static void setContent(Context context, View view, Object bean) {
		for (Field field : bean.getClass().getDeclaredFields()) {
			Annotation annotation = field.getAnnotation(ViewInj.class);
			if (annotation == null) {
				continue;
			}
			ViewInj inj = (ViewInj) annotation;
			int resId = inj.value();
			View v = findViewById(view, resId);
			if (v == null) {
				continue;
			}
			try {
				for(ViewInjector injector:injectors){
					if(injector.setContent(context, v, bean, field)){
							break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static View findView(Context context, ViewGroup parent, Class cls) {
		View view = null;
		if (cls.isAnnotationPresent(GroupViewInj.class)) {
			Annotation annotation = cls.getAnnotation(GroupViewInj.class);
			if (annotation != null) {
				GroupViewInj contentInj = (GroupViewInj) annotation;
				int resId = contentInj.value();
				view = LayoutInflater.from(context).inflate(resId, null);
			}
		}
		return view;
	}

	/**
	 * setContentView
	 * @param context
	 * @param bean
	 * @return
	 */
	private static View setContentView(Context context, Object bean) {
		if (bean.getClass().isAnnotationPresent(ActivityInj.class)) {
			Annotation annotation = bean.getClass().getAnnotation(
					ActivityInj.class);
			if (annotation != null) {
				ActivityInj contentInj = (ActivityInj) annotation;
				int resId = contentInj.value();
				if (context instanceof Activity) {
					((Activity) context).setContentView(resId);
					return ((Activity) context)
							.findViewById(android.R.id.content);
				}
			}
		}
		return null;
	}

	private static void setRequest(final Context context, final View view,
			final Getable request){
		
		int submitId = request.getSubmitButtonId();
		Button button = (Button) findViewById(view, submitId);
		if(button == null){
			return;
		}
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String url = request.onPostUrl(context);
				HashMap<String, String> params = getParams(view, request);
				
				String strParams;
				if(url.contains("?")){
					strParams = "&";
				}else{
					strParams = "?";
				}
				
				for(String key:params.keySet()){
					String value = params.get(key);
					String encodedValue;
					try {
						encodedValue = URLEncoder.encode(value, encoder);
						strParams += key+"="+encodedValue+"&";
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
				
				if(strParams.endsWith("&")){
					strParams = strParams.substring(0, strParams.length()-1);
				}
				url = url+strParams;
				
				if(request instanceof ArrayGetable){
					NetworkRequest.requestArray(request.onPostUrl(context), params, charset,
							new Response.Listener<JSONArray>() {

								@Override
								public void onResponse(JSONArray obj) {
									((ArrayGetable) request).onPostResponse(context, obj);
								}

							}, null);
				}else{
					NetworkRequest.requestJson(request.onPostUrl(context), params, charset,
							new Response.Listener<JSONObject>() {

								@Override
								public void onResponse(JSONObject obj) {
									request.onPostResponse(context, obj);
								}

							}, null);
				}
			}
		});
	}

	private static HashMap<String, String> getParams(View view, Object request){
		HashMap<String, String> params = new HashMap<String, String>();
		for (Field field : request.getClass().getDeclaredFields()) {
			Annotation annotation = field.getAnnotation(ViewInj.class);
			//not from view
			if (annotation == null) {
				/* 
				 * maybe it's an mutable, I mean, who knows.
				 */
				try {
					Object value = field.get(request);
					if (value instanceof MutableEntity){
						Object entity = ((MutableEntity) value).getEntity();
						HashMap<String, String> map = getParams(view, entity);
						params.putAll(map);
					}else{
						//not from view
						params.put(field.getName(), field.get(request).toString());
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
				continue;
			}
			ViewInj inj = (ViewInj) annotation;
			int resId = inj.value();
			View childView = findViewById(view, resId);
			if (childView == null) {
				continue;
			}
			try {
				for(ViewInjector injector:injectors){
					if(injector.addParams(childView, params, request, field)){
							break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		return params;
	}
	
	private static void setRequest(final Context context, final View view,
			final Postable request) {
		int submitId = request.getSubmitButtonId();
		Button button = (Button) findViewById(view, submitId);
		if(button == null){
			return;
		}
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				HashMap<String, String> params = getParams(view, request);
				if(request instanceof ArrayPostable){
					NetworkRequest.requestArray(request.onPostUrl(context), params, charset,
							new Response.Listener<JSONArray>() {

								@Override
								public void onResponse(JSONArray obj) {
									((ArrayPostable) request).onPostResponse(context, obj);
								}

							}, null);
				}else{
					NetworkRequest.requestJson(request.onPostUrl(context), params, charset,
							new Response.Listener<JSONObject>() {

								@Override
								public void onResponse(JSONObject obj) {
									request.onPostResponse(context, obj);
								}

							}, null);
				}
			}
		});
	}
	
	private static void downloadEntity(final Context context, final View view,
			final MutableEntity<Downloadable> m) {
		Downloadable downloadable = m.getEntity();
		Object params = downloadable.onDownloadParams();
		if(params == null){
			params = new HashMap<String, String>();
		}
		if(m.getEntity() instanceof ArrayDownloadable){
			NetworkRequest.requestArray(downloadable.onDownLoadUrl(context), params, charset,
						new Response.Listener<JSONArray>() {

						@Override
						public void onResponse(JSONArray json) {
							Gson gson = new Gson();
							Class beanCls = m.getEntity().getClass();
							Downloadable bean = m.getEntity();
							for (Field field : bean.getClass().getDeclaredFields()) {
								Annotation annotation = field.getAnnotation(ViewInj.class);
								if (annotation == null) {
									continue;
								}
								try {
									Object value = field.get(bean);
									if(value instanceof Collection){
										String name = field.getName();
										String jsonString = json.toString();
										/**
										 * make it a JSONObject
										 */
										jsonString = "{" + "\"" + name + "\"" + ":" + 
												jsonString +"}";
										Object obj = gson.fromJson(jsonString, beanCls);
										handleDownloadObject(context, view, m, obj);
									}
								} catch (IllegalArgumentException e) {
									e.printStackTrace();
								} catch (IllegalAccessException e) {
									e.printStackTrace();
								}
							}
						}

					}, null);
		}else{
			NetworkRequest.requestJson(downloadable.onDownLoadUrl(context), params, charset,
						new Response.Listener<JSONObject>() {

						@Override
						public void onResponse(JSONObject json) {
							Gson gson = new Gson();
							Class beanCls = m.getEntity().getClass();
							Object obj = gson.fromJson(json.toString(), beanCls);
							handleDownloadObject(context, view, m, obj);
						}

					}, null);
		}
	}

	private static void handleDownloadObject(Context context, View view, MutableEntity<Downloadable> m, Object obj){
		if (obj != null) {
			if(obj instanceof Entity){
				saveInDatabase((Entity) obj);
			}
			setContent(context, view, obj);
//			if(obj instanceof Downloadable){
				m.setEntity((Downloadable) obj);
				m.onStoring();
//			}
		}
	}

	private static boolean injectEntity(final Context context, final View view,
			final MutableEntity<Entity> m) {
		Entity bean = m.getEntity();
		if (bean.getId() == null) {
			Entity queriedBean = bean.query();
			if (queriedBean == null || queriedBean.getId() == null) {
				return false;
			} else {
				bean = queriedBean;
				m.setEntity(bean);
				setContent(context, view, bean);
				return true;
			}
		}
		return false;
	}

	private static void saveInDatabase(Entity obj) {
		obj.save();
		for (Field field : obj.getClass().getDeclaredFields()) {
			try {
				Object value = field.get(obj);
				if (value instanceof Entity) {
					((Entity) value).setForignKey(obj);
					((Entity) value).save();
				} else if (value instanceof Collection<?>) {
					for (Object item : (Collection) value) {
						if (item instanceof Entity) {
							((Entity) item).setForignKey(obj);
							((Entity) item).save();
						}
					}
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}


	private static View findViewById(View view, int id) {
		return view.findViewById(id);
	}

}
