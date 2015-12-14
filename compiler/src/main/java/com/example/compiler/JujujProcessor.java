package com.example.compiler;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import framework.inj.ViewInj;

public class JujujProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(ViewInj.class);
        for(Element e : elements){
//            if(!e.getClass().equals(ParticularType.class)){
//                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
//                        "@CustomAnnotation annotated fields must be of type ParticularType");
//            }
        }
        return true;
    }
}
