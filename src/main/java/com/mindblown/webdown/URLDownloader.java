package com.mindblown.webdown;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;

/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

/**
 *
 * @author beamj
 */
public class URLDownloader {
    
    ServerTree serverTree;
    
    public URLDownloader(ServerTree tree) {
        serverTree = tree;
    }
    
    private String urlOk(HttpURLConnection httpConnection, boolean print){
        try{
            if(!FileAction.contin){
                return null;
            }
            int code = httpConnection.getResponseCode();
            if(!FileAction.contin){
                return null;
            }
            if(code != HttpURLConnection.HTTP_OK){
                if(print){
                    String message = httpConnection.getResponseMessage() + " " + code;
                    if(code == HttpURLConnection.HTTP_FORBIDDEN){
                        InputStream errorStream = httpConnection.getErrorStream();
                        String errorText = Util.readInputStream(errorStream, 100000);
                        if(errorText.contains("captcha") || errorText.contains("Captcha")){
                            message += " Due to Captcha";
                        }
                    }
                    MainFrame.main.addDetailedLog("HTTP Connection Error: " + message, MainFrame.FAILURE);
                    System.out.println("Error Message: " + message);
                    return message;
                }
                return "NotOK";
            }
        } catch(Exception e){
            System.out.println("HTTP CONNECTION ERROR!!!!");
            MainFrame.main.addDetailedLog("Failed to Receive HTTP Connection's Response!", MainFrame.FAILURE);
            return "Failed To Get HTTP Connection's Response";
        }
        return "ok";
    }
    
    public DownloadResult download(Ref ref) {
        boolean go = true;
        HttpURLConnection httpConnection = null;
        int fail = 0;
        String url = ref.toString();
        if(!FileAction.contin){
            return new DownloadResult(null, "Stopped Due to FileAction Exiting",  DownloadResult.DOWNLOAD_START_CANCEL);
        }
        try{//Http connection is disconnected in catch, but it might sitll be null
            while(go){
                if(!FileAction.contin){
                    return new DownloadResult(null, "Stopped Due to FileAction Exiting", DownloadResult.DOWNLOAD_START_CANCEL);
                }
                URL u = new URL(url);
                MainFrame.main.setCurrentProcess("Opening Connection...", 33);
                URLConnection connection = u.openConnection();
                connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.121 Safari/537.36");
                MainFrame.main.setCurrentProcess("Opened Connection!", 35);
                if(!FileAction.contin){
                    return new DownloadResult(null, "Stopped Due to FileAction Exiting", DownloadResult.DOWNLOAD_START_CANCEL);
                }
                try {
                    MainFrame.main.setCurrentProcess("Getting HTTP Connection...", 37);
                    httpConnection = (HttpURLConnection) connection;
                    MainFrame.main.setCurrentProcess("Got HTTP Connection!", 40);
                    MainFrame.main.addDetailedLog("Established HTTP Connection!", MainFrame.NOTIFICATION);
                    if(!FileAction.contin){
                        httpConnection.disconnect();
                        return new DownloadResult(null, "Stopped Due to FileAction Exiting", DownloadResult.DOWNLOAD_START_CANCEL);
                    }
                } catch(Exception e){
                    System.err.println("FAILED TO DOWNLOAD " + url);
                    MainFrame.main.addDetailedLog("Unable to Establish HTTP Connection!", MainFrame.NOTIFICATION);
                    httpConnection.disconnect();
                    return new DownloadResult(null, "Unable to Establish HTTP Connection!", DownloadResult.DOWNLOAD_FAIL);
                }
                httpConnection.setInstanceFollowRedirects(true);
                MainFrame.main.setCurrentProcess("Checking HTTP Connections's Response Code...", 43);
                String urlOk = urlOk(httpConnection, fail == 1);
                if(urlOk == null || !urlOk.equals("ok")){
                    MainFrame.main.setCurrentProcess("Bad URL!", 43);
                    fail++;
                    System.err.println("FAILED TO DOWNLOAD " + url);
                    
                    if(!FileAction.contin){
                        httpConnection.disconnect();
                        return new DownloadResult(null, "Stopped Due to FileAction Exiting", DownloadResult.DOWNLOAD_START_CANCEL);
                    }
                    
                    if(fail > 1){
                        httpConnection.disconnect();
                        return new DownloadResult(null, urlOk, DownloadResult.DOWNLOAD_FAIL);
                    } else if(fail == 1){
                        String proto = ref.getProtocol();
                        if(proto.equals("http://")){
                            ref.setProtocol("https://");
                            MainFrame.main.setCurrentProcess("Trying HTTPS...", 45);
                            MainFrame.main.addDetailedLog("HTTP Protocol Failed!", MainFrame.FAILURE);
                            MainFrame.main.addDetailedLog("Trying HTTPS...", MainFrame.PROCESS);
                        } else if(proto.equals("https://")){
                            ref.setProtocol("http://");
                            MainFrame.main.setCurrentProcess("Trying HTTP...", 45);
                            MainFrame.main.addDetailedLog("HTTPS Protocol Failed!", MainFrame.FAILURE);
                            MainFrame.main.addDetailedLog("Trying HTTP...", MainFrame.NOTIFICATION);
                        } else {
                            httpConnection.disconnect();
                            return new DownloadResult(null, urlOk, DownloadResult.DOWNLOAD_FAIL);
                        }
                        url = ref.toString();
                        if(!FileAction.contin){
                            httpConnection.disconnect();
                            return new DownloadResult(null, "Stopped Due to FileAction Exiting", DownloadResult.DOWNLOAD_START_CANCEL);
                        }
                    }
                } else {
                    go = false;
                    if(!FileAction.contin){
                        httpConnection.disconnect();
                        return new DownloadResult(null, "Stopped Due to FileAction Exiting0", DownloadResult.DOWNLOAD_START_CANCEL);
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            System.err.println("FAILED TO DOWNLOAD " + url);
            if(httpConnection != null){
                httpConnection.disconnect();
            }
            return new DownloadResult(null, "Failed To Open Connection and Communicate With It", DownloadResult.DOWNLOAD_FAIL);
        }
        
        if(!FileAction.contin){
            httpConnection.disconnect();
            return new DownloadResult(null, "Stopped Due to FileAction Exiting", DownloadResult.DOWNLOAD_START_CANCEL);
        }
        
        ServerPartData partData;
        MainFrame.main.setCurrentProcess("Opening Input Stream...", 45);
        httpConnection.setReadTimeout(1000*60);
        try (InputStream in = httpConnection.getInputStream()) {
            System.out.print("DOWNLOAD: URL: " + url);
            if(!FileAction.contin){
                in.close();
                httpConnection.disconnect();
                return new DownloadResult(null, "Stopped Due to FileAction Exiting", DownloadResult.DOWNLOAD_START_CANCEL);
            }
            
            MainFrame.main.setCurrentProcess("Creating Server Node...", 47);
            URL u = httpConnection.getURL();
            Ref tempRef = new Ref(u);
            if(tempRef.compareTo(ref) != 0){
                ref = tempRef;
                MainFrame.main.addDetailedLog("Redirecting to " + ref, MainFrame.NOTIFICATION);
                boolean redirectExists = serverTree.exists(ref);
                if(redirectExists){
                    in.close();
                    httpConnection.disconnect();
                    return new DownloadResult(null, "Redirected Url " + ref + " already exists.", DownloadResult.DOWNLOAD_START_CANCEL);
                } else if(!shouldDownload(ref)){
                    in.close();
                    httpConnection.disconnect();
                    return new DownloadResult(null, "Redirected Url " + ref + " is a resource html "
                            + "and shouldn't be downloaded.", DownloadResult.DOWNLOAD_START_CANCEL);
                }
            }
            partData = serverTree.makeData(ref);
            MainFrame.main.setCurrentProcess("Created Server Node!", 50);
            MainFrame.main.addDetailedLog("Established Server Node!", MainFrame.NOTIFICATION);
            if(!FileAction.contin){
                in.close();
                httpConnection.disconnect();
                return new DownloadResult(null, "Stopped Due to FileAction Exiting", DownloadResult.DOWNLOAD_START_CANCEL);
            }
            File out = partData.getFile();
            if(ref.getPath().endsWith("/") || Util.isBlank(ref.getPath())){
                out.mkdirs();
                out = new File(out, "index.html");
            } else {
                if(out.getName().contains(".")){
                    
                } else {
                    out = new File(out.getParentFile(), out.getName() + ".html");
                }
            }
            if(out.exists()){
                System.out.println("--------------------------\n\nFILE " + out + " ALREADY EXISTS\n\n-----------"
                        + "------------");
            }
            out.getParentFile().mkdirs();
            partData.setFile(out);
            byte[] buffer = new byte[1000000];
            String urlSize = Util.formatBytes(httpConnection.getContentLengthLong());
            ByteCount byteCount = new ByteCount();
            PerSecondKeeper perSecond = new PerSecondKeeper("0.0 B");
            Timer bytesRecorderTimer = new Timer();
            bytesRecorderTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    perSecond.setPerSecond(Util.formatBytes(byteCount.getBytes() * 4));
                    byteCount.reset();
                }
            }, 250, 250);
            MainFrame.main.setCurrentProcess("Writing to File...", 50);
            MainFrame.main.addDetailedLog("Downloading to File...", MainFrame.PROCESS);
            try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(out), 1000000)) {
                int read;
                while((read = in.read(buffer)) != -1){
                    if(!FileAction.contin){
                        if(!out.delete()){
                            out.deleteOnExit();//Find out the specific things to return based on what happens (DeideResult)
                        }
                        bos.close();
                        in.close();
                        httpConnection.disconnect();
                        return new DownloadResult(null, "Stopped in the Middle of Download Due to FileAction Exiting", DownloadResult.DOWNLOAD_START_CANCEL);
                    }
                    MainFrame.main.setCurrentProcess("Written " + Util.formatBytes(byteCount.getTotalBytes()) + "/" + urlSize + " at " + perSecond.getPerSecond() + " per second!", 55);
                    bos.write(buffer, 0, read);
                    byteCount.add(read);
                }
            } catch (Exception e){
                Util.persistentDelete(out, 3);
                throw e;
            }
        } catch (Exception e){
            e.printStackTrace();
            System.err.println("FAILED TO DOWNLOAD " + url);
            httpConnection.disconnect();
            return new DownloadResult(null, "Failed To Download To File. Error Message: " + e.getMessage(), DownloadResult.DOWNLOAD_FAIL);
        }
        httpConnection.disconnect();
        System.out.println("    DONE");
        // WHEN FILE IS DOWNLOADED, GET RID OF BACKUP
        File downloadedFile = partData.getFile().getAbsoluteFile();
        File potentialBackup = new File(downloadedFile.getParentFile(), downloadedFile.getName() + "."
                + ServerTree.backupServerFileExtension);
        if(potentialBackup.isFile()){
            Util.persistentDelete(potentialBackup, 3);
        }
        return new DownloadResult(new ServerPartData(downloadedFile, partData.getRef()), "Downloaded Successfully!", DownloadResult.DOWNLOAD_SUCCESS);
    }
    
    
    
    private class PerSecondKeeper {
        private String perSecond;
        
        public PerSecondKeeper(String perSecond) {
            this.perSecond = perSecond;
        }
        
        public void setPerSecond(String perSecond) {
            this.perSecond = perSecond;
        }
        
        public String getPerSecond() {
            return perSecond;
        }
    }
    
    /**
     * Decides whether the ref given should be downloaded. This function decides with these rules: 
     * If the ref has the same host as the server tree (aka not a resource) then it can be downloaded. 
     * If the ref is (or appears to be) a link to a html page, then the ref shouldn't be downloaded. 
     * This function determines whether the ref appears to be a link to a html page with these following rules: 
     * If the ref's path is blank, if there is a slash at the end of the ref's path, if there is no period in 
     * the last section (everything after the last slash in the ref's path), or if the ref's path ends with .html 
     * or .htm, or something else (that means its an html file or similar to one like an xhtml file)
     * @param ref The ref to check
     * @return whether the ref should be downloaded.
     */
    public boolean shouldDownload(Ref ref){
        if(!serverTree.isResource(ref)){
            return true;
        }
        
        return !ref.isHtml();
    }
}