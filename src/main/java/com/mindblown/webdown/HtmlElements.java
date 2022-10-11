package com.mindblown.webdown;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

/**
 *
 * @author beamj
 */
public class HtmlElements {
    private ArrayList<HtmlTag> tags;
    
    public HtmlElements() {
        tags = new ArrayList<>();
    }

    public ArrayList<HtmlTag> getTags() {
        return tags;
    }
    
    public void addTag(HtmlTag tag){
        tags.add(tag);
    }
    
    public void sort(){
        Collections.sort(tags);
    }
    
//    public HtmlTree formTree(){
//        HtmlTree tree = new HtmlTree("HTML-FILE", null, null);
//        ft(tree, 0);
//        return tree;
//    }
    
//    private void ft(HtmlBranch parent, int ind){
//        HtmlTag tag = tags.get(ind);
//        HtmlTag parentTag = parent.getObj();
//        if(parentTag != null){
//            parentTag.getData().addInside(tag.getData().getParentInside());
//        }
//        int rank = tag.getData().getRank();
//        HtmlBranch part = new HtmlBranch(tag.getTagName(), parent, tag);
//        parent.addPart(part);
//        if(ind == tags.size() - 1){
//            return;
//        }
//        HtmlTag next = tags.get(ind+1);
//        int nextRank = next.getData().getRank();
//        if(nextRank == rank){
//            ft(parent, ind+1);
//        } else if(nextRank > rank){
//            ft(part, ind+1);
//        } else {
//            ft((HtmlBranch) parent.getParent(), ind+1);
//        }
//    }
    
    public static HtmlElements getHtmlElements(File f){
        HtmlScanner scan = new HtmlScanner(f);
        HtmlElements tags = scan.getTags();
        tags.sort();
        return tags;
    }
    
//    public Object[] getTagsOfType(String type, HtmlTagAcceptor acceptor){
//        ArrayList<HtmlTag> tagsOfType = new ArrayList<>();
//        for(HtmlTag tag : tags){
//            if(acceptor.accept(tag, type)){
//                tagsOfType.add(tag);
//            }
//        }
//        return Util.toArray(tagsOfType);
//    }
    
    public ArrayList<ArrayList<HtmlTag>> getTagsOfType(String[] inputs, boolean isTag){
        if(inputs.length == 0){
            return new ArrayList<>();
        }
        ArrayList<ArrayList<HtmlTag>> tagsOfType = new ArrayList<>();
        if(isTag){
            Comparator<HtmlTag> c = new Comparator<HtmlTag>() {
                @Override
                public int compare(HtmlTag o1, HtmlTag o2) {
                    return o1.getTagName().compareTo(o2.getTagName());
                }
            };
            Collections.sort(tags, c);
            for(String input : inputs){
                tagsOfType.add(Util.binaryGetAll(tags, new HtmlTag(input, null), c));
            }
        } else {
            
            Comparator<HtmlTag> c = new Comparator<HtmlTag>() {
                @Override
                public int compare(HtmlTag o1, HtmlTag o2) {
                    Attrib[] tribs1 = o1.getData().getTribs();
                    Attrib[] tribs2 = o2.getData().getTribs();
                    
                    if(tribs1.length == 0 && tribs2.length == 0){
                        return 0;
                    }
                    Attrib key = new Attrib(Holder.object, "h");
                    boolean has1 = Util.binaryHas(tribs1, key);
                    boolean has2 = Util.binaryHas(tribs2, key);
                    
                    
                    if(has1 == has2){
                        return 0;
                    } else if(has1 && !has2){
                        return -1;
                    } else {
                        return 1;
                    }
                }
            };
            for(int i = 0; i < inputs.length; i++){
                tagsOfType.add(new ArrayList<>());
                String input = inputs[i];
                Holder.object = input;
                Collections.sort(tags, c);
                for(int j = 0; j < tags.size(); j++){
                    HtmlTag t = tags.get(j);
                    if(Util.binaryHas(t.getData().getTribs(), new Attrib(input, "h"))){
                        tagsOfType.get(i).add(t);
                    } else {
                        j = tags.size();
                    }
                }
            }
        }
        return tagsOfType;
    }
    
    private static class Holder {
        private static String object;
    }
}
