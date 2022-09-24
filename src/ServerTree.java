
import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import javax.swing.filechooser.FileFilter;


/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

/**
 *
 * @author beamj
 */
public class ServerTree {
    
    private final Ref serverRef;
    
    private MultiModel nrhModel;
    
    public static String backupServerFileExtension = "dwnsvrfile";
    
    public static final int FILE_ITERATOR_MAX_COUNT = 5000;
    
//    private static final String badFileChars = "\\/:*?\"<>|";
//    private static final String badUrlChars = "[]{}|\\\"%~#<>";
    
    
    public static class UrlToFileConverter {
        private static final String switchBadFileChars = ":*?";
        private static final String switchBadUrlChars = "]{}";
        
        private static final String doubleSlashReplacement = "~";
        
        public static final char badNamePrefix = '#';
        private static final String[] badFileNames = new String[]{
            "aux", "com1", "com2", "com3", "con", "lpt1", "lpt2", "lpt3", "nul", "prn"
        };
        
        /**
         * Converts the file path (as a string) given to a url path.
         * @param file the file path
         * @return file converted to a url path
         */
        public static String convertToUrl(String file){
            //Split the file into its directories and file
            String[] urlPaths = Util.split(file, File.separatorChar + "");
            String url = "";
            //Go through the files and folders, correct any bad names, and add them back to file
            for(int i = 0; i < urlPaths.length; i++){
                String add = urlPaths[i];
                String addEnd;
                //If the file/folder starts with the bad-name indicator and the rest of the word is
                //a bad name, then get rid of the indicator
                if(add.startsWith(badNamePrefix + "") && badName((addEnd = add.substring(1)))){
                    add = addEnd;
                }
                url += add;
                //Add a seperator if not at the end so that the next file/folder is in a seperate folder
                //This basically adds Util.fullyReplace(url, File.seperatorChar, "/")
                //It splits file paths based on seperator chars and adds slashes instead
                if(i != urlPaths.length - 1){
                    url += "/";
                }
            }
            
            //Change all file seperator chars to url seperator chars (/)
            url = Util.fullyReplace(url, File.separatorChar + "", "/");
            
            //Get all the bad file characters and switch them with their corresponding url characters
            for(int i = 0; i < switchBadFileChars.length(); i++){
                url = url.replace(switchBadUrlChars.charAt(i), switchBadFileChars.charAt(i));
            }
            
            //Get rid of all double slash replacements since their only purpose is to put things in
            //between double slashes, not replace them.
            url = Util.fullyReplace(url, doubleSlashReplacement, "");
            
            return url;
        }
        
        /**
         * Converts the url path (as a string) given to a file path.
         * @param url the url path
         * @return url converted to a file path
         */
        public static String convertToFile(String url){
            String file = url + "";
            //Put double slash replacements in between the double slashes
            file = Util.fullyReplace(file, "//", "/" + doubleSlashReplacement + "/");
            
            //Switch bad file characters with bad url characters
            for(int i = 0; i < switchBadUrlChars.length(); i++){
                file = file.replace(switchBadFileChars.charAt(i), switchBadUrlChars.charAt(i));
            }
            
            //Split the file into its directories and file
            String[] filePaths = file.split("/");
            file = "";
            //Go through the files and folders, correct any bad names, and add them back to file
            for(int i = 0; i < filePaths.length; i++){
                String add = filePaths[i];
                add = goodName(add);
                file += add;
                //Add a seperator if not at the end so that the next file/folder is in a seperate folder
                //This basically adds Util.fullyReplace(file, "/", and File.seperatorChar)
                //It splits file paths based on slashes and adds seperator chars instead
                if(i != filePaths.length - 1){
                    file += File.separatorChar;
                }
            }
            return file;
        }
        
        public static String goodName(String n){
            String name;
            //Seperating the name of the file from its file extension
            int i = n.indexOf(".");
            String extension = "";
            if(i != -1){
                name = n.substring(0, i);
                extension = n.substring(i);
            } else {
                name = n + "";
            }
            if(badName(name)){
                name = badNamePrefix + name;
            }
            //Put the file extension back
            return name + extension;
        }
        
        public static boolean badName(String name){
            if(name.length() == 2 && Character.isUpperCase(name.charAt(0)) && name.charAt(1) == ':'){
                return true;
            }
            if(Arrays.binarySearch(badFileNames, name) >= 0 ){
                return true;
            }
            return false;
        }
    }
    
    public static FileFilter filesFilter = new FileFilter() {
        @Override
        public boolean accept(File f) {
            return !f.toString().endsWith("." + ServerTree.backupServerFileExtension);
        }
        
        @Override
        public String getDescription() {
            return "";
        }
    };
    
    /**
     * Instantiate a new ServerTree object.
     * @param r the server ref which the server tree is based on.
     * @param nrhModel a model which contains the non-resource host refs.
     */
    public ServerTree(Ref r, MultiModel nrhModel) {
        this.serverRef = r;
        this.nrhModel = nrhModel;
    }
    
    public ServerPartData getServerTreeData(){
        return makeData(serverRef);
    }
    
    public boolean exists(Ref ref){
        return exists(ref, ref);
    }
    
    public boolean exists(Ref ref, Ref orig){
        File file = makeData(ref).getFile();
        if(ref.getPath().endsWith("/") || Util.isBlank(ref.getPath())){
            file = new File(file, "index.html");
        } else {
            if(file.getName().contains(".")){
                
            } else {
                file = new File(file.getParentFile(), file.getName() + ".html");
            }
        }
        
        return file.exists();
    }
    
    /**
     * Used to identify if a ref given to makeData(File file) is a resource.
     * @param ref The ref
     * @return whether the ref is a resource.
     */
    public boolean isResource(Ref ref){
        //If it's not the same host as the server ref and isn't in the non-resource-hosts list
        boolean resourceHost = !ref.sameHost(serverRef) && !nrhModel.hasElement(ref.getHostRef());
        return resourceHost;
    }
    
    /**
     * Makes a ServerPartData out of a file that belongs to the server tree.
     * This function is used for providing the string that describes the web location of the file.
     * If file was something like C:\Servers\bing.com\about\connect.html, this would return a string
     * like "bing.com/about/connect.html"
     * @param file the file
     * @return a ServerPartData
     */
    public ServerPartData makeData(File file){
        ServerPartData data = md(file);
        if(!fileDataMadeRight(data, file)){
            Util.showNorm("File\n" + file + " \n's data wasn't made correctly", "MAKE DATA ERROR");
        }
        return data;
    }
    
    /**
     * This is a private helper function that contains the flesh code for makeData(file). This contains all the
     * actual code, but is used so that makeData(file) can check if the data was made
     * correctly. If I included the code in makeData(), it would result in an infinite loop.
     * @param file the file to make data from
     * @return the server part data made from the file
     */
    private ServerPartData md(File file){
        String string;
        //This part below essentially gets rid of the file system part of the file
        //and narrows it down to the file path that is directly related to the url.
        String serverContainerHeadFileLoc = MainFrame.serverContainerHeadFile.toString();
        int serverContainerHeadFileLength = serverContainerHeadFileLoc.length();
        if(!serverContainerHeadFileLoc.endsWith("/")){
            //If the server container head file string doesn't end with a slash, then add one
            //so that the slash is deleting in the next lines
            serverContainerHeadFileLength++;
        }
        //This process is essentially removing the server host and path from the server container head file.
        //It would take something like "C:\Servers\bing.com\about.html" and remove the "C:\Servers\" part.
        string = file.toString().substring(serverContainerHeadFileLength);
        string = UrlToFileConverter.convertToUrl(string);
        
        //Directories mean that they should end with a slash.
        if(file.isDirectory() && !string.endsWith("/")){
            string += "/";
        }
        
        Ref ref = new Ref(string);
        return new ServerPartData(file, ref);
    }
    
    public ServerPartData makeData(Ref ref){
        ServerPartData data = md(ref);
        if(data == null){
            return data;
        }
        if(!refDataMadeRight(data, ref)){
            Util.showNorm("Ref\n" + ref + " \n's data wasn't made correctly", "MAKE DATA ERROR");
        }
        return data;
    }
    
    /**
     * This is a private helper function that contains the flesh code for makeData(ref). This contains all the
     * actual code, but is used so that makeData(ref) can check if the data was made
     * correctly. If I included the code in makeData(), it would result in an infinite loop.
     * @param ref the ref to make data from
     * @return the server part data made from the ref
     */
    private ServerPartData md(Ref ref){
        if(!ref.isValidUrl()){
            return null;
        }
        //Get the ref in file version (ref.getHost + ref.getPath)
        String refFile = ref.toFile();
        //Replace all forward slashes with back slashes since ref.toFile() replaces
        //back slashes with forward slashes
        
        //The tricky part of this is is that some refs(though rare) have double slashes which
        //correspond to actual urls. Due to this, I need to find every double slash and put in between
        //each double slash a directory name that is reserved for double slashes. This will be gotten from UrlToFile converter
        refFile = UrlToFileConverter.convertToFile(refFile);
        File file = new File(MainFrame.serverContainerHeadFile, refFile);
        file = file.getAbsoluteFile();
        return new ServerPartData(file, ref);
    }
    
    /**
     * This function checks to make sure if the data's file (that was presumably made from a ref)
     * was created correctly. This function does that by comparing the original file with the file from
     * the data made by data's ref. Basically it makes sure that makeData() can be used to change data's ref
     * to data's file and vice versa.
     * @param data the data to check.
     * @param orig the original file.
     * @return whether the file's data was made right
     */
    private boolean fileDataMadeRight(ServerPartData data, File orig){
        return md(data.getRef()).getFile().compareTo(orig) == 0;
    }
    
    /**
     * This function checks to make sure if the data's ref (that was presumably made from a file)
     * was created correctly. This function does that by comparing the original ref with the ref from
     * the data made by data's file. Basically it makes sure that makeData() can be used to change data's ref
     * to data's file and vice versa.
     * @param data the data to check.
     * @param orig the original ref.
     * @return whether the ref's data was made right
     */
    private boolean refDataMadeRight(ServerPartData data, Ref orig){
        return md(data.getFile()).getRef().compareTo(orig) == 0;
    }
    
    public ServerPartData nearestParentData(ServerPartData data){
        File dataFile = data.getFile();
        assert dataFile.exists();
        if(dataFile.isDirectory()){
            return data;
        } else {
            File parentFile = dataFile.getParentFile().getAbsoluteFile();
            return makeData(parentFile);
        }
    }
    
    public Ref getServerRef() {
        return serverRef;
    }
    
    public int getTotalNumOfLeaves(){
        //Return the numbers of leaves in the whole server download (including the main server and the other servers)
        getServerTreeData().getFile();
        int totalNum = 0;
        try(DirectoryStream stream = Files.newDirectoryStream(MainFrame.serverContainerHeadFile.toPath())){
            Iterator<Path> iterator = stream.iterator();
            while(iterator.hasNext()){
                File downloadedServerHolder = iterator.next().toFile();
                totalNum += getNumOfLeaves(downloadedServerHolder);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return totalNum;
    }
    
    public int getNumOfLeaves(File downloadedServerHolder){
        return Util.getFilesOfType(downloadedServerHolder, true, filesFilter).size();
    }
    
    public void backupAndClearTree(){
        /*
        1. While the filesGotten isn't empty (it becomes empty when all files are listed)
        2. For each file, rename each file to have the backup server file extension
        */
        boolean go = true;
        FileIterator iterator = genFileIterator();
        while(go){
            //#1
            ArrayList<File> filesGotten = iterator.nextFiles();
            if(filesGotten.isEmpty()){
                go = false;
            } else {
                //#2
                for(File file : filesGotten){
                    if(!Util.rename(file, file.getName() + "." + backupServerFileExtension)){
                        System.out.println("Error in renaming " + file + "!");
                    }
                }
            }
        }
    }
    
    public FileIterator genFileIterator(){
        //Return a new FileIterator using the set max count, and the server tree's folder
        return new FileIterator(FILE_ITERATOR_MAX_COUNT, getServerTreeData().getFile());
    }
}