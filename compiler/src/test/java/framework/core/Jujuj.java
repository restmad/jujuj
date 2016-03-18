package framework.core;

import android.content.Context;
import android.view.View;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * nothing but for compiler to finish testing
 */
public class Jujuj {

    private ViewInjectHelper mInjectHelper;
    private final Map<Class<?>, ViewInject> BINDERS = new LinkedHashMap<>();

    public interface ViewInject<T>{
        void setContent(View view, T target, ViewInjectHelper helper);
        HashMap<String, String> getParams(View view, T target, ViewInjectHelper helper);
    }

    /**
     * the entrance for generating ViewInject class
     *
     * @param context    you know
     * @param view        you know
     * @param target     the target object to be casted to the view
     */
    public void generateViewInject(Context context, View view, Object target) {
        Class<?> targetClass =  target.getClass();
        try {
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
            return viewBinder;
        }
        String clsName = cls.getName();

        try {
            Class<?> viewBindingClass = Class.forName(clsName + "$$ViewBinder");
            viewBinder = (ViewInject) viewBindingClass.newInstance();
        } catch (ClassNotFoundException e) {
            viewBinder = findViewInject(context, cls.getSuperclass());
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        BINDERS.put(cls, viewBinder);
        return viewBinder;
    }

    public class ViewInjectHelper{

        private Context context;

        public ViewInjectHelper(Context context){
            this.context = context;
        }

        public boolean same(Context context){
            return  this.context == context;
        }

        /**
         * invoke by ViewInject
         * @param v           the view
         * @param bean       the object of the target
         * @param fieldName the name of the field
         * @param value      the value of the field
         */
        public void setContent(View v, Object bean, String fieldName, Object value){
        }

        /**
         * invoke by ViewInject
         */
        public void getParams(View childView, HashMap<String, String> params, Object target, String fieldName){
        }

        /**
         * invoke by ViewInject
         * @param id could be int or identifier
         */
        public View findViewById(View view, String id) {
            return null;
        }

    }
}
