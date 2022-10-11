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
public class CssUrl {
    
    private Ref[] urls;
    
    public CssUrl(String line, String head) {
        System.out.println(line);
        Object[] u = Util.toArray(extractUrl(line, head));
        urls = new Ref[u.length];
        for(int i = 0; i < u.length; i++){
            urls[i] = (Ref)u[i];
        }
    }
    
    public Ref[] getUrls() {
        return urls;
    }
    
    private static ArrayList<Ref> extractUrl(String l, String head){
        ArrayList<Ref> refs = new ArrayList<>();
        String line = l + "";
        while(isUrl(line)){
            int startIndex = line.indexOf("url(");
            String lookingAt = line.substring(startIndex);
            int endIndex = lookingAt.indexOf(')');
            String workingWith = lookingAt.substring(0, endIndex);
            //REVAMP THE WHOLE SYSTEM
            /*
            INSTEAD OF LOOKING THORUGH SPACES IN FILEACTION,
            LOOK FOR URL(.
            ISURL RETURNS TRUE IF THERE'S A PARANTHESES BEFORE THE NEXT SPACE AFTER URL(.
            */
            
            if(workingWith.endsWith("'") || workingWith.endsWith("\"")){
                workingWith = workingWith.substring(0, workingWith.length() - 1);
            }
            if(workingWith.startsWith("'") || workingWith.startsWith("\"")){
                workingWith = workingWith.substring(1);
            }
            Ref ref = new Ref(new Ref(head), workingWith);
            if(!Ref.isMailto(ref)){
                refs.add(ref);
            }
            line = line.substring(startIndex + endIndex);
        }
        return refs;
    }
    
    public static boolean isUrl(String url){
        int start = url.indexOf("url(");
        if(start == -1){
            return false;
        }
        
        String lookLast;
        int firstSpace = url.indexOf(' ');
        if(firstSpace == -1){
            lookLast = url + "";
        } else {
            lookLast = url.substring(0, firstSpace);
        }
        if(lookLast.lastIndexOf(")") > start ){
            return true;
        }
        return false;
    }
    
}
