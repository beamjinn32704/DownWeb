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
public class HtmlBranch extends Branch<HtmlTag>{
    
    public HtmlBranch(String name, Branch p, HtmlTag obj) {
        super(name, p, obj);
    }
    
    public ArrayList<HtmlTag> getTagsOfType(String type, HtmlTagAcceptor acceptor){
        ArrayList<HtmlTag> tagsOfType = new ArrayList<>();
        HtmlTag thisObj = getObj();
        if(thisObj != null){
            if(acceptor.accept(thisObj, type)){
                tagsOfType.add(thisObj);
            }
        }
        ArrayList<TreePart<HtmlTag>> thisParts = getParts();
        if(thisParts != null && !thisParts.isEmpty()){
            for(TreePart<HtmlTag> part : thisParts){
                if(part instanceof HtmlBranch){
                    ArrayList<HtmlTag> got = ((HtmlBranch)part).getTagsOfType(type, acceptor);
                    tagsOfType.addAll(got);
                } else {
                    if(acceptor.accept(part.getObj(), type)){
                        tagsOfType.add(part.getObj());
                    }
                }
            }
        }
        return tagsOfType;
    }
    
}
