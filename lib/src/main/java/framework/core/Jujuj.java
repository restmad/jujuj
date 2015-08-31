package framework.core;

import java.util.ArrayList;

import framework.inj.impl.AbsListViewInjector;
import framework.inj.impl.CheckBoxInjector;
import framework.inj.impl.ImageViewInjector;
import framework.inj.impl.SpinnerInjector;
import framework.inj.impl.TextViewInjector;
import framework.inj.impl.ViewInjector;
import framework.inj.impl.WebViewInjector;
import framework.net.abs.AbsDataProvider;

/**
 * Created by shinado on 15/8/28.
 * inject -> (loadEntity)* -> setContent
 *   -> post(set on post)
 */
public class Jujuj {

    private ArrayList<ViewInjector> injectors;

    private Jujuj(){
        injectors = new ArrayList<>();
        injectors.add(new ImageViewInjector());
        injectors.add(new CheckBoxInjector());
        injectors.add(new AbsListViewInjector());
        injectors.add(new SpinnerInjector());
        injectors.add(new WebViewInjector());
        injectors.add(new TextViewInjector());
    }

    public void inject(){

    }

    public void loadEntity(){

    }

    private void setContent(){

    }

    private void setOnPost(){

    }

    private void post(){

    }
}
