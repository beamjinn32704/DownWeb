/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author beamj
 */
public class ByteCount {
    private long bytes = 0;
    
    private long totalBytes = 0;

    public long getBytes() {
        return bytes;
    }
    
    public void reset(){
        bytes = 0;
    }
    
    public void add(long add){
        bytes += add;
        totalBytes += add;
    }

    public long getTotalBytes() {
        return totalBytes;
    }
}
