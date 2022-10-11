package com.mindblown.webdown;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author beamj
 */
public class DownloadResult {
    private ServerPartData treePartData;
    private String downloadMessage;
    private int resultCode;
    
    public static final int DOWNLOAD_SUCCESS = 100;
    public static final int DOWNLOAD_START_CANCEL = 403;
    public static final int DOWNLOAD_FAIL = 404;
    public static final int DOWNLOAD_CANCEL = 102;
    
    
    public DownloadResult(ServerPartData treePartData, String downloadMessage, int resultCode){
        this.treePartData = treePartData;
        this.downloadMessage = downloadMessage;
        this.resultCode = resultCode;
    }

    public String getDownloadMessage() {
        return downloadMessage;
    }

    public ServerPartData getServerPartData() {
        return treePartData;
    }

    public void setDownloadMessage(String downloadMessage) {
        this.downloadMessage = downloadMessage;
    }

    public void setServerPartData(ServerPartData treePart) {
        treePartData = treePart;
    }

    public ServerPartData getTreePartData() {
        return treePartData;
    }

    public int getResultCode() {
        return resultCode;
    }
}
