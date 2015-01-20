package framework.inj.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;

import org.json.JSONObject;

import com.activeandroid.Model;
import com.android.volley.Response;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import framework.inj.ImageViewInj;
import framework.inj.ViewGroupInj;
import framework.inj.ViewInj;
import framework.inj.entity.Downloadable;
import framework.inj.entity.Entity;
import framework.inj.entity.MutableEntity;
import framework.inj.entity.Postable;
import framework.net.image.ImageUploader;
import framework.net.image.Uploadable;
import framework.net.impl.NetworkRequest;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class Netframe {

	public static void inject(Context context, MutableEntity<?> m) {
		if (m == null || m.getEntity() == null) {
			return;
		}

		View view = setContentView(context, m.getEntity());
		inject(context, view, m);
	}

	@SuppressWarnings("unchecked")
	public static void inject(Context context, View view, MutableEntity<?> m) {
		if (m == null || m.getEntity() == null) {
			return;
		}

		Object bean = m.getEntity();
		if (bean instanceof Postable) {
			setRequest(context, view, (Postable) m.getEntity());
		}
		if (bean instanceof Entity) {
			boolean b = injectEntity(context, view, (MutableEntity<Entity>) m);
			if(!b){
				if (bean instanceof Downloadable){
					downloadEntity(context, view, (MutableEntity<Downloadable>) m);
				}
			}
		}else{
			if (bean instanceof Downloadable){
				downloadEntity(context, view, (MutableEntity<Downloadable>) m);
			}
		}
		if(! (bean instanceof Entity) &&
				! (bean instanceof Downloadable) &&
				! (bean instanceof Postable)){
			setContent(context, view, bean);
		}
	}

	private static void setListView(Context context, AbsListView view,
			Collection<Object> list) {
		LazyAdapter adapter = new LazyAdapter(context, list);
		view.setAdapter(adapter);
	}

	static void setContent(Context context, View view, Object bean) {
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
				Object value = field.get(bean);
				if (v instanceof ViewGroup) {
					if (v instanceof ListView || v instanceof GridView) {
						if (value instanceof Collection) {
							setListView(context, (AbsListView) v,
									(Collection<Object>) value);
						}
					}
				} else {
					String str = getString(value);
					if (v instanceof TextView) {
						((TextView) v).setText(str);
					} else if (v instanceof CheckBox) {
						if (value instanceof Boolean) {
							((CheckBox) v).setChecked((Boolean) value);
						}
					} else if (v instanceof ImageView) {
						ImageLoader.getInstance().displayImage(str,
								(ImageView) v);
					}
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	static View findView(Context context, ViewGroup parent, Class cls) {
		View view = null;
		if (cls.isAnnotationPresent(ViewGroupInj.class)) {
			Annotation annotation = cls.getAnnotation(ViewGroupInj.class);
			if (annotation != null) {
				ViewGroupInj contentInj = (ViewGroupInj) annotation;
				int resId = contentInj.value();
				view = LayoutInflater.from(context).inflate(resId, null);
			}
		}
		return view;
	}

	private static View setContentView(Context context, Object bean) {
		// setContentView
		if (bean.getClass().isAnnotationPresent(ViewGroupInj.class)) {
			Annotation annotation = bean.getClass().getAnnotation(
					ViewGroupInj.class);
			if (annotation != null) {
				ViewGroupInj contentInj = (ViewGroupInj) annotation;
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

	private static String getString(Object value) {
		if (value instanceof String) {
			return (String) value;
		} else if (value instanceof Integer || value instanceof Long
				|| value instanceof Double || value instanceof Float) {
			return value + "";
		}
		return "";
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
				HashMap<String, String> params = new HashMap<String, String>();
				for (Field field : request.getClass().getDeclaredFields()) {
					Annotation annotation = field.getAnnotation(ViewInj.class);
					if (annotation == null) {
						continue;
					}
					ViewInj inj = (ViewInj) annotation;
					int resId = inj.value();
					View childView = findViewById(view, resId);
					if (childView == null) {
						continue;
					}
					try {
						if (childView instanceof TextView) {
							String value = ((TextView) childView).getText()
									.toString();
							String key = field.getName();
							params.put(key, value);
							field.set(request, value);
						} else if (childView instanceof CheckBox) {
							boolean value = ((CheckBox) childView).isChecked();
							String key = field.getName();
							params.put(key, value+"");
							field.set(request, value);
						} else if (childView instanceof ImageView) {
							Annotation anno = field.getAnnotation(ImageViewInj.class);
							ImageViewInj imageInj = (ImageViewInj) anno;
							String url = imageInj.value();
							if(childView instanceof Uploadable){
								ImageUploader.upload(url, ((Uploadable) childView).getFileUrl());
							}else{
								BitmapDrawable drawable = (BitmapDrawable) ((ImageView) childView).getDrawable();
								ImageUploader.upload(url, drawable.getBitmap());
							}
							
						}
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
				NetworkRequest.requestJson(request.onPostUrl(context),
						params, new Response.Listener<JSONObject>() {

							@Override
							public void onResponse(JSONObject obj) {
								request.onPostResponse(context, obj);
							}

						}, null);
			}
		});
	}

	private static void downloadEntity(final Context context, final View view,
			final MutableEntity<Downloadable> m) {
		final Class beanCls = m.getEntity().getClass();
		Downloadable downloadable = m.getEntity();
		Object params = downloadable.onDownloadParams();
		if(params == null){
			params = new HashMap<String, String>();
		}
		NetworkRequest.requestJson(downloadable.onDownLoadUrl(context),
				params, new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject json) {
						Gson gson = new Gson();
						Object obj = gson.fromJson(json.toString(), beanCls);
						if (obj != null) {
							if(obj instanceof Model){
								saveInDatabase((Entity) obj);
							}
							setContent(context, view, obj);
							if(obj instanceof Downloadable){
								m.setEntity((Downloadable) obj);
							}
						}
					}

				}, null);
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

	// private static View findViewById(Context context, int id){
	// return ((Activity) context).findViewById(id);
	// }

	private static View findViewById(View view, int id) {
		return view.findViewById(id);
	}

}
