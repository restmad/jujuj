package jujuj.internal;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.HashSet;
import java.util.Set;

import static javax.lang.model.element.Modifier.PUBLIC;

final class Generator {
    private ClassName THIS;
    private static final ClassName VIEW_INJECT =
            ClassName.get("framework.core", "Jujuj", "ViewInject");
    private static final ClassName HELPER =
            ClassName.get("framework.core", "Jujuj", "ViewInjectHelper");
    private static final ClassName VIEW = ClassName.get("android.view", "View");
    private static final ClassName LOADABLE = ClassName.get("framework.inj.entity", "Loadable");
    private static final ClassName ACTION = ClassName.get("framework.inj.entity", "Action");
    private static final ClassName HASHMAP = ClassName.get("java.util", "HashMap");
    private static final ClassName STRING = ClassName.get("java.lang", "String");

    private final Set<AnnotationItem> annotations = new HashSet<>();
    private final String classPackage;
    private final String className;

    Generator(String classPackage, String className, String targetClass) {
        this.classPackage = classPackage;
        THIS = ClassName.get(classPackage, targetClass);
        this.className = className;
    }

    /**
     *
     */
    void addAnnotation(AnnotationItem anno) {
        annotations.add(anno);
    }


    JavaFile brewJava() {
        TypeSpec.Builder result = TypeSpec.classBuilder(className)
                .addModifiers(PUBLIC);

        result.addSuperinterface(ParameterizedTypeName.get(VIEW_INJECT, THIS));

        result.addMethod(setContentMethod());
        result.addMethod(getParamsMethod());

        return JavaFile.builder(classPackage, result.build())
                .addFileComment("Generated code from Jujuj. Do not modify!")
                .build();
    }

    private MethodSpec setContentMethod() {
        MethodSpec.Builder result = MethodSpec.methodBuilder("setContent")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addParameter(VIEW, "view")
                .addParameter(THIS, "target")
                .addParameter(HELPER, "helper")
                .addParameter(STRING, "packageName");

        if (!annotations.isEmpty()) {
            // Loop over each view bindings and emit it.
            for (AnnotationItem anno : annotations) {
                addContentBindings(result, anno);
            }
        }

        return result.build();
    }

    private MethodSpec getParamsMethod() {
        MethodSpec.Builder result = MethodSpec.methodBuilder("getParams")
                .returns(HASHMAP)
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addParameter(VIEW, "view")
                .addParameter(THIS, "target")
                .addParameter(HELPER, "helper")
                .addParameter(STRING, "packageName");

        result.addStatement("HashMap<String, String> params = new HashMap<>()");
        if (!annotations.isEmpty()) {
            for (AnnotationItem anno : annotations) {
                addParamsBindings(result, anno);
            }
        }
        result.addStatement("return params");

        return result.build();
    }

    //finish setContent method
    private void addContentBindings(MethodSpec.Builder result, AnnotationItem anno) {
        if (anno.getType() == AnnotationItem.Type.DependentInj) {
            handleDependentInj(result, anno);
        } else if (anno.getType() == AnnotationItem.Type.ActionInj){
            handleActionInj(result, anno);
        } else {
            String nameOfView = anno.getElementName() + "View";
            //field or method
            String suffix = anno.getType() == AnnotationItem.Type.ViewInj ? "" : "()";
            result.addStatement("$T " + nameOfView + " = helper.findViewById(view, $S, packageName)", VIEW, anno.getValue());
            result.addStatement("helper.setContent(" + nameOfView + ", target, $S, target." + anno.getElementName() + suffix + ", packageName)", anno.getElementName());
        }
    }

    private void handleDependentInj(MethodSpec.Builder result, AnnotationItem anno){
        String elementName = anno.getElementName();
        result.addStatement("$T " + elementName + " = target." + elementName, LOADABLE);
        //if state stored
        result.beginControlFlow("if("+elementName + ".isStateStored())");
            result.addStatement("helper.setContent(view, " + elementName+ ", packageName)");
        //else
        result.nextControlFlow("else");
            result.addStatement("helper.inject(view, " + elementName+ ", packageName)");
        result.endControlFlow();
    }

    private void handleActionInj(MethodSpec.Builder result, AnnotationItem anno){
        String nameOfView = anno.getElementName() + "View";
        result.addStatement("$T " + nameOfView + " = helper.findViewById(view, $S, packageName)", VIEW, anno.getValue());

        String elementName = anno.getElementName();
        result.addStatement("helper.inject("+nameOfView+", target." + elementName+ ")");

        result.addStatement("helper.setContent(" + nameOfView + ", target, $S, target." + anno.getElementName() + ".getValue(), packageName)", anno.getElementName());
    }

    //finish addParams method
    private void addParamsBindings(MethodSpec.Builder result, AnnotationItem anno) {
        String nameOfView = anno.getElementName() + "View";
        result.addStatement("$T " + nameOfView + " = helper.findViewById(view, $S, packageName)", VIEW, anno.getValue());
        result.addStatement("helper.getParams(" + nameOfView + ", params, target, $S, packageName)", anno.getElementName());
    }

}
