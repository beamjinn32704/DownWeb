package com.mindblown.webdown;

public class Watch {
    private long mili;
    
    public Watch() {
        
    }
    
    public void start(String word){
        System.out.println("Starting " + word + "!");
        start();
    }
    
    public void start(){
        mili = System.currentTimeMillis();
    }
    
    public void stop(String word){
        System.out.println("Finished " + word + " in " + (System.currentTimeMillis() - mili) + " miliseconds");
    }
}