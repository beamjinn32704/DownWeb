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
public class UrlDownData implements Comparable<UrlDownData>, Cacheable<UrlDownData>{
    private ServerPartData parentData;
    private Ref url;
    private String refFile;
    private String tag;
    private String downloadResult;
    
    public UrlDownData(ServerPartData parentData, Ref url, String refFile, String tag, String downloadResult) {
        this.parentData = parentData;
        this.url = url;
        this.refFile = refFile;
        this.tag = tag;
        this.downloadResult = removeLines(downloadResult);
    }
    
    private String removeLines(String str){
        return str.replace("\n", " ");
    }

    public void setDownloadResult(String downloadResult) {
        this.downloadResult = removeLines(downloadResult);
    }

    public void setRefFile(String refFile) {
        this.refFile = refFile;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setUrl(Ref url) {
        this.url = url;
    }
    
    public String getTag() {
        return tag;
    }
    
    public ServerPartData getParentData() {
        return parentData;
    }
    
    public Ref getUrl() {
        return url;
    }
    
    public String getRefFile() {
        return refFile;
    }

    public String getDownloadResult() {
        return downloadResult;
    }
    
    @Override
    public int compareTo(UrlDownData o) {
        return url.compareTo(o.url);
    }
    
    @Override
    public String toString() {
        return url.toString();
    }
    
    @Override
    public String cache() {
        String str = cacheStart + parentData.getRef() + "\n" + url.toString() + "\n" + refFile + "\n " + downloadResult + "\n" + tag + cacheEnd;
        return str;
    }
    
    private static final String cacheStart = "\\*\\%\\SPECCODE-WORLDPEACE_START";
    private static final String cacheEnd = "\\*\\%\\SPECCODE-WORLDPEACE_END";
    
    public static CacheScanner<UrlDownData> getScan(){
        return new CacheScanner<UrlDownData>() {
            
            private String text;
            private ServerTree serverTree;
            
            @Override
            public UrlDownData next() {
                int start = text.indexOf(cacheStart);
                if(start == -1){
                    return null;
                }
                int end = text.indexOf(cacheEnd, start + cacheStart.length());
                if(end == -1){
                    return null;
                }
                String code = text.substring(start + cacheStart.length(), end);
                String[] parts = Util.split(code, "\n");
                if(parts.length < 5){
                    return null;
                }
                String p = parts[0];
                String u = parts[1];
                String refFile = parts[2];
                String downResult = parts[3];
                String tag = parts[4];
                Ref url = new Ref(u);
                ServerPartData parentData = serverTree.makeData(new Ref(p));
                text = text.substring(end + 1);
                return new UrlDownData(parentData, url, refFile, tag, downResult);
            }
            
            @Override
            public void analyze() {
                
            }
            
            @Override
            public ArrayList<UrlDownData> scanned() {
                return null;
            }
            
            @Override
            public void setParams(Object[] p) {
                text = (String)p[0];
                serverTree = (ServerTree)p[1];
            }
            
        };
    }
}