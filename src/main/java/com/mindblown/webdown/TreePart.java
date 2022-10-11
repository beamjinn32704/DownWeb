package com.mindblown.webdown;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author beamj
 * @param <T>
 */
public class TreePart<T> implements Comparable<Object>{
    private final String branchName;
    private T t;
    private Branch parent = null;
    public static String serverParent = "D:/Servers/";
    
    public TreePart(String name, Branch p, T obj) {
        parent = p;
        branchName = name;
        t = obj;
        if(parent != null){
            parent.addPart(this);
        }
    }

    public Branch<T> getParent() {
        return parent;
    }
    
    public String getPos(){
        if(parent != null){
            return parent.getPos() + "/" + branchName;
        } else {
            return serverParent + branchName;
        }
    }
    
    public static String getPos(String bN, Branch p){
        if(p != null){
            return p.getPos() + "/" + bN;
        } else {
            return serverParent + bN;
        }
    }

    public T getObj() {
        return t;
    }
    
    public String getBranchName() {
        return branchName;
    }

    @Override
    public int compareTo(Object o) {
        if(o instanceof String){
            return getObj().toString().compareTo((String) o);
        } else if(o instanceof TreePart){
            return getObj().toString().compareTo(((TreePart) o).getObj().toString());
        } else {
            return hashCode() - o.hashCode();
        }
    }

    @Override
    public String toString() {
        return t.toString();
    }
    
    public Branch<T> nearestBranch(){
        if(this instanceof Branch){
            return (Branch)this;
        } else {
            return getParent();
        }
    }
}
