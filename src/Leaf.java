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
public class Leaf<T> extends TreePart<T> {
    
    public Leaf(String name, Branch p, T obj) {
        super(name, p, obj);
    }
}
