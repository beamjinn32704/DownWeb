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
 */
public class ProcessConfig implements Comparable<ProcessConfig>{
    private String name;
    private boolean processesDownloads;
    private boolean checkFileTypesToDownload;
    private FileTypeModel fileTypesToDownload = new FileTypeModel();
    private FileTypeModel fileTypesNotToDownload = new FileTypeModel();

    public ProcessConfig(String configName, boolean processesDownloads, boolean checkFileTypesToDownload) {
        name = configName;
        this.processesDownloads = processesDownloads;
        this.checkFileTypesToDownload = checkFileTypesToDownload;
    }

    /**
     * Returns the name of the configuration.
     * @return name of the configuration
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns whether to look at the file types to download or the file types 
     * not to download.
     * @return whether to look at the file types to download or the file types 
     * not to download
     */
    public boolean checkFileTypesToDownload(){
        return checkFileTypesToDownload;
    }
    
    /**
     * Add the file type to the list of file types to download if the file type isn't already in the list.
     * @param fileType the file type to add
     */
    public void addFileTypeToDownload(FileType fileType){
        fileTypesToDownload.addFileType(fileType);
    }
    
    /**
     * Add the file type to the list of file types not to download if the file type isn't already in the list.
     * @param fileType the file type to add
     */
    public void addFileTypeNotToDownload(FileType fileType){
        fileTypesNotToDownload.addFileType(fileType);
    }
    
    /**
     * Gets the file type to download at the index parameter.
     * @param index location of file type
     * @return file type at index
     */
    public FileType getFileTypeToDownload(int index){
        return fileTypesToDownload.getElementAt(index);
    }
    
    /**
     * Gets the file type not to download at the index parameter.
     * @param index location of file type
     * @return file type at index
     */
    public FileType getFileTypeNotToDownload(int index){
        return fileTypesNotToDownload.getElementAt(index);
    }
    
    /**
     * Returns whether fileExtension to download is included in the ProcessConfig object.
     * @param fileExtension the file extension to check
     * @return whether fileExtension is in the ProcessConfig object
     */
    public boolean doesFileExtensionToDownloadExist(String fileExtension){
        return fileTypesToDownload.doesFileExtensionExist(fileExtension);
    }
    
    /**
     * Returns whether fileExtension not to download is included in the ProcessConfig object.
     * @param fileExtension the file extension to check
     * @return whether fileExtension is in the ProcessConfig object
     */
    public boolean doesFileExtensionNotToDownloadExist(String fileExtension){
        return fileTypesNotToDownload.doesFileExtensionExist(fileExtension);
    }
    
    /**
     * Return whether downloads should be processed.
     * @return whether downloads should be processed
     */
    public boolean processesDownloads(){
        return processesDownloads;
    }

    public void setCheckFileTypesToDownload(boolean checkFileTypesToDownload) {
        this.checkFileTypesToDownload = checkFileTypesToDownload;
    }

    public void setConfigName(String configName) {
        name = configName;
    }

    public void setFileTypesNotToDownload(ArrayList<FileType> fileTypesNotToDownload) {
        this.fileTypesNotToDownload.setFileTypes(fileTypesNotToDownload);
    }

    public void setFileTypesToDownload(ArrayList<FileType> fileTypesToDownload) {
        this.fileTypesToDownload.setFileTypes(fileTypesToDownload);
    }

    public void setProcessesDownloads(boolean processesDownloads) {
        this.processesDownloads = processesDownloads;
    }

    public FileTypeModel getFileTypesToDownload() {
        return fileTypesToDownload;
    }

    public FileTypeModel getFileTypesNotToDownload() {
        return fileTypesNotToDownload;
    }

    @Override
    public int compareTo(ProcessConfig o) {
        return name.compareTo(o.name);
    }

    @Override
    public String toString() {
        return name;
    }
}