package com.mindblown.configdvpmnt;


import com.mindblown.webdown.Util;
import java.util.ArrayList;
import javax.swing.ComboBoxModel;
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
public class UrlConfigModel implements ComboBoxModel<UrlConfig>{
    
    private ArrayList<UrlConfig> urlConfigs = new ArrayList<>();
    private int currentIndex = -1;
    
    
    /**
     * Add config to the list of url configurations if config isn't already in there.
     * @param config the url configuration to add
     */
    public void addConfig(UrlConfig config){
        Util.binaryAddNoDups(urlConfigs, config);
    }
    
    /**
     * Sets the configuration at location index to config.
     * @param index the index of the configuration to set
     * @param config the config to be set
     */
    public void setConfig(int index, UrlConfig config){
        urlConfigs.set(index, config);
    }
    
    @Override
    public void setSelectedItem(Object anItem) {
        if(!(anItem instanceof UrlConfig)){
            //If the object isn't a url config, return
            return;
        }
        
        //Get index of item. If it's in the list set the index.
        int ind = Util.binaryIndexOf(urlConfigs, (UrlConfig)anItem);
        if(ind < 0){
            return;
        }
        currentIndex = ind;
    }
    
    @Override
    public Object getSelectedItem() {
        if(currentIndex == -1){
            return null;
        }
        
        return urlConfigs.get(currentIndex);
    }
    
    @Override
    public int getSize() {
        return urlConfigs.size();
    }
    
    @Override
    public UrlConfig getElementAt(int index) {
        return urlConfigs.get(index);
    }
    
    @Override
    public void addListDataListener(ListDataListener l) {
        
    }
    
    @Override
    public void removeListDataListener(ListDataListener l) {
        
    }
}