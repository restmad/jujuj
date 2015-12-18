package jujuj.internal;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import static javax.lang.model.element.Modifier.PUBLIC;

final class BindingClass {
    private ClassName THIS ;
    private static final ClassName VIEW_INJECT =
            ClassName.get("framework.core", "Jujuj", "ViewInject");
    private static final ClassName HELPER =
            ClassName.get("framework.core", "Jujuj", "ViewInjectHelper");
    private static final ClassName VIEW = ClassName.get("android.view", "View");
    private static final ClassName HASHMAP = ClassName.get("java.util", "HashMap");

    private final Map<String, ViewBindings> viewIdMap = new LinkedHashMap<>();
    private final String classPackage;
    private final String className;

    BindingClass(String classPackage, String className, String targetClass) {
        this.classPackage = classPackage;
        THIS = ClassName.get(classPackage, targetClass);
        this.className = className;
    }

    void addField(String id, FieldViewBinding binding) {
        getOrCreateViewBindings(id).addFieldBinding(binding);
    }

    ViewBindings getViewBinding(String id) {
        return viewIdMap.get(id);
    }

    private ViewBindings getOrCreateViewBindings(String id) {
        ViewBindings viewId = viewIdMap.get(id);
        if (viewId == null) {
            viewId = new ViewBindings(id);
            viewIdMap.put(id, viewId);
        }
        return viewId;
    }

    JavaFile brewJava() {
        TypeSpec.Builder result = TypeSpec.classBuilder(className)
                .addModifiers(PUBLIC);

        result.addSuperinterface(ParameterizedTypeName.get(VIEW_INJECT, THIS));

        result.addMethod(createBindMethod());
        result.addMethod(createParamsMethod());

        return JavaFile.builder(classPackage, result.build())
                .addFileComment("Generated code from Jujuj. Do not modify!")
                .build();
    }

    private MethodSpec createBindMethod() {
        MethodSpec.Builder result = MethodSpec.methodBuilder("setContent")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addParameter(VIEW, "view")
                .addParameter(THIS, "target")
                .addParameter(HELPER, "helper");

        if (!viewIdMap.isEmpty()) {
            // Loop over each view bindings and emit it.
            for (ViewBindings bindings : viewIdMap.values()) {
                addViewBindings(result, bindings);
            }
        }

        return result.build();
    }

    private MethodSpec createParamsMethod(){
        MethodSpec.Builder result = MethodSpec.methodBuilder("getParams")
                .returns(HASHMAP)
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addParameter(VIEW, "view")
                .addParameter(THIS, "target")
                .addParameter(HELPER, "helper");

        result.addStatement("HashMap<String, String> params = new HashMap<>()");
        if (!viewIdMap.isEmpty()) {
            for (ViewBindings bindings : viewIdMap.values()) {
                addParamsBindings(result, bindings);
            }
        }
        result.addStatement("return params");

        return result.build();
    }

    private void addViewBindings(MethodSpec.Builder result, ViewBindings bindings) {
        addFieldBindings(result, bindings);
        addMethodBindings(result, bindings);
    }

    private void addFieldBindings(MethodSpec.Builder result, ViewBindings bindings) {
        Collection<FieldViewBinding> fieldBindings = bindings.getFieldBindings();
        for (FieldViewBinding fieldBinding : fieldBindings) {
            String nameOfView = fieldBinding.getName() + "View";
            //setContent(View v, Object bean, String fieldName, Object value){
            result.addStatement("$T " + nameOfView + " = helper.findViewById(view, $S)", VIEW, bindings.getId());
            result.addStatement("helper.setContent(" + nameOfView + ", target, $S, target.$L)", fieldBinding.getName(), fieldBinding.getName());
        }
    }

    private void addParamsBindings(MethodSpec.Builder result, ViewBindings bindings) {
        Collection<FieldViewBinding> fieldBindings = bindings.getFieldBindings();
        //TODO MutableEntity
        //TODO
        for (FieldViewBinding fieldBinding : fieldBindings) {
            result.addStatement("params.put($S, target.$L)", fieldBinding.getName(), fieldBinding.getName());
        }
    }

    //TODO ViewValueInj
    private void addMethodBindings(MethodSpec.Builder result, ViewBindings bindings) {
    }

}
