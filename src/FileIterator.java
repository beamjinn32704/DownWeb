
import java.io.File;
import java.util.ArrayList;
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
public class FileIterator {
    private FileCounter fileCounter;
    //This filter will be used to collect the files that will be backupped.
    private FileFilter filter;
    
    private File dirToGoThrough;
    
    public FileIterator(int iterateNum, File dirToGoThrough) {
        fileCounter = new FileCounter(iterateNum);
        this.dirToGoThrough = dirToGoThrough;
        filter = new FileFilter() {
            /*
            1. Check if Counter is Maxed
            2. Check if the file is a Server File and isn't a folder
            3. Check if the file is alphabetically ahead of the last file. (since the files will be in alphabetical order)
            4. Add Counter by 1, and set lastFileDone to the file.
            */
            @Override
            public boolean accept(File f) {
                //#1
                if(fileCounter.maxReached()){
                    return false;
                }
                String fileAbsPath = f.getAbsolutePath();
                //#2
                if(ServerTree.filesFilter.accept(f)){
                    //#3
                    if(fileAbsPath.compareTo(fileCounter.lastFileDone) > 0){
                        //#4
                        fileCounter.addOne();
                        fileCounter.setLastFileDone(fileAbsPath);
                        return true;
                    }
                    return false;
                }
                return false;
            }
            
            @Override
            public String getDescription() {
                return "";
            }
        };
    }
    
    public ArrayList<File> nextFiles(){
        ArrayList<File> files = Util.getFilesOfType(dirToGoThrough, true, filter);
        fileCounter.reset();
        return files;
    }
    
    private class FileCounter {
        private int maxPerRound;
        private int iterated = 0;
        private String lastFileDone = "";
        
        public FileCounter(int maxPerRound) {
            this.maxPerRound = maxPerRound;
        }
        
        public boolean maxReached(){
            return iterated >= maxPerRound;
        }
        
        public void addOne(){
            iterated++;
        }
        
        public void reset(){
            iterated = 0;
        }
        
        public void setLastFileDone(String lastFileDoneTemp){
            lastFileDone = lastFileDoneTemp;
        }
    }
}