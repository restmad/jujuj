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

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import framework.inj.ActivityInj;
import framework.inj.GroupViewInj;
import framework.inj.ViewInj;
import framework.inj.entity.ArrayDownloadable;
import framework.inj.entity.ArrayGetable;
import framework.inj.entity.ArrayPostable;
import framework.inj.entity.Downloadable;
import framework.inj.entity.Entity;
import framework.inj.entity.Getable;
import framework.inj.entity.Moreable;
import framework.inj.entity.Multipleable;
import framework.inj.entity.MutableEntity;
import framework.inj.entity.Postable;
import framework.inj.entity.Validatable;
import framework.inj.impl.AbsListViewInjector;
import framework.inj.impl.CheckBoxInjector;
import framework.inj.impl.ImageViewInjector;
import framework.inj.impl.SpinnerInjector;
import framework.inj.impl.TextViewInjector;
import framework.inj.impl.ViewInjector;
import framework.inj.impl.WebViewInjector;
import framework.net.abs.Listener;
import framework.net.impl.NetworkRequest;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class Netframe {

	protected Configuration mConfig;
	private static Netframe instance;

	private Netframe(){
		injectors = new ArrayList<ViewInjector>();
		injectors.add(new ImageViewInjector());
		injectors.add(new CheckBoxInjector());
		injectors.add(new AbsListViewInjector());
		injectors.add(new SpinnerInjector());
		injectors.add(new WebViewInjector());
		injectors.add(new TextViewInjector());
	}

	public static Netframe getInstance(){
		if(instance == null){
			instance = new Netframe();
		}
		return instance;
	}

	public void init(Configuration config){
		this.mConfig = config;
	}

	private ArrayList<ViewInjector> injectors;
	
	public void inject(Context context, Multipleable mtp){
		if (mtp == null) {
			return;
		}

		View view = setContentView(context, mtp);
		inject(context, view, mtp);
	}

	public  void inject(Context context, View view, Multipleable mtp) {
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
				if(value instanceof MutableEntity){
					inject(context, view, (MutableEntity) value);
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

	public  void inject(Context context, MutableEntity m) {
		if (m == null || m.getEntity() == null) {
			return;
		}

		View view = setContentView(context, m.getEntity());
		inject(context, view, m);
	}

	public  void inject(Context context, Object bean){
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
	public  boolean inject(Context context, View view, Object bean) {
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
	public  boolean inject(Context context, View view, MutableEntity m) {
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
			boolean b = injectEntity(context, view, (MutableEntity) m);
			if(!b){
				/**
				 * download from server and set view
				 */
				if (bean instanceof Downloadable){
					downloadEntity(context, view, (MutableEntity) m);
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
				downloadEntity(context, view, (MutableEntity) m);
				return true;
			}
		}
		
		setContent(context, view, bean);
		return false;
	}
	
	public  void setContent(Context context, View view, Object bean) {
		for (Field field : bean.getClass().getDeclaredFields()) {
			Annotation annotation = field.getAnnotation(ViewInj.class);
			if (annotation == null) {
				continue;
			}
			ViewInj inj = (ViewInj) annotation;
			int resId = inj.value();
			View v = findViewById(view, resId);
			if (v == null) {
				//TODO cant just throw it
				//when a field is owned by different layout
				//you have no idea
//				throw new ViewNotFoundException("view not found, in field "+field.getName());
				continue;
			}
			for(ViewInjector injector:injectors){
				if(injector.setContent(context, v, bean, field)){
					break;
				}
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public  View findView(Context context, ViewGroup parent, Class cls) {
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
	private  View setContentView(Context context, Object bean) {
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

	private  void setRequest(final Context context, final View view,
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
				HashMap<String, String> params = getParams(context, view, request);
				if(params == null){
					return;
				}
				
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
						encodedValue = URLEncoder.encode(value, mConfig.encoder);
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
					mConfig.networkRequest.requestArray(url, null, mConfig.charset,
							new Listener.Response<JSONArray>() {
								@Override
								public void onResponse(JSONArray obj) {
									((ArrayGetable) request).onPostResponse(context, obj);
								}
							},
							new Listener.Error() {
								@Override
								public void onError(String msg) {
									((ArrayGetable) request).onError(context, msg);
								}
							});
				}else{
					mConfig.networkRequest.requestArray(url, null, mConfig.charset,
							new Listener.Response<JSONObject>() {

								@Override
								public void onResponse(JSONObject obj) {
									request.onPostResponse(context, obj);
								}

							},
							new Listener.Error() {
								@Override
								public void onError(String msg) {
									request.onError(context, msg);
								}
							});
				}
			}
		});
	}
	
	/**
	 * 
	 * @param context
	 * @param view
	 * @param request
	 * @return null if it fails validating provided by request
	 */
	private  HashMap<String, String> getParams(Context context, View view, Object request){
		HashMap<String, String> params = new HashMap<String, String>();
		for (Field field : request.getClass().getDeclaredFields()) {
			Annotation annotation = field.getAnnotation(ViewInj.class);
			//not from view
			if (annotation == null) {
				/* 
				 * maybe it's an mutable, which wraps entities
				 */
				try {
					Object value = field.get(request);
					if (value instanceof MutableEntity){
						Object entity = ((MutableEntity) value).getEntity();
//						if(entity instanceof Getable){
							HashMap<String, String> map = getParams(context, view, entity);
							params.putAll(map);
//						}
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
					String value = injector.addParams(childView, params, request, field);
					if(value != null){
						if(request instanceof Validatable){
							String validate = ((Validatable) request).validate(
									field.getName(), value);
							if(validate != null){
								//error!
								if(request instanceof Getable){
									((Getable) request).onError(context, validate);
								}
								return null;
							}
						}
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		return params;
	}
	
	private  void setRequest(final Context context, final View view,
			final Postable request) {
		int submitId = request.getSubmitButtonId();
		Button button = (Button) findViewById(view, submitId);
		if(button == null){
			return;
		}
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				HashMap<String, String> params = getParams(context, view, request);
				if(params == null){
					return;
				}
				
				if(request instanceof ArrayPostable){
					mConfig.networkRequest.requestArray(request.onPostUrl(context), params, mConfig.charset,
							new Listener.Response<JSONArray>() {
								@Override
								public void onResponse(JSONArray obj) {
									((ArrayPostable) request).onPostResponse(context, obj);
								}
							},
							new Listener.Error() {
								@Override
								public void onError(String msg) {
									request.onError(context, msg);
								}
							});

				}else{
					mConfig.networkRequest.requestJson(request.onPostUrl(context), params, mConfig.charset,
							new Listener.Response<JSONObject>() {
								@Override
								public void onResponse(JSONObject obj) {
									request.onPostResponse(context, obj);
								}
							},
							new Listener.Error() {
								@Override
								public void onError(String msg) {
									request.onError(context, msg);
								}
							});
				}
			}
		});
	}
	
	private  void downloadEntity(final Context context, final View view,
			final MutableEntity m) {
		final Downloadable downloadable = (Downloadable) m.getEntity();
		Object params = downloadable.onDownloadParams();
		if(params == null){
			params = new HashMap<String, String>();
		}
		if(m.getEntity() instanceof ArrayDownloadable){
			mConfig.networkRequest.requestArray(downloadable.onDownLoadUrl(context), params, mConfig.charset,
					new Listener.Response<JSONArray>() {
						@Override
						public void onResponse(JSONArray obj) {
							Gson gson = generateGson();
							Class beanCls = m.getEntity().getClass();
							for (Field field : downloadable.getClass().getDeclaredFields()) {
								Annotation annotation = field.getAnnotation(ViewInj.class);
								if (annotation == null) {
									continue;
								}
								try {
									Object value = field.get(downloadable);
//									if(value instanceof Collection){
									String name = field.getName();
									String jsonString = obj.toString();
									/**
									 * make it a JSONObject
									 */
									jsonString = "{" + "\"" + name + "\"" + ":" +
											jsonString +"}";
									Downloadable entity = (Downloadable) gson.fromJson(jsonString, beanCls);
									handleDownloadObject(context, view, m, entity);
//									}
								} catch (IllegalArgumentException e) {
									e.printStackTrace();
								} catch (IllegalAccessException e) {
									e.printStackTrace();
								}
							}
						}
					},
					new Listener.Error() {
						@Override
						public void onError(String msg) {
							downloadable.onError(context, msg);
						}
					});

		}else{
			mConfig.networkRequest.requestJson(downloadable.onDownLoadUrl(context), params, mConfig.charset,
					new Listener.Response<JSONObject>() {

						@Override
						public void onResponse(JSONObject json) {
							Gson gson = generateGson();
							Class beanCls = m.getEntity().getClass();
							Downloadable obj = (Downloadable) gson.fromJson(json.toString(), beanCls);
							handleDownloadObject(context, view, m, obj);
						}

					},
					new Listener.Error() {
						@Override
						public void onError(String msg) {
							downloadable.onError(context, msg);
						}
					});
		}
	}

    private  Gson generateGson(){
        return new GsonBuilder()
                .setDateFormat("yyyy-MM-dd hh:mm:ss.S")
                .create();
    }

	private  void handleDownloadObject(Context context, View view, MutableEntity m, Downloadable obj){
		if (obj != null) {
			if(obj instanceof Entity){
				saveInDatabase((Entity) obj);
			}
			if(obj instanceof Moreable){
				//clone params etc
				Object original = (Object) m.getEntity();
				if(original != null){
					((Moreable)obj).loadMore(original);
				}
				//set content
//				setContent(context, view, obj, m);
			}else{
			}
			setContent(context, view, obj);
//			if(obj instanceof Downloadable){
				m.setEntity(obj);
				m.onStoring();
				obj.onDownLoadResponse(context);
				if(m.getNotifiable() != null){
					m.getNotifiable().onDownloadResponse();
				}
//			}
		}
	}

	private  boolean injectEntity(final Context context, final View view,
			final MutableEntity m) {
		Entity bean = (Entity) m.getEntity();
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

	private  void saveInDatabase(Entity obj) {
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


	private  View findViewById(View view, int id) {
		return view.findViewById(id);
	}

}
