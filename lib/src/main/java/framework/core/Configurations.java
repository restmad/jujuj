package framework.core;

import java.util.ArrayList;

import framework.inj.impl.ViewBinder;
import framework.net.image.AbsImageProvider;
import framework.provider.AbsDataProvider;

public class Configurations {

    String encoder;
    String charset;
    AbsDataProvider dataProvider;
    AbsImageProvider imageProvider;
    ArrayList<ViewBinder> binders;

    private Configurations(Builder builder){
        if(builder.dataProvider == null){
            throw new IllegalArgumentException("DataProvider must be set before using");
        }

        encoder = builder.encoder;
        charset = builder.charset;
        dataProvider = builder.dataProvider;
        imageProvider = builder.imageProvider;
        binders = builder.binders;
    }

    public static class Builder{
        private String encoder;
        private String charset;
        private AbsDataProvider dataProvider;
        private AbsImageProvider imageProvider;
        private ArrayList<ViewBinder> binders = new ArrayList<>();

        public Builder(){
            encoder = "gbk";
            charset = "utf-8";
        }

        public Builder setEncoder(String encoder){
            this.encoder = encoder;
            return this;
        }

        public Builder setCharset(String charset){
            this.charset = charset;
            return this;
        }

        public Builder addDataProvider(AbsDataProvider provider){
            if(provider == null){
                throw new IllegalArgumentException("DataProvider can't be null");
            }
            if(this.dataProvider == null){
                dataProvider = provider;
            }else{
                addDataProvider(dataProvider, provider);
            }
            return this;
        }

        public Builder addViewInjector(ViewBinder injector){
            binders.add(injector);
            return this;
        }

        private void addDataProvider(AbsDataProvider base, AbsDataProvider provider){
            AbsDataProvider supervisor = base.getSupervisor();
            if(supervisor != null){
                addDataProvider(supervisor, provider);
            }else{
                base.setSupervisor(provider);
            }
        }

        public Builder setImageProvider(AbsImageProvider provider){
            imageProvider = provider;
            return this;
        }

        public Configurations build(){
            return new Configurations(this);
        }
    }
}
