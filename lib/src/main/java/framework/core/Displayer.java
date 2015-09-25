package framework.core;

import android.content.Context;
import android.view.View;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import framework.inj.ViewInj;
import framework.inj.entity.Downloadable;
import framework.inj.entity.Loadable;
import framework.inj.entity.MutableEntity;
import framework.inj.entity.Postable;
import framework.inj.exception.ViewNotFoundException;
import framework.inj.impl.ViewInjector;

public class Displayer {

    private Jujuj jujuj;

    Displayer(Jujuj jujuj){
        this.jujuj = jujuj;
    }

}
