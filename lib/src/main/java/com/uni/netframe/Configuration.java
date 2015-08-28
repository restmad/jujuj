package com.uni.netframe;

import framework.net.abs.AbsDataProvider;
import framework.net.impl.VolleyProvider;

/**
 * Created by shinado on 15/8/27.
 */
public class Configuration {

    String encoder;
    String charset;
    AbsDataProvider networkRequest;

    private Configuration(Builder builder){
        encoder = builder.encoder;
        charset = builder.charset;
        networkRequest = builder.networkRequest;
    }

    public static Configuration getDefault(){
        return new Configuration.Builder().build();
    }

    public static class Builder{
        private String encoder;
        private String charset;
        private AbsDataProvider networkRequest;

        public Builder(){
            networkRequest = new VolleyProvider();
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

        public Builder setNetworkRequest(AbsDataProvider networkRequest){
            this.networkRequest = networkRequest;
            return this;
        }

        public Configuration build(){
            return new Configuration(this);
        }
    }
}
