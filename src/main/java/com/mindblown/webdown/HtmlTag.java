package com.mindblown.webdown;


import java.util.Arrays;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author beamj
 */
public class HtmlTag implements Comparable<Object>{
    
    private final String tagName;
    private final HtmlTagData data;
    
    public HtmlTag(String name, HtmlTagData d) {
        tagName = name;
        data = d;
    }

    public String getTagName() {
        return tagName;
    }
    
    public static boolean isTag(String l, String front, String back, String attrib){
        String line = Util.strip(l);
        return (line.startsWith(front) && line.endsWith(back) && line.contains(attrib + "=\""));
    }

    public Attrib getAttrib(String name) {
        int ind = Arrays.binarySearch(data.getTribs(), name);
        if(ind >= 0){
            return data.getTribs()[ind];
        }
        return null;
    }

    public HtmlTagData getData() {
        return data;
    }

    @Override
    public int compareTo(Object o) {
        if(o instanceof HtmlTag){
            HtmlTag tag = (HtmlTag)o;
            int nameComp = tagName.compareTo(tag.tagName);
            if(nameComp != 0){
                return nameComp;
            }
            return data.compareTo(tag.data);
        } else if(o instanceof TagName){
            TagName name = (TagName)o;
            return name.getTagName().compareTo(tagName);
        } else if(o instanceof Attrib){
            Attrib trib = (Attrib)o;
            if(Util.binaryHas(data.getTribs(), trib)){
                return 0;
            }
        }
        return hashCode() - o.hashCode();
    }
    
    @Override
    public String toString() {
        String mess = "<";
        if(!data.isStart()){
            mess += "/";
        }
        mess += tagName + " ";
        Attrib[] tribs = data.getTribs();
        for(int i = 0; i < tribs.length; i++){
            mess += tribs[i].toString();
            if(i != tribs.length - 1){
                mess += " ";
            }
        }
        mess += ">";
        return mess;
    }
}
