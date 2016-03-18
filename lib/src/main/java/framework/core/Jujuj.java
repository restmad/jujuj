package framework.core;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.apache.commons.beanutils.BeanUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import framework.core.exception.NotInitiatedException;
import framework.inj.ActivityInj;
import framework.inj.GroupViewInj;
import framework.inj.Requestable;
import framework.inj.entity.Action;
import framework.inj.entity.Downloadable;
import framework.inj.entity.Follower;
import framework.inj.entity.Following;
import framework.inj.entity.Listable;
import framework.inj.entity.Loadable;
import framework.inj.entity.Multipleable;
import framework.inj.entity.MutableEntity;
import framework.inj.entity.Postable;
import framework.inj.entity.utility.PostButtonListenable;
import framework.inj.entity.utility.Validatable;
import framework.inj.impl.AbsListViewBinder;
import framework.inj.impl.CheckBoxBinder;
import framework.inj.impl.ImageViewBinder;
import framework.inj.impl.SpinnerBinder;
import framework.inj.impl.TextViewBinder;
import framework.inj.impl.ToggleButtonBinder;
import framework.inj.impl.ViewBinder;
import framework.inj.impl.WebViewBinder;
import framework.provider.AbsDataProvider;
import framework.provider.Listener;

//import java.lang.reflect.Field;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.lang.reflect.Type;

/**
 * Created by shinado on 15/8/28.
 * inject -> (loadEntity)* -> setContent
 * -> post(set on post)
 */
public class Jujuj {

    private static final String BINDING_CLASS_SUFFIX = "$$ViewBinder";
    private final Map<Class<?>, ViewInject> BINDERS = new LinkedHashMap<>();
    private static boolean debug = false;
    private final static String TAG = "Jujuj";
    private Configurations configurations;
    private ViewInjectHelper mInjectHelper;
    private static Jujuj instance;

    public static Jujuj getInstance() {
        if (instance == null) {
            instance = new Jujuj();
        }
        return instance;
    }

    private Jujuj() {
    }

    public void init(Configurations conf) {
        if (configurations != null) {
            Log.w(TAG, "jujuj has already bean initiated");
            return;
        }
        configurations = conf;

        addStandardViewInjectors(configurations);
    }

    private void addStandardViewInjectors(Configurations conf) {
        conf.binders.add(new ToggleButtonBinder());
        conf.binders.add(new ImageViewBinder(conf.imageProvider));
        conf.binders.add(new CheckBoxBinder());
        conf.binders.add(new AbsListViewBinder());
        conf.binders.add(new SpinnerBinder());
        conf.binders.add(new WebViewBinder());
        conf.binders.add(new TextViewBinder());
    }

    void checkInit() {
        if (configurations == null) {
            throw new NotInitiatedException("Please initiate jujuj by using init() before inject");
        }
    }

    /**
     * to request data
     */
    public void request(Context context, MutableEntity<? extends Downloadable> m) {
        loadEntity(context, null, m, m.getEntity(), m.getEntity().getClass(), context.getPackageName());
    }

    /**
     * Notice that Multipleable is implemented with reflection
     * inject an object with multiple requests
     * used for Activity
     */
    public void inject(Context context, Multipleable mtp) {
        inject(context, mtp, context.getPackageName());
    }

    /**
     * Notice that Multipleable is implemented with reflection
     * inject an object with multiple requests
     * for view
     */
    public void inject(Context context, View view, Multipleable mtp) {
        inject(context, view, mtp, context.getPackageName());
    }

    /**
     * inject a mutable entity
     * used for Activity
     */
    public void inject(Context context, MutableEntity<? extends Downloadable> m) {
        inject(context, m, context.getPackageName());
    }

    public boolean inject(Context context, View view, MutableEntity<? extends Downloadable> m) {
        return inject(context, view, m, context.getPackageName());
    }

    public void inject(Context context, Object bean) {
        inject(context, bean, context.getPackageName());
    }

    public void inject(Context context, View view, Object bean) {
        inject(context, view, bean, context.getPackageName());
    }

    public void inject(Context context, Multipleable mtp, String packageName) {
        if (mtp == null) {
            return;
        }

        View view = setContentView(context, mtp);
        inject(context, view, mtp, packageName);
    }

    public void inject(Context context, View view, Multipleable mtp, String packageName) {
        for (Object obj : mtp.getLoaders()) {
            if (obj instanceof MutableEntity) {
                inject(context, view, (MutableEntity) obj, packageName);
            } else {
                inject(context, view, obj, packageName);
            }
        }
    }

    public void inject(Context context, MutableEntity<? extends Downloadable> m, String packageName) {
        if (m == null || m.getEntity() == null) {
            return;
        }

        View view;
        if (m instanceof Loadable) {
            //when m is a Loadable
            view = setContentView(context, m);
        } else {
            view = setContentView(context, m.getEntity());
        }
        inject(context, view, m, packageName);
    }

    /**
     * inject a mutable entity
     * for view
     */
    public boolean inject(Context context, View view, MutableEntity<? extends Downloadable> m, String packageName) {
        checkInit();
        if (m == null || m.getEntity() == null) {
            return false;
        }

        Object bean = m.getEntity();
        if (bean instanceof Postable) {
            setDataPost(context, view, (Postable) bean, packageName);
        }

        if (m instanceof Loadable) {
            Loadable loadable = (Loadable) m;
            if (m.isStateStored()) {
                //notice here it's set by method
                //a loadable
                //TODO setContentByMethod
                //setContentByMethod(context, view, loadable);
                return true;
            } else {
                loadEntity(context, view, loadable, loadable, loadable.getEntity().getClass(), packageName);
                return true;
            }
        } else {
            /**
             * if the state is stored
             */
            if (m.isStateStored()) {
                setContent(context, view, bean, packageName);
                return true;
            }
            loadEntity(context, view, m, (Downloadable) bean, bean.getClass(), packageName);
        }

        setContent(context, view, bean, packageName);
        return false;
    }

    /**
     * inject a normal bean, for post
     * used for Activity
     */
    public void inject(Context context, Object bean, String packageName) {
        if (bean == null) {
            return;
        }

        View view = setContentView(context, bean);
        inject(context, view, bean, packageName);
    }

    /**
     * the difference with MutableEntity is the states in bean here won't be stored
     */
    public boolean inject(Context context, View view, Object bean, String packageName) {
        checkInit();
        if (bean == null) {
            return false;
        }

        if (bean instanceof Postable) {
            setDataPost(context, view, (Postable) bean, packageName);
        }

        if (bean instanceof Downloadable) {
            loadEntity(context, view, null, (Downloadable) bean, bean.getClass(), packageName);
            return true;
        }
        setContent(context, view, bean, packageName);
        return false;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
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

    void setDataPost(final Context context, final View view, final Postable request, final String packageName) {
        if (request instanceof PostButtonListenable) {
            ((PostButtonListenable) request).listen(new PostButtonListenable.Listener() {
                @Override
                public void onClick() {
                    postData(context, view, request, null, packageName);
                }
            });
            return;
        }

        int submitId = request.getSubmitButtonId();
        View button = view.findViewById(submitId);
        if (button == null) {
            return;
        }
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                long startTime = System.currentTimeMillis();
                postData(context, view, request, v, packageName);
                Log.d("JujujTime post", "" + (System.currentTimeMillis() - startTime));
            }

        });
    }

    @SuppressWarnings("unchecked")
    private void postData(final Context context, final View view, final Postable request, View button, String packageName) {
        ViewInject viewBinder = findViewInject(context, request.getClass());
        HashMap<String, String> params = viewBinder.getParams(view, request, mInjectHelper, packageName);
        if (params == null) {
            return;
        }

        postData(context, request, params, button, packageName);
    }

    void postData(Context context, final Requestable request, Map<String, String> params, View button, String packageName) {
        AbsDataProvider dataProvider = configurations.dataProvider;
        Type[] genType = request.getClass().getGenericInterfaces();
        Type[] typeArguments = ((ParameterizedType) genType[0]).getActualTypeArguments();
        Class entityClass = (Class) typeArguments[0];
        handlePost(context, request, dataProvider, button, params, entityClass, packageName);
        if (button != null) {
            button.setEnabled(false);
        }
    }

    /**
     * handle data in post method
     */
    @SuppressWarnings("unchecked")
    private void handlePost(final Context context, final Requestable request, final AbsDataProvider dataProvider,
                            final View button,
                            final Map<String, String> params, final Class target, final String packageName) {
        //notice the target here is set to null
        //so what?

        dataProvider.handleData(context, request.onPostUrl(context), params, target,
                new Listener.Response() {
                    @Override
                    public void onResponse(Object obj) {
                        if (obj == null) {
                            //get nothing, let supervisor handle it
                            AbsDataProvider supervisor = dataProvider.getSupervisor();
                            if (supervisor != null) {
                                handlePost(context, request, supervisor, button, params, target, packageName);
                            }
                        } else {
                            if (button != null) {
                                button.setEnabled(true);
                            }
                            request.onPostResponse(context, obj);
                        }
                    }
                },
                new Listener.Error() {
                    @Override
                    public void onError(String msg) {
                        if (button != null) {
                            button.setEnabled(true);
                        }
                        request.onError(context, msg);
                    }
                });
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> objToMap(Object obj) {
        if (obj == null) {
            return new HashMap<>();
        }

        if (obj instanceof HashMap) {
            return (HashMap<String, String>) obj;
        }

        Map<String, String> map = null;
        try {
            map = BeanUtils.describe(obj);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        return map;
    }

    /**
     * notice that m could be null.
     * When m is null, the state won't be stored
     *
     * @param target the target entity supposed to be loaded, could be either Downloadable or Loadable
     */
    void loadEntity(final Context context, final View view, final MutableEntity m, final Downloadable downloadable, Class target, String packageName) {
        Object downloadParams = downloadable.onDownloadParams();

        Map<String, String> params = objToMap(downloadParams);

        String uri = downloadable.onDownLoadUrl(context);

        AbsDataProvider dataProvider = configurations.dataProvider;
        handleLoad(dataProvider, context, view, m, downloadable, target, null, uri, params, packageName);
    }

    /**
     * @param m              could be a Loadable
     * @param downloadable to receive onError
     * @param target        the target entity the data should be
     * @param listable      the previous Listable, if any
     */
    private void handleLoad(final AbsDataProvider dataProvider, final Context context, final View view,
                            final MutableEntity m, final Downloadable downloadable, final Class target,
                            final Listable listable,
                            final String uri, final Map<String, String> params, final String packageName) {
        Log.d(TAG, "provider:" + dataProvider.getClass());

        dataProvider.handleData(context, uri, params, target,
                new Listener.Response<Object>() {
                    @Override
                    public void onResponse(Object obj) {
                        if (obj == null) {
                            //get nothing, let supervisor handle it
                            AbsDataProvider supervisor = dataProvider.getSupervisor();
                            if (supervisor != null) {
                                handleLoad(supervisor, context, view, m, downloadable, target, listable, uri, params, packageName);
                            }else {
                                //if the previous result is not null
                                //got something to handle
                                if (listable != null){
                                    handleDownloadObject(context, view, m, listable, downloadable, packageName);
                                }
                            }
                        } else {
                            if (obj instanceof Listable) {
                                Listable newListable = (Listable) obj;
                                if (listable != null){
                                    //add previous items
                                    newListable.getList().addAll(listable.getList());
                                }
                                AbsDataProvider supervisor = dataProvider.getSupervisor();
                                if (supervisor != null) {
                                    //go on
                                    handleLoad(supervisor, context, view, m, downloadable, target, newListable, uri, params, packageName);
                                }else{
                                    //finish
                                    handleDownloadObject(context, view, m, newListable, downloadable, packageName);
                                }
                            } else {
                                //not a Listable, return immediately
                                handleDownloadObject(context, view, m, obj, downloadable, packageName);
                            }
                        }
                    }
                },
                new Listener.Error() {
                    @Override
                    public void onError(String msg) {
                        Log.e("jujuj Error", msg);
                        downloadable.onError(context, msg);
                    }
                });
    }

    @SuppressWarnings("unchecked")
    private void handleDownloadObject(Context context, View view, MutableEntity m, Object obj, Downloadable downloadable, String packageName) {
        if (obj != null) {
            if (view != null) {
                if (m != null) {
                    m.setEntity(obj);
                    m.onStoring();
                    if (m.getNotifiable() != null) {
                        m.getNotifiable().onDownloadResponse();
                    }
                    if (m instanceof Loadable) {
                        setContent(context, view, m, packageName);
                    } else {
                        setContent(context, view, obj, packageName);
                    }
                } else {
                    setContent(context, view, obj, packageName);
                }
            }
            downloadable.onDownLoadResponse(context);
        }
    }

    /**
     * set content for view
     * using ViewInject that's been generated
     *
     * @param context context
     * @param view    view
     * @param target  the target object to be casted to the view
     */
    @SuppressWarnings("unchecked")
    public void setContent(Context context, View view, Object target, String packageName) {
        long startTime = System.currentTimeMillis();
        ViewInject viewInject = findViewInject(context, target.getClass());
        viewInject.setContent(view, target, mInjectHelper, packageName);
        Log.d("JujujTime", "setContent: " + target.getClass().getName() + ", " + (System.currentTimeMillis() - startTime));
    }

    /**
     * @param context that mInjectHelper relies on
     * @param cls     the target class of that to be casted to the view
     * @return the ViewInject created by JujujProcessor
     */
    private ViewInject findViewInject(Context context, Class<?> cls) {
        //new one when it is null or the context is not same
        if (mInjectHelper == null || !mInjectHelper.same(context)) {
            mInjectHelper = new ViewInjectHelper(context);
        }
        ViewInject viewBinder = BINDERS.get(cls);
        if (viewBinder != null) {
            if (debug) Log.d(TAG, "HIT: Cached in view binder map.");
            return viewBinder;
        }
        String clsName = cls.getName();

        try {
            Class<?> viewBindingClass = Class.forName(clsName + BINDING_CLASS_SUFFIX);
            viewBinder = (ViewInject) viewBindingClass.newInstance();
            if (debug) Log.d(TAG, "HIT: Loaded view binder class.");
        } catch (ClassNotFoundException e) {
            if (debug)
                Log.d(TAG, clsName + " not found. Trying superclass " + cls.getSuperclass().getName());
            viewBinder = findViewInject(context, cls.getSuperclass());
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        BINDERS.put(cls, viewBinder);
        return viewBinder;
    }

    /**
     * The generated class will implement this interface
     * as defined in JujujProcessor
     */
    public interface ViewInject<T> {

        void setContent(View view, T target, ViewInjectHelper helper, String packageName);

        HashMap<String, String> getParams(View view, T target, ViewInjectHelper helper, String packageName);
    }

    public class ViewInjectHelper {

        private Context context;

        public ViewInjectHelper(Context context) {
            this.context = context;
        }

        public boolean same(Context context) {
            return this.context == context;
        }

        /**
         * invoke by ViewInject
         *
         * @param v         the view
         * @param bean      the object of the target
         * @param fieldName the name of the field
         * @param value     the value of the field
         */
        public void setContent(View v, Object bean, String fieldName, Object value, String packageName) {
            if (value == null) {
                return;
            }

            if (value instanceof Action) {
                //TODO
                return;
            }
            for (ViewBinder injector : configurations.binders) {
                if (injector.setViewContent(context, v, bean, fieldName, value, packageName)) {
                    break;
                }
            }
        }

        /**
         * invoke by ViewInject
         */
        public void getParams(View childView, HashMap<String, String> params, Object request, String fieldName, String packageName) {
            try {
                for (ViewBinder injector : configurations.binders) {
                    String value = injector.addParams(childView, params, request, fieldName, packageName);
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
         *
         * @param id could be int or identifier
         */
        public View findViewById(View view, String id, String packageName) {
            int intId;
            try {
                intId = Integer.parseInt(id);
            } catch (NumberFormatException e) {
                intId = context.getResources().getIdentifier(id, "id", packageName);
            }

            return view.findViewById(intId);
        }

        /**
         * to inject a DependentInj
         */
        public void inject(View view, Loadable bean, String packageName) {
            Jujuj.this.inject(context, view, bean, packageName);
        }

        /**
         * to set content for a DependentInj
         */
        public void setContent(View view, Object target, String packageName) {
            Jujuj.this.setContent(context, view, target, packageName);
        }

        /**
         * to inject a ActionInj
         */
        @SuppressWarnings("unchecked")
        public void inject(View button, final Action action, final String packageName) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String, String> params;
                    Object obj = action.onDownloadParams();
                    if (obj instanceof HashMap) {
                        params = (Map<String, String>) obj;
                    } else {
                        params = objToMap(obj);
                    }
                    postData(context, action, params, v, packageName);
                }
            });

        }

    }

}
