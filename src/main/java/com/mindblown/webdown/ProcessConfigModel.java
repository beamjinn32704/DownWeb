package com.mindblown.webdown;


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
public class ProcessConfigModel implements ComboBoxModel<ProcessConfig>{
    
    private ArrayList<ProcessConfig> processConfigs = new ArrayList<>();
    private int currentIndex = -1;
    /**
     * Add config to the list of processing downloads configurations if config isn't already in there.
     * @param config the processing downloads configuration to add
     */
    public void addConfig(ProcessConfig config){
        Util.binaryAddNoDups(processConfigs, config);
    }
    
    /**
     * Sets the configuration at location index to config.
     * @param index the index of the configuration to set
     * @param config the config to be set
     */
    public void setConfig(int index, ProcessConfig config){
        processConfigs.set(index, config);
    }
    
    @Override
    public void setSelectedItem(Object anItem) {
        if(!(anItem instanceof ProcessConfig)){
            //If the object isn't a process config, return
            return;
        }
        
        //Get index of item. If it's in the list set the index.
        int ind = Util.binaryIndexOf(processConfigs, (ProcessConfig)anItem);
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
        
        return processConfigs.get(currentIndex);
    }
    
    @Override
    public int getSize() {
        return processConfigs.size();
    }
    
    @Override
    public ProcessConfig getElementAt(int index) {
        return processConfigs.get(index);
    }
    
    @Override
    public void addListDataListener(ListDataListener l) {
        
    }
    
    @Override
    public void removeListDataListener(ListDataListener l) {
        
    }
}
