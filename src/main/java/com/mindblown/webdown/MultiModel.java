package com.mindblown.webdown;


import java.util.ArrayList;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author beamj
 * @param <E>
 */
public class MultiModel<E extends Comparable<E>> implements ComboBoxModel<E>, ListModel<E>{
    
    //Make a class that manages two different model types. Implement the ComboBoxModel and the ListModel so this
    //class can be used as a model for others.
    
    private DefaultListModel<E> listModel;
    private DefaultComboBoxModel<E> boxModel;

    public MultiModel() {
        listModel = new DefaultListModel<>();
        boxModel = new DefaultComboBoxModel<>();
    }
    
    public boolean hasElement(E elem){
        return Util.binaryIndexOf(getElements(), elem) >= 0;
    }
    
    public ArrayList<E> getElements(){
        ArrayList<E> elems = new ArrayList<>();
        for(int i = 0; i < listModel.getSize(); i++){
            elems.add(listModel.getElementAt(i));
        }
        return elems;
    }
    
    /**
     * Adds an element to the model.
     * @param elem the element to add.
     * @param noDups If this is true and there is an element in the model that when 
     * compared to elem is 0, this function won't add elem into model.
     */
    public void addElement(E elem, boolean noDups){
        ArrayList<E> elems = getElements();
        int elemInd = Util.binaryIndexOf(elems, elem);
        if(elemInd >= 0 && noDups){
            return;
        } else {
            elemInd = Util.translateIndex(elemInd);
        }
        listModel.add(elemInd, elem);
        
        //Make a list that only contains the element because the box model only accepts collections
        ArrayList<E> boxSingleList = new ArrayList<>();
        boxSingleList.add(elem);
        boxModel.addAll(elemInd, boxSingleList);
    }
    
    public void removeElement(E elem){
        listModel.removeElement(elem);
        boxModel.removeElement(elem);
    }
    
    public void removeElement(int index){
        listModel.removeElementAt(index);
        boxModel.removeElementAt(index);
    }

    @Override
    public void setSelectedItem(Object anItem) {
        boxModel.setSelectedItem(anItem);
    }

    @Override
    public Object getSelectedItem() {
        return boxModel.getSelectedItem();
    }

    @Override
    public int getSize() {
        return listModel.getSize();
    }

    @Override
    public E getElementAt(int index) {
        return listModel.getElementAt(index);
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        listModel.addListDataListener(l);
        boxModel.addListDataListener(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        listModel.removeListDataListener(l);
        boxModel.addListDataListener(l);
    }
}
