package jujuj.internal;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import framework.inj.Constants;
import framework.inj.DependentInj;
import framework.inj.ViewInj;
import framework.inj.ViewValueInj;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class JujujProcessor extends AbstractProcessor {

    private static final String BINDING_CLASS_SUFFIX = "$$ViewBinder";
    private Elements elementUtils;
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);

        elementUtils = env.getElementUtils();
        filer = env.getFiler();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(ViewInj.class.getCanonicalName());
        types.add(ViewValueInj.class.getCanonicalName());
        types.add(DependentInj.class.getCanonicalName());
        return types;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<TypeElement, Generator> targetClassMap = findAndParseTargets(roundEnv);

        for (Map.Entry<TypeElement, Generator> entry : targetClassMap.entrySet()) {
            Generator bindingClass = entry.getValue();

            try {
                bindingClass.brewJava().writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    private Map<TypeElement, Generator> findAndParseTargets(RoundEnvironment env) {
        Map<TypeElement, Generator> targetClassMap = new LinkedHashMap<TypeElement, Generator>();

        // Process each @ViewInj element.
        for (Element element : env.getElementsAnnotatedWith(ViewInj.class)) {
            try {
                int id = element.getAnnotation(ViewInj.class).value();
                bind(element, targetClassMap, id, AnnotationItem.Type.ViewInj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (Element element : env.getElementsAnnotatedWith(ViewValueInj.class)) {
            try {
                int id = element.getAnnotation(ViewValueInj.class).value();
                bind(element, targetClassMap, id, AnnotationItem.Type.ViewValueInj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (Element element : env.getElementsAnnotatedWith(DependentInj.class)) {
            try {
                bind(element, targetClassMap, 0, AnnotationItem.Type.DependentInj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //TODO

        return targetClassMap;
    }

    private void bind(Element element, Map<TypeElement, Generator> targetClassMap,
                      int id, AnnotationItem.Type type) {
        String strId;
        if (id == Constants.DEFAULT) {
            //no id set
            //as default
            strId = element.getSimpleName().toString();
        } else {
            strId = id + "";
        }

        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        Generator bindingClass = targetClassMap.get(enclosingElement);
        if (bindingClass == null) {
            bindingClass = getOrCreateTargetClass(targetClassMap, enclosingElement);
        }

        String elementName = element.getSimpleName().toString();
        AnnotationItem binding = new AnnotationItem(elementName, type, strId);
        bindingClass.addAnnotation(binding);
    }

    //get target class
    private Generator getOrCreateTargetClass(Map<TypeElement, Generator> targetClassMap,
                                             TypeElement enclosingElement) {
        Generator generator = targetClassMap.get(enclosingElement);
        if (generator == null) {
            String targetType = enclosingElement.getQualifiedName().toString();
            String classPackage = getPackageName(enclosingElement);
            String className = getClassName(enclosingElement, classPackage) + BINDING_CLASS_SUFFIX;

            generator = new Generator(classPackage, className, targetType);
            targetClassMap.put(enclosingElement, generator);
        }
        return generator;
    }

    private String getPackageName(TypeElement type) {
        return elementUtils.getPackageOf(type).getQualifiedName().toString();
    }

    private static String getClassName(TypeElement type, String packageName) {
        int packageLen = packageName.length() + 1;
        return type.getQualifiedName().toString().substring(packageLen).replace('.', '$');
    }


}
