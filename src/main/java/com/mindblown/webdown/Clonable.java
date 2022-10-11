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
 * @param <T>
 */
public interface Clonable<T> {
    public T copy();
    
    public static Object[] clone(Object[] o){
        Object[] clone = new Object[o.length];
        
        for(int i = 0; i < o.length; i++){
            Object obj = o[i];
            if(obj instanceof Clonable){
                clone[i] = ((Clonable)obj).copy();
            } else if(obj instanceof String){
                clone[i] = ((String)obj).substring(0);
            } else {
                clone[i] = obj;
            }
        }
        return clone;
    }
    
    public static <T> ArrayList<T> clone(ArrayList<T> o){
        ArrayList<T> clone = new ArrayList<>();
        
        for(int i = 0; i < o.size(); i++){
            T obj = o.get(i);
            if(obj instanceof Clonable){
                clone.add(((Clonable<T>)obj).copy());
            } else if(obj instanceof String){
                clone.add((T)((String)((String) obj).substring(0)));
            } else {
                clone.add(obj);
            }
        }
        return clone;
    }
    
}
