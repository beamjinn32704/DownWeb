
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
public class MemoryTracker {
    private long memory = 0;
    private String strToRemember = "";
    private static File logFile = new File("memoryLogFile.txt").getAbsoluteFile();
    
    public MemoryTracker() {
        
    }
    
    public void start(){
        start("");
    }
    
    public void start(String str){
        strToRemember = str + "";
        String started;
        if(Util.isBlank(str)){
            started = "Started";
            strToRemember = "";
        } else {
            started = "Started ";
        }
        String logged = started + strToRemember + "! Counting Memory.";
        System.out.println(logged);
//        Util.writeToFile(logFile, "\n" + logged, true);
        memory = getUsedMem();
    }
    
    public void end(){
        String used = Util.formatBytes(getUsedMem());
//        String used = Util.formatBytes(getUsedMem() - memory);
        String finished;
        if(Util.isBlank(strToRemember)){
            finished = "Finished";
        } else {
            finished = "Finished ";
        }
        String logged = finished + strToRemember + "! Memory used is " + used;
        System.out.println(logged);
        Util.writeToFile(logFile, "\n" + logged, true);
    }
    
    public static long getUsedMem(){
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }
    
    public static void publishAndOpenLog(){
        Util.open(logFile);
    }
}
