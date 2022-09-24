
import java.io.File;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author beamj
 */
public class UrlResponse {
    
    private final File file;
    private final boolean worked;
    
    public UrlResponse(File f, boolean b) {
        file = f;
        worked = b;
    }
    
    public File getFile(){
        return file;
    }
    
    public boolean getWorked(){
        return worked;
    }
    
    @Override
    public String toString(){
        if(worked){
            return "URL Succesfully Retrieved! Data can be found at " + file;
        } else {
            return "URL Retrieval Failed!";
        }
    }
}
