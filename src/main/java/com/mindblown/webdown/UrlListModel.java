package com.mindblown.webdown;


import com.mindblown.webdown.UrlDownData;
import com.mindblown.webdown.Util;
import java.util.ArrayList;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author beamj
 */
public class UrlListModel implements ListModel{
    
    private ArrayList<UrlDownData> data;
    private ArrayList<ListDataListener> listeners = new ArrayList<>();

    public UrlListModel() {
        data = new ArrayList<>();
    }

    public UrlListModel(ArrayList<UrlDownData> data) {
        this.data = data;
    }

    public void setData(ArrayList<UrlDownData> data) {
        this.data = data;
    }

    public ArrayList<UrlDownData> getData() {
        return data;
    }
    
    public void add(UrlDownData d){
        int ind = Util.binaryIndexOf(data, d);
        if(ind >= 0){
            return;
        }
        ind = -1 * (ind + 1);
        data.add(ind, d);
        for(ListDataListener l : listeners){
            l.intervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, ind, ind));
        }
    }
    
    public void remove(UrlDownData d){
        int ind = Util.binaryIndexOf(data, d);
        if(ind < 0){
            return;
        }
        remove(ind);
    }
    
    public void remove(int index){
        data.remove(index);
        for(ListDataListener l : listeners){
            l.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index, index));
        }
    }

    @Override
    public int getSize() {
        return data.size();
    }
    
    @Override
    public UrlDownData getElementAt(int index) {
        if(index >= data.size() || index < 0){
            System.out.println("(UrlListModel::getElementAt) recieved invalid index " + index + ". Data length is " + data.size() + ".");
            return null;
        }
        UrlDownData element = data.get(index);
        return element;
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        listeners.add(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        listeners.remove(l);
    }
    
}
