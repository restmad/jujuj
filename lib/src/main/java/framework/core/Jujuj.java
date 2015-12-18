package framework.core;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.apache.commons.beanutils.BeanUtils;

import java.lang.annotation.Annotation;
//import java.lang.reflect.Field;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.lang.reflect.Type;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import framework.core.exception.NotInitiatedException;
import framework.inj.ActivityInj;
import framework.inj.GroupViewInj;
import framework.inj.entity.Downloadable;
import framework.inj.entity.Following;
import framework.inj.entity.Loadable;
import framework.inj.entity.Multipleable;
import framework.inj.entity.MutableEntity;
import framework.inj.entity.Postable;
import framework.inj.entity.utility.PostButtonListenable;
import framework.inj.entity.utility.Validatable;
import framework.inj.impl.AbsListViewInjector;
import framework.inj.impl.CheckBoxInjector;
import framework.inj.impl.ImageViewInjector;
import framework.inj.impl.SpinnerInjector;
import framework.inj.impl.TextViewInjector;
import framework.inj.impl.ViewInjector;
import framework.inj.impl.WebViewInjector;
import framework.provider.AbsDataProvider;
import framework.provider.Listener;

/**
 * Created by shinado on 15/8/28.
 * inject -> (loadEntity)* -> setContent
 *   -> post(set on post)
 */
public class Jujuj {

    private final Map<Class<?>, ViewInject> BINDERS = new LinkedHashMap<>();
    private final int TAG_FOR_LISTENER = -0xeeee;
    private static boolean debug = false;
    private final static String TAG = "Jujuj";
    private Configurations configurations;
    private ArrayList<ViewInjector> injectors;
    private ViewInjectHelper mInjectHelper;
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
     * Notice that Multipleable is implemented with reflection
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
     * Notice that Multipleable is implemented with reflection
     * inject an object with multiple requests
     * for view
     */
    public void inject(Context context, View view, Multipleable mtp) {
        checkInit();
        //set request and content
        if (mtp instanceof Postable) {
            setDataPost(context, view, (Postable) mtp);
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * inject a mutable entity
     * used for Activity
     */
    public void inject(Context context, MutableEntity m) {
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
        generateViewInject(context, view, m.getEntity());
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
                //TODO setContentByMethod
                //setContentByMethod(context, view, loadable);
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
        generateViewInject(context, view, bean);
        inject(context, view, bean);
    }

    /**
     * the difference with MutableEntity is the states in bean here won't be stored
     */
    public  boolean inject(Context context, View view, Object bean) {
        checkInit();
        if (bean == null) {
            return false;
        }

        if (bean instanceof Postable) {
            setDataPost(context, view, (Postable) bean);
        }

        if (bean instanceof Downloadable){
            loadEntity(context, view, null, (Downloadable) bean, bean.getClass());
            return true;
        }
        setContent(context, view, bean);
        return false;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public View findViewForGroup(Context context, ViewGroup parent, Class cls) {
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
        if (request instanceof PostButtonListenable){
            ((PostButtonListenable) request).listen(new PostButtonListenable.Listener() {
                @Override
                public void onClick() {
                    postData(context, view, request);
                }
            });
            return;
        }

        int submitId = request.getSubmitButtonId();
        View button = view.findViewById(submitId);
        if(button == null){
            return;
        }
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                postData(context, view, request);
            }

        });
    }

    private void postData(final Context context, final View view, final Postable request){
        ViewInject viewBinder = findViewInject(context, request.getClass());
        HashMap<String, String> params = viewBinder.getParams(view, request, mInjectHelper);
        if(params == null){
            return;
        }

        AbsDataProvider dataProvider = configurations.dataProvider;
        Type typeOfGeneric = request.getClass().getGenericSuperclass();
//        Type typeOfGeneric = ((ParameterizedType) type).getActualTypeArguments()[0];
        Class classOfGeneric;
        if (typeOfGeneric != null){
            classOfGeneric = typeOfGeneric.getClass();
            //not specified
            if (classOfGeneric == Class.class){
                classOfGeneric = request.getClass();
            }
        }else{
            classOfGeneric = request.getClass();
        }
        handlePost(context, request, dataProvider, params, classOfGeneric);
    }

    /**
     * handle data in post method
     */
    private void handlePost(final Context context, final Postable request, final AbsDataProvider dataProvider,
                            final HashMap<String, String> params, final Class target){
        //notice the target here is set to null
        //so what?
        dataProvider.handleData(context, request.onPostUrl(context), params, target,
                new Listener.Response<Postable>() {
                    @Override
                    public void onResponse(Postable obj) {
                        if (obj == null) {
                            //get nothing, let supervisor handle it
                            AbsDataProvider supervisor = dataProvider.getSupervisor();
                            if (supervisor != null) {
                                handlePost(context, request, supervisor, params, target);
                            }
                        } else {
                            request.onPostResponse(context, obj);

                            if (request instanceof Following) {
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
     * notice that m could be null.
     * When m is null, the state won't be stored
     * @param target the target entity supposed to be loaded, could be either Downloadable or Loadable
     */
    void loadEntity(final Context context, final View view, final MutableEntity m, final Downloadable downloadable, Class target) {

        Object downloadParams = downloadable.onDownloadParams();

        Map<String, String> params = objToMap(downloadParams);

        String uri = downloadable.onDownLoadUrl(context);

        AbsDataProvider dataProvider = configurations.dataProvider;
        handleLoad(dataProvider, context, view, m, downloadable, target, uri, params);
    }

    /**
     *
     * @param m could be a Loadable
     * @param downloadable to receive onError
     * @param target the target entity the data should be
     */
    private void handleLoad(final AbsDataProvider dataProvider, final Context context, final View view,
                            final MutableEntity m, final Downloadable downloadable, final Class target,
                            final String uri, final Map<String, String> params){
        Log.d(TAG, "provider:"+dataProvider.getClass());
        dataProvider.handleData(context, uri, params, target,
                new Listener.Response<Object>() {
                    @Override
                    public void onResponse(Object obj) {
                        if (obj == null) {
                            //get nothing, let supervisor handle it
                            AbsDataProvider supervisor = dataProvider.getSupervisor();
                            if (supervisor != null) {
                                handleLoad(supervisor, context, view, m, downloadable, target, uri, params);
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
            //TODO what about more

            if(m != null){
                m.setEntity(obj);
                m.onStoring();
                if(m.getNotifiable() != null){
                    m.getNotifiable().onDownloadResponse();
                }
                if(m instanceof Loadable){
                    setContent(context, view, m);
                }else{
                    setContent(context, view, obj);
                }
            }else{
                setContent(context, view, obj);
            }
            downloadable.onDownLoadResponse(context);
        }
    }

    /**
     * set content for view
     * using ViewInject that's been generated
     * @param context  context
     * @param view      view
     * @param target   the target object to be casted to the view
     */
    public void setContent(Context context, View view, Object target){
        ViewInject viewInject = findViewInject(context, target.getClass());
        viewInject.setContent(view, target, mInjectHelper);
    }

    /**
     * the entrance for generating ViewInject class
     *
     * @param context    you know
     * @param view        you know
     * @param target     the target object to be casted to the view
     */
    private void generateViewInject(Context context, View view, Object target) {
        Class<?> targetClass =  target.getClass();
        try {
            if (debug) Log.d(TAG, "Looking up view inject for " + targetClass.getName());
            ViewInject viewBinder = findViewInject(context, targetClass);
            if (viewBinder != null) {
                viewBinder.setContent(view, target, mInjectHelper);
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to bind views for " + targetClass.getName(), e);
        }
    }

    /**
     * @param context that mInjectHelper relies on
     * @param cls the target class of that to be casted to the view
     * @return the ViewInject created by JujujProcessor
     */
    private ViewInject findViewInject(Context context, Class<?> cls){
        //new one when it is null or the context is not same
        if (mInjectHelper == null || !mInjectHelper.same(context)){
            mInjectHelper = new ViewInjectHelper(context);
        }
        ViewInject viewBinder = BINDERS.get(cls);
        if (viewBinder != null) {
            if (debug) Log.d(TAG, "HIT: Cached in view binder map.");
            return viewBinder;
        }
        String clsName = cls.getName();

        try {
            Class<?> viewBindingClass = Class.forName(clsName + "$$ViewBinder");
            viewBinder = (ViewInject) viewBindingClass.newInstance();
            if (debug) Log.d(TAG, "HIT: Loaded view binder class.");
        } catch (ClassNotFoundException e) {
            if (debug) Log.d(TAG, "Not found. Trying superclass " + cls.getSuperclass().getName());
            viewBinder = findViewInject(context, cls.getSuperclass());
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        BINDERS.put(cls, viewBinder);
        return viewBinder;
    }

    /**
     * The generated class will implement this interface
     * as defined in JujujProcessor
     */
    public interface ViewInject<T>{
        public void setContent(View view, T target, ViewInjectHelper helper);
        public HashMap<String, String> getParams(View view, T target, ViewInjectHelper helper);
    }


    public class ViewInjectHelper{

        private Context context;

        public ViewInjectHelper(Context context){
            this.context = context;
        }

        public boolean same(Context context){
            return  context == context;
        }

        /**
         * invoke by ViewInject
         * @param v           the view
         * @param bean       the object of the target
         * @param fieldName the name of the field
         * @param value      the value of the field
         */
        public void setContent(View v, Object bean, String fieldName, Object value){
            for (ViewInjector injector : injectors) {
                if (injector.setViewContent(context, v, bean, fieldName, value)) {
                    break;
                }
            }
        }

        /**
         * invoke by ViewInject
         */
        public void getParams(View childView, HashMap<String, String> params, Object request, String fieldName){
            try {
                for (ViewInjector injector : injectors) {
                    String value = injector.addParams(childView, params, request, fieldName);
                    if (value != null) {
                        if (request instanceof Validatable) {
                            String validate = ((Validatable) request).validate(fieldName, value);
                            if (validate != null) {
                                //error!
                                if (request instanceof Postable) {
                                    ((Postable) request).onError(context, validate);
                                }
                                return;
                            }
                        }
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * invoke by ViewInject
         * @param id could be int or identifier
         */
        public View findViewById(View view, String id) {
            int intId;
            try {
                intId = Integer.parseInt(id);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                intId = context.getResources().getIdentifier(id , "id", context.getPackageName());
            }
            return view.findViewById(intId);
        }

    }
}
