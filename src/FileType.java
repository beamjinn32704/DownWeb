
import java.util.ArrayList;
import java.util.Collections;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author beamj
 */
public class FileType implements Comparable<FileType>{
    
    public static final FileType JPG;
    public static final FileType PNG;
    public static final FileType BITMAP;
    public static final FileType GIF;
    public static final FileType TIFF;
    public static final FileType ICO;
    public static final FileType IMAGES;
    
    public static final FileType MP3;
    public static final FileType M4A;
    public static final FileType FLAC;
    public static final FileType AAC;
    public static final FileType WMA;
    public static final FileType WAV;
    public static final FileType REAL_AUDIO;
    public static final FileType MIDI;
    public static final FileType OGG;
    public static final FileType AUDIO;
    
    public static final FileType MP4;
    public static final FileType MOV;
    public static final FileType WMV;
    public static final FileType FLV;
    public static final FileType AVI;
    public static final FileType AVCHD;
    public static final FileType WEBM;
    public static final FileType MKV;
    public static final FileType VIDEO;
    
    public static final FileType PDF;
    public static final FileType DOC;
    public static final FileType DOCX;
    public static final FileType ODT;
    public static final FileType RTF;
    public static final FileType TXT;
    public static final FileType DOCUMENTS;
    
    public static final FileType AZW;
    public static final FileType AZW3;
    public static final FileType AZW4;
    public static final FileType EPUB;
    public static final FileType MOBI;
    public static final FileType PDB;
    public static final FileType LRF;
    public static final FileType KFX;
    public static final FileType IBOOKS;
    public static final FileType EBOOKS;
    
    static {
        //Initalize the Hard-Coded FileTypes
        JPG = new FileType("JPEG", "JPG", "JPEG", "JPE", "JFIF");
//        JPG = new FileType("JPEG", ".jpg", ".jpeg", ".jpe", ".jfif");
        PNG = sameNameExt("PNG");
        BITMAP = new FileType("Bitmap", ".bmp");
        GIF = sameNameExt("GIF");
        TIFF = new FileType("TIFF", ".tiff", ".tif");
        ICO = sameNameExt("ICO");
        IMAGES = new FileType("Images", JPG, PNG, BITMAP, GIF, TIFF, ICO);
        
        MP3 = sameNameExt("MP3");
        M4A = sameNameExt("M4A");
        FLAC = sameNameExt("FLAC");
        AAC = sameNameExt("AAC");
        WMA = sameNameExt("WMA");
        WAV = sameNameExt("WAV");
        REAL_AUDIO = new FileType("Real Audio", ".ra", ".ram", ".rm");
        MIDI = new FileType("MIDI", ".mid");
        OGG = sameNameExt("OGG");
        AUDIO = new FileType("Audio", MP3, M4A, FLAC, AAC, WMA, WAV, REAL_AUDIO, MIDI, OGG);
        
        MP4 = sameNameExt("MP4");
        MOV = sameNameExt("MOV");
        WMV = sameNameExt("WMV");
        FLV = sameNameExt("FLV");
        AVI = sameNameExt("AVI");
        AVCHD = sameNameExt("AVCHD");
        WEBM = sameNameExt("WEBM");
        MKV = sameNameExt("MKV");
        VIDEO = new FileType("Videos", MP4, MOV, WMV, FLV, AVI, AVCHD, WEBM, MKV);
        
        PDF = sameNameExt("PDF");
        DOC = sameNameExt("DOC");
        DOCX = sameNameExt("DOCX");
        ODT = sameNameExt("ODT");
        RTF = sameNameExt("RTF");
        TXT = sameNameExt("TXT");
        DOCUMENTS = new FileType("Documents", PDF, DOC, DOCX, ODT, RTF, TXT);
        
        AZW = sameNameExt("AZW");
        AZW3 = sameNameExt("AZW3");
        AZW4 = sameNameExt("AZW4");
        EPUB = sameNameExt("EPUB");
        MOBI = sameNameExt("MOBI");
        PDB = sameNameExt("PDB");
        LRF = sameNameExt("LRF");
        KFX = sameNameExt("KFX");
        IBOOKS = sameNameExt("IBOOKS");
        EBOOKS = new FileType("Ebooks", AZW, AZW3, AZW4, EPUB, MOBI, PDB, 
                LRF, KFX, IBOOKS, PDF, RTF, TXT);
    }
    
    private ArrayList<String> fileExtensions = new ArrayList<>();
    private String name;
    
    /**
     * This function returns a file type that has the name name and has one file extension 
     * that is the name in an extension format. It turns the name into an extension format by 
     * turning it to lowercase and adding a period (".") to the front if it isn't there already. 
     * This would turn a name "PNG" into ".png"
     * @param name the name of the file type and the name of the extension
     * @return a new file type based off of the name
     */
    public static FileType sameNameExt(String name){
        FileType ft = new FileType(name, toFileExtension(name));
        return ft;
    }
    
    /**
     * This function returns a file extension formatted in the extension format. It turns the name into an extension format by 
     * turning it to lowercase and adding a period (".") to the front if it isn't there already. 
     * This would turn a fileExt "PNG" into ".png"
     * @param fileExt
     * @return 
     */
    private static String toFileExtension(String fileExt){
        //Set the file extension to be the file type name in an extension format
        //Basically if name is MP4, the extension would be .mp4
        String fileExtension = fileExt.toLowerCase();
        if(!fileExtension.startsWith(".")){
            fileExtension = '.' + fileExtension;
        }
        return fileExtension;
    }
    
    
    public FileType(String name, String[] fileExtensionArray) {
        this.name = name;
        fileExtensions = Util.toList(fileExtensionArray);
        finalizeFileExtensions();
    }
    
    public FileType(String name, ArrayList<String> fileExtensions){
        this.name = name;
        this.fileExtensions = fileExtensions;
        finalizeFileExtensions();
    }
    
    public FileType(String name, FileType fileType, FileType... fileTypes){
        this.name = name;
        fileExtensions.addAll(fileType.fileExtensions);
        for(int i = 0; i < fileTypes.length; i++){
            fileExtensions.addAll(fileTypes[i].fileExtensions);
        }
        finalizeFileExtensions();
    }
    
    public FileType(String name, String fileExtension){
        this.name = name;
        fileExtensions.add(fileExtension);
        formatFileExtensions();
    }
    
    public FileType(String name, String fileExtension1, String... fileExtensionList){
        this.name = name;
        fileExtensions = Util.toList(fileExtensionList);
        fileExtensions.add(fileExtension1);
        finalizeFileExtensions();
    }
    
    /**
     * Sorts the list of file extensions.
     */
    private void sortFileExtensions(){
        Collections.sort(fileExtensions);
    }
    
    /**
     * Formats the list of file extensions so that they are in the 
     * file extension format (.extension-name-lowercase).
     */
    private void formatFileExtensions(){
        //Go through all the file extensions and set them to the file extension format.
        for(int i = 0; i < fileExtensions.size(); i++){
            fileExtensions.set(i, toFileExtension(fileExtensions.get(i)));
        }
    }
    
    /**
     * This sorts the file extensions and formats them to the file extension format.
     */
    private void finalizeFileExtensions(){
        sortFileExtensions();
        formatFileExtensions();
    }
    
    /**
     * Gets the file extension at the index parameter.
     * @param index location of file extension
     * @return file extension at index
     */
    public String getFileExtension(int index){
        return fileExtensions.get(index);
    }
    
    /**
     * Adds the file extension if it hasn't already been added.
     * @param fileExt the file extension to add
     */
    public void addFileExtension(String fileExt){
        String fileExtension = toFileExtension(fileExt);
        int extInd = Util.binaryIndexOf(fileExtensions, fileExtension);
        if(extInd >= 0){
            return;
        } else {
            extInd = Util.translateIndex(extInd);
        }
        fileExtensions.add(extInd, fileExtension);
    }
    
    /**
     * Returns whether fileExtension is included in the FileType object.
     * @param fileExtension the file extension to check
     * @return whether fileExtension is in the FileType object
     */
    public boolean doesFileExtensionExist(String fileExtension){
        return Util.binaryHas(fileExtensions, fileExtension);
    }
    
    /**
     * Returns whether the list of file extensions is empty.
     * @return whether the list of file extensions is empty
     */
    public boolean isFileExtensionListEmpty(){
        return fileExtensions.isEmpty();
    }
    
    
    public void setFileTypeName(String name) {
        this.name = name;
    }
    
    public void setFileExtension(int index, String fileExtension){
        fileExtensions.set(index, fileExtension);
    }
    
    /**
     * Get the name of the file type.
     * @return name of the file type
     */
    public String getFileTypeName() {
        return name;
    }

    @Override
    public int compareTo(FileType o) {
        return fileExtensions.toString().compareTo(o.fileExtensions.toString());
    }

    @Override
    public String toString() {
        return name;
    }
}