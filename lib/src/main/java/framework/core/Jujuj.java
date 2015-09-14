package framework.core;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.apache.commons.beanutils.BeanUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import framework.core.exception.NotInitiatedException;
import framework.inj.ActivityInj;
import framework.inj.GroupViewInj;
import framework.inj.ViewInj;
import framework.inj.entity.Downloadable;
import framework.inj.entity.Following;
import framework.inj.entity.Loadable;
import framework.inj.entity.Multipleable;
import framework.inj.entity.MutableEntity;
import framework.inj.entity.Postable;
import framework.inj.entity.utility.Validatable;
import framework.inj.exception.ViewNotFoundException;
import framework.inj.impl.AbsListViewInjector;
import framework.inj.impl.CheckBoxInjector;
import framework.inj.impl.ImageViewInjector;
import framework.inj.impl.SpinnerInjector;
import framework.inj.impl.TextViewInjector;
import framework.inj.impl.ViewInjector;
import framework.inj.ViewValueInj;
import framework.inj.impl.WebViewInjector;
import framework.provider.AbsDataProvider;
import framework.provider.Listener;

/**
 * Created by shinado on 15/8/28.
 * inject -> (loadEntity)* -> setContent
 *   -> post(set on post)
 */
public class Jujuj {

    private final String TAG = "Jujuj";
    private Configurations configurations;
    private ArrayList<ViewInjector> injectors;
    private static Jujuj instance;

    public static Jujuj getInstance(){
        if(instance == null){
            instance = new Jujuj();
        }
        return instance;
    }

    private Jujuj(){
    }

    public void init(Configurations conf){
        if(configurations != null){
            Log.w(TAG, "jujuj has already bean initiated");
            return;
        }
        configurations = conf;

        injectors = new ArrayList<>();
        injectors.add(new ImageViewInjector(configurations.imageProvider));
        injectors.add(new CheckBoxInjector());
        injectors.add(new AbsListViewInjector());
        injectors.add(new SpinnerInjector());
        injectors.add(new WebViewInjector());
        injectors.add(new TextViewInjector());
    }

    void checkInit(){
        if(configurations == null){
            throw new NotInitiatedException("Please initiate jujuj by using init() before inject");
        }
    }

    /**
     * inject an object with multiple requests
     * used for Activity
     */
    public void inject(Context context, Multipleable mtp){
        if (mtp == null) {
            return;
        }

        View view = setContentView(context, mtp);
        inject(context, view, mtp);
    }

    /**
     * inject an object with multiple requests
     * for view
     */
    public void inject(Context context, View view, Multipleable mtp) {
        checkInit();
        //set request and content
        if (mtp instanceof Postable) {
            setDataPost(context, view, (Postable) mtp);
        }
//        else if (mtp instanceof Getable){
//            setDataPost(context, view, (Getable) mtp);
//        }
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * inject a mutable entity
     * used for Activity
     */
    public  void inject(Context context, MutableEntity m) {
        if (m == null || m.getEntity() == null) {
            return;
        }

        View view = null;
        if(m instanceof Loadable){
            //when m is a Loadable
            view = setContentView(context, m);
        }else{
            view = setContentView(context, m.getEntity());
        }
        inject(context, view, m);
    }

    /**
     * inject a mutable entity
     * for view
     */
    public boolean inject(Context context, View view, MutableEntity m) {
        checkInit();
        if (m == null || m.getEntity() == null) {
            return false;
        }

        Object bean = m.getEntity();
        if (bean instanceof Postable) {
            setDataPost(context, view, (Postable) bean);
        }

        if(m instanceof Loadable){
            Loadable loadable = (Loadable) m;
            if(m.isStateStored()){
                //notice here it's set by method
                //a loadable
                setContentByMethod(context, view, loadable);
                return true;
            }else{
                loadEntity(context, view, loadable, loadable, loadable.getEntity().getClass());
                return true;
            }
        }else{
            /**
             * if the state is stored
             */
            if (m.isStateStored()){
                setContent(context, view, bean);
                return true;
            }
            if (bean instanceof Downloadable){
                loadEntity(context, view, m, (Downloadable) bean, bean.getClass());
                return true;
            }
        }

        setContent(context, view, bean);
        return false;
    }

    /**
     * inject a normal bean, for post
     * used for Activity
     */
    public void inject(Context context, Object bean){
        if (bean == null) {
            return;
        }

        View view = setContentView(context, bean);
        inject(context, view, bean);
    }

    /**
     * inject a normal bean, for post mostly
     * used for view
     */
    public  boolean inject(Context context, View view, Object bean) {
        checkInit();
        if (bean == null) {
            return false;
        }

        if (bean instanceof Postable) {
            setDataPost(context, view, (Postable) bean);
        }
        setContent(context, view, bean);
        return false;
    }

    /**
     *
     * @param context
     * @param view
     * @param bean it must defines
     */
    public void setContent(Context context, View view, Object bean) {
        setContentByField(context, view, bean);
        setContentByMethod(context, view ,bean);
    }

    void setContentByField(Context context, View view, Object bean){
        for (Field field : bean.getClass().getDeclaredFields()) {
            Annotation annotation = field.getAnnotation(ViewInj.class);
            if (annotation == null) {
                continue;
            }
            ViewInj inj = (ViewInj) annotation;
            int resId = inj.value();
            View v = findViewById(view, resId);
            if (v == null) {
                throw new ViewNotFoundException("Can't find this View for method:"+field.getName());
            }
            for(ViewInjector injector:injectors){
                if(injector.setContent(context, v, bean, field)){
                    break;
                }
            }
        }
    }

    void setContentByMethod(Context context, View view, Object bean){
        for (Method method : bean.getClass().getDeclaredMethods()) {
            Annotation annotation = method.getAnnotation(ViewValueInj.class);
            if (annotation == null) {
                continue;
            }
            ViewValueInj inj = (ViewValueInj) annotation;
            int resId = inj.value();
            View v = findViewById(view, resId);
            if (v == null) {
                throw new ViewNotFoundException("Can't find this View for method:"+method.getName());
            }
            for(ViewInjector injector:injectors){
                if(injector.setContent(context, v, bean, method)){
                    break;
                }
            }
        }
    }

    /**
     * save a tag for this view
     */
    View findViewById(View view, int id) {
        View v = null;
        //TODO Build.VERSION below 4.0
        Object tag = view.getTag(id);
        if(tag != null && tag instanceof View){
            v = (View) tag;
        }else{
            v = view.findViewById(id);
            view.setTag(id, v);
        }
        return v;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public View findView(Context context, ViewGroup parent, Class cls) {
        View view = null;
        if (cls.isAnnotationPresent(GroupViewInj.class)) {
            Annotation annotation = cls.getAnnotation(GroupViewInj.class);
            if (annotation != null) {
                GroupViewInj contentInj = (GroupViewInj) annotation;
                int resId = contentInj.value();
                view = LayoutInflater.from(context).inflate(resId, parent, false);
            }
        }
        return view;
    }

    /**
     * setContentView for Activity
     */
    private View setContentView(Context context, Object bean) {
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

    void setDataPost(final Context context, final View view, final Postable request) {
        int submitId = request.getSubmitButtonId();
        Button button = (Button) findViewById(view, submitId);
        if(button == null){
            return;
        }
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                HashMap<String, String> params = getParams(context, view, request);
                if(params == null){
                    return;
                }

                final AbsDataProvider dataProvider = configurations.dataProvider;
                handleData(dataProvider, params);
            }

            /**
             * handle data with RoC
             * @param dataProvider
             */
            private void handleData(final AbsDataProvider dataProvider, final HashMap<String, String> params){
                //notice the target here is set to null
                //
                dataProvider.handleData(request.onPostUrl(context), params, null,
                        new Listener.Response<Postable>() {
                            @Override
                            public void onResponse(Postable obj) {
                                if (obj == null) {
                                    //get nothing, let supervisor handle it
                                    AbsDataProvider supervisor = dataProvider.getSupervisor();
                                    if(supervisor != null){
                                        handleData(supervisor, params);
                                    }
                                } else {
                                    request.onPostResponse(context, obj);

                                    if(request instanceof Following) {
                                        setFollowing(context, (Following) request);
                                    }
                                }
                            }
                        },
                        new Listener.Error() {
                            @Override
                            public void onError(String msg) {
                                request.onError(context, msg);
                            }
                        });
            }
        });
    }

    /**
     * set a following
     */
    private void setFollowing(Context context, Following following){
            Downloadable dlb = null;
            try {
                dlb = (Downloadable) following.getClass().getGenericSuperclass().getClass().newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if(dlb != null){
                inject(context, new MutableEntity(dlb));
            }
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
                                if(request instanceof Postable){
                                    ((Postable) request).onError(context, validate);
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

    private Map<String, String> objToMap(Object obj){
        if(obj == null){
            return new HashMap<String, String>();
        }

        if(obj instanceof HashMap){
            return (HashMap<String, String>) obj;
        }

        Map<String, String> map = null;
        try {
            map = BeanUtils.describe(obj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return map;
    }

    /**
     * @param target the target entity supposed to be loaded, could be either Downloadable or Loadable
     */
    void loadEntity(final Context context, final View view, final MutableEntity m, final Downloadable downloadable, Class target) {

        Object downloadParams = downloadable.onDownloadParams();

        Map<String, String> params = objToMap(downloadParams);

        String uri = downloadable.onDownLoadUrl(context);

        AbsDataProvider dataProvider = configurations.dataProvider;
        handleData(dataProvider, context, view, m, downloadable, target, uri, params);
    }

    /**
     *
     * @param m could be a Loadable
     * @param downloadable to receive onError
     * @param target the target entity the data should be
     */
    private void handleData(final AbsDataProvider dataProvider, final Context context, final View view,
                            final MutableEntity m, final Downloadable downloadable, final Class target,
                            final String uri, final Map<String, String> params){
        Log.d(TAG, "provider:"+dataProvider.getClass());
        dataProvider.handleData(uri, params, target,
                new Listener.Response<Object>() {
                    @Override
                    public void onResponse(Object obj) {
                        if (obj == null) {
                            //get nothing, let supervisor handle it
                            AbsDataProvider supervisor = dataProvider.getSupervisor();
                            if(supervisor != null){
                                handleData(supervisor, context, view, m, downloadable, target, uri, params);
                            }
                        } else {
                            handleDownloadObject(context, view, m, obj, downloadable);

                        }
                    }
                },
                new Listener.Error() {
                    @Override
                    public void onError(String msg) {
                        downloadable.onError(context, msg);
                    }
                });
    }

    private void handleDownloadObject(Context context, View view, MutableEntity m, Object obj, Downloadable downloadable){
        if (obj != null) {
            //TODO save in local?
//            if(obj instanceof Entity){
//                saveInDatabase((Entity) obj);
//            }

            //TODO what about more
//            if(obj instanceof Moreable){
//                //clone params etc
//                Object original = (Object) m.getEntity();
//                if(original != null){
//                    ((Moreable)obj).loadMore(original);
//                }
//                //set content
////				setContent(context, view, obj, m);
//            }else{
//            }

//			if(obj instanceof Downloadable){
            m.setEntity(obj);
            m.onStoring();
            downloadable.onDownLoadResponse(context);
            if(m.getNotifiable() != null){
                m.getNotifiable().onDownloadResponse();
            }
            //
            if(m instanceof Loadable){
                setContent(context, view, m);
            }else{
                setContent(context, view, obj);
            }
//			}
        }
    }

}
