package com.mindblown.webdown;


import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author beamj
 */
public class UrlConfig implements Comparable<UrlConfig>{
    private String configName;
    private ArrayList<Url> urls = new ArrayList<>();
    
    public class Url implements Comparable<Url>{
        private Ref ref;
        private boolean isParent;

        public Url(Ref ref, boolean isParent) {
            this.ref = ref;
            this.isParent = isParent;
        }

        public Ref getRef() {
            return ref;
        }

        public boolean isParent() {
            return isParent;
        }

        @Override
        public int compareTo(Url o) {
            return ref.compareTo(o.ref);
        }
    }
    
    public UrlConfig(String configName) {
        this.configName = configName;
    }

    /**
     * Returns the name of the configuration.
     * @return name of the configuration
     */
    public String getConfigName() {
        return configName;
    }
    
    /**
     * Add url to the list of urls if url isn't already in the list.
     * @param url the file type to add
     */
    public void addUrl(Url url){
        Util.binaryAddNoDups(urls, url);
    }
    
    
    /**
     * Gets the url at the index parameter.
     * @param index location of url
     * @return url at index
     */
    public Url getUrl(int index){
        return urls.get(index);
    }
    
    /**
     * Returns whether url is in the url list.
     * @param url the url to look for
     * @return whether fileExtension is in fileTypeList
     */
    public boolean doesUrlExist(Url url){
        return Util.binaryHas(urls, url);
    }

    @Override
    public int compareTo(UrlConfig o) {
        return configName.compareTo(o.configName);
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public void setUrls(ArrayList<Url> urls) {
        this.urls = urls;
    }
    
    @Override
    public String toString() {
        return configName;
    }
}