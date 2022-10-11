package com.mindblown.webdown;


import java.io.File;
import java.io.FileFilter;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;

/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

/**
 *
 * @author beamj
 */
public class FileAction implements Runnable {
   
    private ArrayList<Ref> noDown;
    private ServerTree serverTree;
    private URLDownloader downloader;
    private FailedUrlsPanel failedUrlsPanel;
    private boolean downloadFiles;
    public static boolean contin = true;
    private boolean downloadedServer = false;
    private int downloadCount = 0;
    private static int backupPerDownloadsNum = 50;
    public static int maxBackupPerDownloadsNum = 100;
    private int numTimesDeletedUrlToDown = 0;
    
    private static final int resetCodeGeneratorInterval = 100;
    
    private CodeGenerator urlDownDataFileNameGenerator;
    
    public static final char slashReplacement = '#';
    
    private String startUrl;
    private UrlDownData startUrlData;
    
    private int numOfUrlsToDown = 0;
    
    private boolean suddenBackup = false;
    
    private Thread runningThread;
    
    public FileAction(String sUrl, ServerTree serverTree) {
        noDown = new ArrayList<>();
        this.serverTree = serverTree;
        downloader = new URLDownloader(serverTree);
        calculateNumUrlsToDown();
        startUrl = sUrl;
        resetUrlDownDataFileNameGenerator();
    }
    
    /**
     * Creates a code generator that generates unique names for the UTD files. This can be used multiple times 
     * throughout the program, since many UTD files will be deleted, so the code generator should be reset.
     */
    private void resetUrlDownDataFileNameGenerator(){
        urlDownDataFileNameGenerator = CodeGenerator.createCodeGeneratorBasedOnFileNames(MainFrame.urlsToDownFolder, "00000001", new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile();
            }
        });
    }
    
    private void calculateNumUrlsToDown(){
        int count = 0;
        try(DirectoryStream stream = Files.newDirectoryStream(MainFrame.urlsToDownFolder.toPath())){
            Iterator<Path> iterator = stream.iterator();
            while(iterator.hasNext()){
                iterator.next();
                count++;
            }
            numOfUrlsToDown = count;
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public int getNumOfUrlsToDown() {
        return numOfUrlsToDown;
    }
    
    public void initDown(String startUrl){
        Ref r = new Ref(startUrl);
        r.setProtocol(serverTree.getServerRef().getProtocol());
        String fav = r.getProtocol() + r.getHost() + "favicon.ico";
        String four04 = r.getProtocol() + r.getHost() + "404";
        ServerPartData serverTreeData = serverTree.getServerTreeData();
        startUrlData = new UrlDownData(serverTreeData, r, "FILE ACTION COMMAND CONSTRUCTOR", "NO TAG", "");
        addUrlToDown(startUrlData);
        addUrlToDown(new UrlDownData(serverTreeData, new Ref(fav), "FILE ACTION COMMAND CONSTRUCTOR", "NO TAG", ""));
        addUrlToDown(new UrlDownData(serverTreeData, new Ref(four04), "FILE ACTION COMMAND CONSTRUCTOR", "NO TAG", ""));
    }
    
    public void setStartupUrl(String sUrl){
        if(startUrl.equals(sUrl)){
            return;
        }
        Ref r = new Ref(sUrl);
        r.setProtocol(serverTree.getServerRef().getProtocol());
        addUrlToDown(new UrlDownData(serverTree.getServerTreeData(), r, "FILE ACTION COMMAND CONSTRUCTOR", "NO TAG", ""));
        removeUrlToDown(startUrlData);
    }

    public static int getBackupPerDownloadsNum() {
        return backupPerDownloadsNum;
    }
    
    public void doSuddenBackup(){
        suddenBackup = true;
    }
    
    public int getNumOfLeaves(){
        return serverTree.getTotalNumOfLeaves();
    }

    public static void setBackupPerDownloadsNum(int bpdn) {
        backupPerDownloadsNum = bpdn;
    }
    
    public void setFailedUrlsPanel(FailedUrlsPanel failedUrlsPanel) {
        this.failedUrlsPanel = failedUrlsPanel;
    }

    public ServerTree getServerTree() {
        return serverTree;
    }
    
    public void processTree(){
        System.out.println("Processing Tree!");
        int count = 0;
        ArrayList<File> dirsToStreamThrough = new ArrayList<>();
        dirsToStreamThrough.add(serverTree.getServerTreeData().getFile().getAbsoluteFile());
        while(!dirsToStreamThrough.isEmpty()){
            File fileDir = dirsToStreamThrough.remove(0);
            File[] files = fileDir.listFiles();
            for(int i = 0; i < files.length; i++){
                File file = files[i].getAbsoluteFile();
                if(file.isDirectory()){
                    dirsToStreamThrough.add(file);
                } else {
                    boolean accept = ServerTree.filesFilter.accept(file);
                    if(accept){
                        ServerPartData fileData = serverTree.makeData(file);
                        ServerPartData parentData = serverTree.nearestParentData(fileData);
                        actOnFile(fileData, parentData);
                        count++;
                        System.out.println("Count: " + count);
                    }
                }
            }
        }
        System.out.println("Finished Processing Tree!");
    }
    
    public void addUrlToDown(UrlDownData d){
        Ref dataUrl = d.getUrl();
        if(!dataUrl.isValidUrl()){
            return;
        }
        if(Ref.isMailto(dataUrl)){
            return;
        }
        if(!downloader.shouldDownload(dataUrl)){
            return;
        }
        boolean nodown = Util.binaryHas(noDown, dataUrl);
        boolean exists = serverTree.exists(dataUrl);
        if(!nodown && !exists){
            Cacheable.cache(d, genUrlDownDataFileSave());
            numOfUrlsToDown++;
            /*
            Instead of Doing Binary Add^, I need to give each UrlDownData it's own file.
            The file's name should be the Ref's Head and Name together (no protocol),
            with the slashes replaced with # 's 
            */
            MainFrame.main.doProgressBar();
            if(downloadedServer){
                downloadedServer = false;
                MainFrame.main.restartServerDownload();
            }
        }
    }
    
    private File genUrlDownDataFileSave(){
        return new File(MainFrame.urlsToDownFolder, urlDownDataFileNameGenerator.next()).getAbsoluteFile();
    }
    
    public void removeUrlToDown(UrlDownData d){
        File urlToDownFile = null;
        try(DirectoryStream stream = Files.newDirectoryStream(MainFrame.urlsToDownFolder.toPath())){
            Iterator<Path> iterator = stream.iterator();
            boolean go = true;
            while(go){
                if(iterator.hasNext()){
                    File file = iterator.next().toFile().getAbsoluteFile();
                    UrlDownData fileUDD = Cacheable.convertFirst(UrlDownData.getScan(), Util.getText(file), serverTree);
                    if(fileUDD.compareTo(d) == 0){
                        go = false;
                        urlToDownFile = file;
                    }
                } else {
                    go = false;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Error! Failed to Delete UrlDownData " + d.getUrl());
            return;
        }
        
        if(urlToDownFile == null){
            System.out.println("Error! Failed to find the UrlDownData file for " + d.getUrl());
            return;
        }
        
        if(!urlToDownFile.isFile()){
            System.out.println("Warning! Deleting a UrlDownData that doesn't exist!\n - " + d.getUrl());
        }
        if(!Util.persistentDelete(urlToDownFile, 3)){
            System.out.println("Failed to delete UrlDownData " + d.getUrl());
        } else {
            //If urls to down have been deleted a certain multiple of times, reset the the code generator so it starts
            //at the lowest number possible
            numTimesDeletedUrlToDown++;
            if(numTimesDeletedUrlToDown % resetCodeGeneratorInterval == 0){
                resetUrlDownDataFileNameGenerator();
            }
        }
        numOfUrlsToDown--;
        MainFrame.main.doProgressBar();
    }
    
//    private HtmlTagAcceptor tribAcceptor = new HtmlTagAcceptor() {
//        @Override
//        public boolean accept(HtmlTag tag, String type) {
//            if(tag.getAttrib(type) != null){
//                return true;
//            }
//            return false;
//        }
//    };
//    
//    private HtmlTagAcceptor tagAcceptor = new HtmlTagAcceptor() {
//        @Override
//        public boolean accept(HtmlTag tag, String type) {
//            return tag.getTagName().toLowerCase().equals(type.toLowerCase());
//        }
//    };
    
    public boolean isAlive(){
        return runningThread.isAlive();
    }
    
    public void begin(){
        runningThread = new Thread(this);
        runningThread.start();
    }
    
    @Override
    public void run() {
        try {
            downloadFiles = true;
            contin = true;
            initDown(startUrl);
            while (downloadFiles) {
                if(!contin){
                    end();
                }
                downloadFiles = !g();
                if(!contin){
                    end();
                }
            }
            
            MainFrame.main.finishedServerDownload();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }    
    
    private void deleteUrlToDownFile(File f){
        if(!Util.persistentDelete(f, 3)){
            System.out.println("Failed to delete UrlDownData File - " + f);
        }
    }
    
    public void reset(){
        serverTree.backupAndClearTree();
        File[] urlToDownFiles = MainFrame.urlsToDownFolder.listFiles();
        if(urlToDownFiles.length != 0){
            System.out.println("Hmmm... this is strange. The server download is done, but there are still UrlDownDatas"
                    + " existing in the file system! Deleting them now!");
            for(File urlToDownFile : urlToDownFiles){
                deleteUrlToDownFile(urlToDownFile);
            }
        }
    }
    
    private boolean g() throws Exception {
        UrlDownData data = null;
        MainFrame.main.setCurrentProcess("Picking Url...", 10);
        boolean doit = true;
        while(doit){
            if(numOfUrlsToDown <= 0){
                downloadedServer = true;
                Util.showNorm("Server Download Complete!", "Complete!");
                MainFrame.main.setCurrentProcess("Finsished Downloading Server!", 100);
                downloadedServer = true;
                return true;
            }
            if(!contin){
                end();
            }
            File urlDownDataFile;
            try(DirectoryStream<Path> stream = Files.newDirectoryStream(MainFrame.urlsToDownFolder.toPath())){
                Iterator<Path> iterator = stream.iterator();
                if(iterator.hasNext()){
                    Path next = iterator.next();
                    urlDownDataFile = next.toFile().getAbsoluteFile();
                } else {
                    urlDownDataFile = null;
                }
            }
            if(urlDownDataFile == null){
                Util.showNorm("Error! NumOfUrlsToDown is more than 0, but there aren't any UrlDownData files!\n"
                        + "Report this to Developers Immediately!", "Error!").setVisible(true);
            } else {
                data = Cacheable.convertFirst(UrlDownData.getScan(), Util.getText(urlDownDataFile), serverTree);
                if(data == null){
                    System.out.println("Warning! UrlDownData file " + urlDownDataFile + " produced a null UrlDownData!");
                    Util.persistentDelete(urlDownDataFile, 3);
                } else if(Util.binaryHas(noDown, data.getUrl())){
                    removeUrlToDown(data);
                } else {
                    doit = false;
                }
            }
        }
        if(!contin){
            end();
        }
        MainFrame.main.addDetailedLog("Deciding Whether To Download " + data.getUrl(), MainFrame.PROCESS);
        MainFrame.main.setCurrentProcess("Deciding Whether To Download Url...", 30);
        DownloadResult downloadResult = decide(data);
        removeUrlToDown(data);
        ServerPartData download = downloadResult.getServerPartData();
        MainFrame.main.setCurrentProcess("Processing Download...", 65);
        
        int downloadResultCode = downloadResult.getResultCode();
        
        switch(downloadResultCode) {
            case DownloadResult.DOWNLOAD_CANCEL:
                MainFrame.main.addDetailedLog("Download Canceled!\n", MainFrame.NOTIFICATION);
                MainFrame.main.setCurrentProcess("Download Canceled!", 100);
                return false;
            case DownloadResult.DOWNLOAD_FAIL :
                failedUrlsPanel.addFailedUrl(data);
                MainFrame.main.addBasicLog("Download Failed!");
                MainFrame.main.addDetailedLog("Download Failed!\n", MainFrame.FAILURE);
                MainFrame.main.setCurrentProcess("Download Failed!", 100);
                addToNoDownAndToFailedUrls(data);
                return false;
            case DownloadResult.DOWNLOAD_START_CANCEL :
                MainFrame.main.addDetailedLog("Download Canceled!\n", MainFrame.NOTIFICATION);
                MainFrame.main.addBasicLog("Download Canceled!");
                return false;
            case DownloadResult.DOWNLOAD_SUCCESS :
                break;
            default : 
                return false;
        }
        
        File file = download.getFile();
        MainFrame.main.addBasicLog("Downloaded!");
        MainFrame.main.addDetailedLog("Downloaded!", MainFrame.GOOD);
        MainFrame.main.changeInNumOfDownloadedFiles(1);
        if(serverTree.isResource(download.getRef())){
            MainFrame.main.setCurrentProcess("No Need to Process Url!", 100);
            MainFrame.main.addDetailedLog("No Need To Process Url, Returning.\n", MainFrame.NOTIFICATION);
            return false;
        }
        if(!contin){
            end();
        }
        ServerPartData parentData = serverTree.nearestParentData(download);
        actOnFile(download, parentData);
        if(!contin){
            end();
        }
        MainFrame.main.setCurrentProcess("Finished Processing Url!", 100);
        MainFrame.main.addDetailedLog("Processed " + data.getUrl().toFile() + "\n", MainFrame.NOTIFICATION);
        return false;
    }
    
    private void addToNoDownAndToFailedUrls(UrlDownData data){
        int indexOf = Util.binaryIndexOf(noDown, data.getUrl());
        if(indexOf < 0){
            indexOf = -1 * (indexOf + 1);
            noDown.add(indexOf, data.getUrl());
        }
        failedUrlsPanel.addFailedUrl(data);
    }
    
    private void process(ArrayList<HtmlTag> tags, HtmlProcessor proc, String starter) throws Exception{
        for(int i = 0; i < tags.size(); i++){
            HtmlTag tag = tags.get(i);
            proc.process(tag, starter);
        }
    }
    
    private void actOnFile(ServerPartData fileData, ServerPartData parentData){
        File file = fileData.getFile();
        Ref ref = fileData.getRef();
        System.out.println("Acting on " + file);
        try {
            String name = file.getName().substring(0);
            if(ref.isHtml()){
                html(file, parentData);
                MainFrame.main.setCurrentProcess("Processed HTML!", 100);
            } else if(name.endsWith(".css")){
                css(file, parentData);
                MainFrame.main.setCurrentProcess("Processed CSS!", 100);
            } else {
                if(FileExtensionAnalyzer.isHtmlFile(file)){
                    MainFrame.main.addDetailedLog("File doesn't end with html but looks like html, so processing file.", MainFrame.NOTIFICATION);
                    html(file, parentData);
                    MainFrame.main.setCurrentProcess("Processed HTML!", 100);
                }
                MainFrame.main.setCurrentProcess("No Need to Process!", 100);
            }
        } catch (Exception e){
            e.printStackTrace();
            end();
        }
        System.out.println("Finished Acting On " + file);
    }
    
    public void html(File file, ServerPartData parentData) throws Exception {
        MainFrame.main.setCurrentProcess("Gathering HTML Elements", 70);
        HtmlElements htmlElements = HtmlElements.getHtmlElements(file);
        MainFrame.main.setCurrentProcess("Processing HTML Elements", 75);
        
        String url = parentData.getRef().toString();
        String starter = file.toString();
        
        ArrayList<ArrayList<HtmlTag>> tagsFoundByTags = htmlElements.getTagsOfType(Util.toArr("meta", "style"), true);
        
        htmlProcessTagsFoundByTags(tagsFoundByTags, url, parentData, starter);
        
        ArrayList<ArrayList<HtmlTag>> tagsFoundByTribs = htmlElements.getTagsOfType(Util.toArr("href", "src",
                "download", "data", "style"), false);
        htmlProcessTagsFoundByTribs(tagsFoundByTribs, url, parentData, starter);
    }
    
    private void htmlProcessTagsFoundByTribs(ArrayList<ArrayList<HtmlTag>> tagsFoundByTribs, String url, ServerPartData parentData, String starter) throws Exception{
        
        HtmlProcessor proc = new HtmlProcessor("href", this, url, parentData);
        ArrayList<HtmlTag> hrefTags = tagsFoundByTribs.get(0);
//        ArrayList<HtmlTag> hrefTags = htmlElements.getTagsOfType("href", tribAcceptor);
        process(hrefTags, proc, starter);
        
        proc = new HtmlProcessor("src", this, url, parentData);
        ArrayList<HtmlTag> srcTags = tagsFoundByTribs.get(1);
//        ArrayList<HtmlTag> srcTags = htmlElements.getTagsOfType("src", tribAcceptor);
        process(srcTags, proc, starter);
        
        proc = new HtmlProcessor("download", this, url, parentData);
        ArrayList<HtmlTag> downloadTags = tagsFoundByTribs.get(2);
//        ArrayList<HtmlTag> downloadTags = htmlElements.getTagsOfType("download", tribAcceptor);
        process(downloadTags, proc, starter);
        
        proc = new HtmlProcessor("data", this, url, parentData);
        ArrayList<HtmlTag> dataTags = tagsFoundByTribs.get(3);
//        ArrayList<HtmlTag> dataTags = htmlElements.getTagsOfType("data", tribAcceptor);
        process(dataTags, proc, starter);
        
        proc = new HtmlProcessor(new HtmlResponder() {
            @Override
            public void respond(HtmlTag tag, ServerPartData parentData) throws Exception{
                Attrib styleTrib = tag.getAttrib("style");
                if(styleTrib == null){
                    return;
                }
                String tribVal = styleTrib.getVal();
                if(!tribVal.equals("")){
                    css(tribVal, parentData, starter);
                }
            }
        }, this, url, parentData);
        
        ArrayList<HtmlTag> tagsWithStyles = tagsFoundByTribs.get(4);
//        ArrayList<HtmlTag> tagsWithStyles = htmlElements.getTagsOfType("style", tribAcceptor);
        process(tagsWithStyles, proc, starter);
    }
    
    private void htmlProcessTagsFoundByTags(ArrayList<ArrayList<HtmlTag>> tagsFoundByTags, String url, ServerPartData parentData, String starter) throws Exception{
        ArrayList<HtmlTag> metas = tagsFoundByTags.get(0);
//        ArrayList<HtmlTag> metas = htmlElements.getTagsOfType("meta", tagAcceptor);
        meta(metas, url, parentData, starter);
        
        ArrayList<HtmlTag> styles = tagsFoundByTags.get(1);
//        ArrayList<HtmlTag> styles = htmlElements.getTagsOfType("style", tagAcceptor);

        HtmlProcessor proc = new HtmlProcessor(new HtmlResponder() {
            @Override
            public void respond(HtmlTag tag, ServerPartData parentData) throws Exception{
                String inside = tag.getData().getInside();
                if(!inside.equals("")){
                    css(inside, parentData, starter);
                }
            }
        }, this, url, parentData);
        process(styles, proc, starter);
    }
    
    public void meta(ArrayList<HtmlTag> metas, String url, ServerPartData parentData, String starter) throws Exception{
        for(int i = 0; i < metas.size(); i++){
            HtmlTag meta = metas.get(i);
            Attrib trib = meta.getAttrib("content");
            if(trib != null){
                String val = trib.getVal() + "";
                boolean doit = true;
                while(doit){
                    int ind = val.indexOf("url");
                    if(ind == -1){
                        doit = false;
                    } else {
                        int sc = val.indexOf(";", ind+1);
                        int equals = val.indexOf("=", ind+1);
                        if(!(sc != -1 && equals > sc)){
                            String part;
                            if(sc != -1){
                                part = val.substring(equals+1, sc);
                            } else {
                                part = val.substring(equals+1);
                                val = "";
                                doit = false;
                            }
                            part = Util.strip(part);
                            Ref ref = new Ref(new Ref(url), part);
                            if(!Ref.isMailto(ref)){
                                addUrlToDown(new UrlDownData(parentData, ref, starter, meta.toString(), ""));
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void css(File file, ServerPartData parentData) throws Exception{
        css(Util.getText(file), parentData, file.toString());
    }
    
    public void css(String tmpfileText, ServerPartData parentData, String starter) throws Exception {
        String fileText = tmpfileText + "";
        MainFrame.main.setCurrentProcess("Processing CSS...", 75);
        System.out.println("Processing CSS " + starter);
        String url = parentData.getRef().toString();
        boolean go = true;
        
        String lookingFor = "url(";
        int nextIndex = fileText.indexOf(lookingFor, 0);
        while(go){
            if(nextIndex == -1){
                go = false;
            } else {
                int index = fileText.indexOf(lookingFor, nextIndex + 1);
                if(index == -1){
                    go = false;
                    index = fileText.length();
                }
                String workingWith = fileText.substring(nextIndex + 1, index);
                nextIndex = index;
                if(CssUrl.isUrl(workingWith)){
                    CssUrl cu = new CssUrl(workingWith, url);
                    Ref[] refs = cu.getUrls();
                    for(int i = 0; i < refs.length; i++){
                        addUrlToDown(new UrlDownData(parentData, refs[i], starter, "CSS PART: " + workingWith, ""));
                    }
                }
            }
        }
    }
    
    private DownloadResult decide(UrlDownData data) throws Exception {
        Ref ref = data.getUrl();
        if(suddenBackup){
            save(false);
            suddenBackup = false;
        }
        if(!contin){
            end();
        }
        if(serverTree.exists(ref)){
            MainFrame.main.addDetailedLog(ref + " is already downloaded! Returning.", MainFrame.NOTIFICATION);
            return new DownloadResult(null, "Download Canceled", DownloadResult.DOWNLOAD_CANCEL);
        }
        if(!contin){
            end();
        }
        if(!downloader.shouldDownload(ref)){
            MainFrame.main.addDetailedLog(ref + " is an HTML resource and shouldn't be downloaded.", MainFrame.WARNING);
            return new DownloadResult(null, "Download Canceled", DownloadResult.DOWNLOAD_CANCEL);
        }
        if(!contin){
            end();
        }
        downloadCount++;
        int tempBackupPerDownloadsNum = backupPerDownloadsNum;
        if((tempBackupPerDownloadsNum != 0 && downloadCount % tempBackupPerDownloadsNum == 0 && downloadCount != 0 ) || suddenBackup){
            MainFrame.main.setCurrentProcess("Backing up...", 30);
            save(false);
            MainFrame.main.setCurrentProcess("Deciding Whether To Download Url...", 30);
            suddenBackup = false;
        }
        
        MainFrame.main.addBasicLog("Downloading " + ref + ".");
        MainFrame.main.addDetailedLog("Downloading " + ref + ".", MainFrame.PROCESS);
        MainFrame.main.setCurrentProcess("Downloading url...", 30);
        DownloadResult download = downloader.download(ref);
        data.setDownloadResult(download.getDownloadMessage());
        return download;
    }
    
    public void save(boolean block){        
        Cacheable.cache(failedUrlsPanel.getModel().getData(), MainFrame.downFailFile);
    }
    
    public void end() {
        new Exiter().begin();
        MainFrame.main.ending();
    }
    
    public void stopDownloading(){
        contin = false;
    }
    
    private class Exiter implements Runnable{
        
        public void begin(){
            new Thread(this).start();
        }

        @Override
        public void run() {
            MainFrame.main.setCurrentProcess("Saving Configurations...", 90);
            save(true);
            System.exit(0);
        }
        
    }
}