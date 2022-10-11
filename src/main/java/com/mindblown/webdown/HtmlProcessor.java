package com.mindblown.webdown;

/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

/**
 *
 * @author beamj
 */
public class HtmlProcessor {
    private boolean lookAtTribVal;
    private String tribType;
    private FileAction action;
    private String url;
    private ServerPartData parentData;
    private HtmlResponder responder;
    
    public HtmlProcessor(String type, FileAction fa, String u, ServerPartData branchData){
        lookAtTribVal = true;
        tribType = type;
        action = fa;
        url = u;
        parentData = branchData;
    }
    
    public HtmlProcessor(HtmlResponder res, FileAction fa, String u, ServerPartData branchData){
        lookAtTribVal = false;
        action = fa;
        url = u;
        parentData = branchData;
        responder = res;
    }
    
    public void process(HtmlTag tag, String starter) throws Exception{
        if(lookAtTribVal){
            Attrib trib = tag.getAttrib(tribType);
            if(trib != null){
                Ref ref = new Ref(new Ref(url), trib.getVal());
                if(!Ref.isMailto(ref)){
                    action.addUrlToDown(new UrlDownData(parentData, ref, starter, tag.toString(), ""));
                }
            }
        } else {
            String inside = tag.getData().getInside();
            if(!inside.equals("")){
                responder.respond(tag, parentData);
            }
        }
    }
}
