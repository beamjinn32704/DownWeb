package com.mindblown.webdown;


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
public class FileTypeModel implements ComboBoxModel<FileType> {
    
    private ArrayList<FileType> fileTypes = new ArrayList<>();
    private int currentIndex = -1;
    
    /**
     * Add fileType to the list of processing downloads configurations if fileType isn't already in there.
     * @param fileType the processing downloads configuration to add
     */
    public void addFileType(FileType fileType){
        Util.binaryAddNoDups(fileTypes, fileType);
    }
    
    /**
     * Sets the file type at location index to fileType.
     * @param index the index of the configuration to set
     * @param fileType the fileType to be set
     */
    public void setFileType(int index, FileType fileType){
        fileTypes.set(index, fileType);
    }
    
    @Override
    public void setSelectedItem(Object anItem) {
        if(!(anItem instanceof FileType)){
            //If the object isn't a fileType, return
            return;
        }
        
        //Get index of item. If it's in the list set the index.
        int ind = Util.binaryIndexOf(fileTypes, (FileType)anItem);
        if(ind < 0){
            return;
        }
        currentIndex = ind;
    }
    
    /**
     * Returns whether fileExtension is in the model.
     * @param fileExtension the file extension to look for
     * @return whether fileExtension is in fileTypeList
     */
    public boolean doesFileExtensionExist(String fileExtension){
        for(FileType fileType : fileTypes){
            if(fileType.doesFileExtensionExist(fileExtension)){
                return true;
            }
        }
        return false;
    }

    public void setFileTypes(ArrayList<FileType> fileTypes) {
        this.fileTypes = fileTypes;
        //Make sure the current index matches the changed file types list
        currentIndex = Math.min(currentIndex, this.fileTypes.size());
    }
    
    @Override
    public Object getSelectedItem() {
        if(currentIndex == -1){
            return null;
        }
        
        return fileTypes.get(currentIndex);
    }
    
    @Override
    public int getSize() {
        return fileTypes.size();
    }
    
    @Override
    public FileType getElementAt(int index) {
        return fileTypes.get(index);
    }
    
    @Override
    public void addListDataListener(ListDataListener l) {
        
    }
    
    @Override
    public void removeListDataListener(ListDataListener l) {
        
    }
}
