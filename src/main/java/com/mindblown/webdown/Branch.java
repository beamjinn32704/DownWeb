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
public class Branch<T> extends TreePart<T> {
    
    private ArrayList<TreePart<T>> parts;
    
    public Branch(String name, Branch p, T obj) {
        super(name, p, obj);
        if(parts == null){
            parts = new ArrayList<>();
        }
    }
    
    public void remove(int ind){
        parts.remove(ind);
    }
    
    public void remove(TreePart<T> part){
        Util.binaryRemove(parts, part);
    }
    
    public TreePart find(TreePart part){
        int ind = Util.binaryIndexOf(parts, part);
        if(ind >= 0){
            return parts.get(ind);
        } else {
            return null;
        }
    }
    
    public ArrayList<TreePart<T>> getParts() {
        return parts;
    }
    
    public void addPart(TreePart part){
        Util.binaryAdd(parts, part);
    }

    public void setParts(ArrayList<TreePart<T>> parts) {
        this.parts = parts;
    }
}
