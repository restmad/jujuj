package framework.core;

import java.util.TreeSet;

import framework.net.abs.AbsDataProvider;

/**
 * Created by shinado on 15/8/27.
 */
public class Configurations {

    String encoder;
    String charset;
    TreeSet<AbsDataProvider> dataProviders;

    private Configurations(Builder builder){
        encoder = builder.encoder;
        charset = builder.charset;
        dataProviders = builder.dataProviders;
    }

    public static Configurations getDefault(){
        return new Configurations.Builder().build();
    }

    public static class Builder{
        private String encoder;
        private String charset;
        private TreeSet<AbsDataProvider> dataProviders;

        public Builder(){
            dataProviders = new TreeSet<>();
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

        public Builder addDataProvider(AbsDataProvider dataProvider){
            this.dataProviders.add(dataProvider);
            return this;
        }

        public Configurations build(){
            return new Configurations(this);
        }
    }
}
