package com.mindblown.webdown;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * This class extends FileType but instead of having a list of file extensions, 
 * it only contains one file extension.
 * @author beamj
 */
public class FileExtension extends FileType{
    
//    public FileExtension(String fileTypeName) {
//        super(fileTypeName, FileType.);
//    }

    public FileExtension(String fileTypeName, String fileExtension) {
        super(fileTypeName, fileExtension);
    }
    
    /**
     * Sets fileExtension as the object's only file extension
     * @param fileExtension 
     */
    @Override
    public void addFileExtension(String fileExtension) {
        //If the list is empty, add the file extension since it will be the only element
        if(isFileExtensionListEmpty()){
            super.addFileExtension(fileExtension);
        } else {
            //If the list isn't empty, set the first element of the list to the file extension 
            //since there should only be one element
            super.setFileExtension(0, fileExtension);
        }
    }
}
