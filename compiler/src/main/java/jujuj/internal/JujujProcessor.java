package jujuj.internal;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import framework.inj.Constants;
import framework.inj.ViewInj;

import static javax.lang.model.element.ElementKind.INTERFACE;
import static javax.tools.Diagnostic.Kind.ERROR;

/**
 * Created by Administrator on 2015/12/15.
 */
public class JujujProcessor extends AbstractProcessor{

    private static final String BINDING_CLASS_SUFFIX = "$$ViewBinder";
    private static final String NULLABLE_ANNOTATION_NAME = "Nullable";
    private Elements elementUtils;
    private Filer filer;

    @Override public synchronized void init(ProcessingEnvironment env) {
        super.init(env);

        elementUtils = env.getElementUtils();
        filer = env.getFiler();
    }

    @Override public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(ViewInj.class.getCanonicalName());
        return types;
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<TypeElement, BindingClass> targetClassMap = findAndParseTargets(roundEnv);

        for (Map.Entry<TypeElement, BindingClass> entry : targetClassMap.entrySet()) {
            BindingClass bindingClass = entry.getValue();

            try {
                bindingClass.brewJava().writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }


    private Map<TypeElement, BindingClass> findAndParseTargets(RoundEnvironment env) {
        Map<TypeElement, BindingClass> targetClassMap = new LinkedHashMap<>();
        Set<String> erasedTargetNames = new LinkedHashSet<>();

        // Process each @ViewInj element.
        for (Element element : env.getElementsAnnotatedWith(ViewInj.class)) {
            try {
                parseBindOne(element, targetClassMap, erasedTargetNames);
            } catch (Exception e) {

            }
        }

        return targetClassMap;
    }


    private void parseBindOne(Element element, Map<TypeElement, BindingClass> targetClassMap,
                              Set<String> erasedTargetNames) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        // Verify that the target type extends from View.
        TypeMirror elementType = element.asType();
        if (elementType.getKind() == TypeKind.TYPEVAR) {
            TypeVariable typeVariable = (TypeVariable) elementType;
            elementType = typeVariable.getUpperBound();
        }

        String strId = "";
        int id = element.getAnnotation(ViewInj.class).value();
        if (id == Constants.DEFAULT){
            //no id set
            //as default
            strId = enclosingElement.getSimpleName().toString();
        }else {
            strId = id+"";
        }

        BindingClass bindingClass = targetClassMap.get(enclosingElement);
        if (bindingClass != null) {
            ViewBindings viewBindings = bindingClass.getViewBinding(strId);
            if (viewBindings != null) {
                Iterator<FieldViewBinding> iterator = viewBindings.getFieldBindings().iterator();
                if (iterator.hasNext()) {
                    FieldViewBinding existingBinding = iterator.next();
                    error(element, "Attempt to use @%s for an already bound ID %d on '%s'. (%s.%s)",
                            ViewInj.class.getSimpleName(), id, existingBinding.getName(),
                            enclosingElement.getQualifiedName(), element.getSimpleName());
                    return;
                }
            }
        } else {
            bindingClass = getOrCreateTargetClass(targetClassMap, enclosingElement);
        }

        String name = element.getSimpleName().toString();
        TypeName type = TypeName.get(elementType);
        boolean required = isRequiredBinding(element);

        FieldViewBinding binding = new FieldViewBinding(name, type, required);
        bindingClass.addField(strId, binding);

        // Add the type-erased version to the valid binding targets set.
        erasedTargetNames.add(enclosingElement.toString());
    }

    private boolean isSubtypeOfType(TypeMirror typeMirror, String otherType) {
        if (otherType.equals(typeMirror.toString())) {
            return true;
        }
        if (typeMirror.getKind() != TypeKind.DECLARED) {
            return false;
        }
        DeclaredType declaredType = (DeclaredType) typeMirror;
        List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
        if (typeArguments.size() > 0) {
            StringBuilder typeString = new StringBuilder(declaredType.asElement().toString());
            typeString.append('<');
            for (int i = 0; i < typeArguments.size(); i++) {
                if (i > 0) {
                    typeString.append(',');
                }
                typeString.append('?');
            }
            typeString.append('>');
            if (typeString.toString().equals(otherType)) {
                return true;
            }
        }
        Element element = declaredType.asElement();
        if (!(element instanceof TypeElement)) {
            return false;
        }
        TypeElement typeElement = (TypeElement) element;
        TypeMirror superType = typeElement.getSuperclass();
        if (isSubtypeOfType(superType, otherType)) {
            return true;
        }
        for (TypeMirror interfaceType : typeElement.getInterfaces()) {
            if (isSubtypeOfType(interfaceType, otherType)) {
                return true;
            }
        }
        return false;
    }

    private boolean isInterface(TypeMirror typeMirror) {
        return typeMirror instanceof DeclaredType
                && ((DeclaredType) typeMirror).asElement().getKind() == INTERFACE;
    }

    private BindingClass getOrCreateTargetClass(Map<TypeElement, BindingClass> targetClassMap,
                                                TypeElement enclosingElement) {
        BindingClass bindingClass = targetClassMap.get(enclosingElement);
        if (bindingClass == null) {
            String targetType = enclosingElement.getQualifiedName().toString();
            String classPackage = getPackageName(enclosingElement);
            String className = getClassName(enclosingElement, classPackage) + BINDING_CLASS_SUFFIX;

            bindingClass = new BindingClass(classPackage, className, targetType);
            targetClassMap.put(enclosingElement, bindingClass);
        }
        return bindingClass;
    }

    private String getPackageName(TypeElement type) {
        return elementUtils.getPackageOf(type).getQualifiedName().toString();
    }

    private static String getClassName(TypeElement type, String packageName) {
        int packageLen = packageName.length() + 1;
        return type.getQualifiedName().toString().substring(packageLen).replace('.', '$');
    }

    private static boolean hasAnnotationWithName(Element element, String simpleName) {
        for (AnnotationMirror mirror : element.getAnnotationMirrors()) {
            String annotationName = mirror.getAnnotationType().asElement().getSimpleName().toString();
            if (simpleName.equals(annotationName)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isRequiredBinding(Element element) {
        return !hasAnnotationWithName(element, NULLABLE_ANNOTATION_NAME);
    }

    private void error(Element element, String message, Object... args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }
        processingEnv.getMessager().printMessage(ERROR, message, element);
    }

}
