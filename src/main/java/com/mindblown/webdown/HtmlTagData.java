package com.mindblown.webdown;


import java.util.Arrays;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author beamj
 */
public class HtmlTagData implements Comparable<HtmlTagData> {
    
    private final Attrib[] tribs;
    private String parentInside;
    private String inside;
    private final boolean start;
    private final boolean autoClose;
    private int rank;

    public HtmlTagData(Attrib[] tribs, String inside, String parentInside, boolean start, boolean autoClose, int rank) {
        this.tribs = tribs;
        this.parentInside = parentInside;
        this.start = start;
        this.autoClose = autoClose;
        this.rank = rank;
        this.inside = inside;
        Arrays.sort(tribs);
    }

    public String getParentInside() {
        return parentInside;
    }

    public void setParentInside(String parentInside) {
        this.parentInside = parentInside;
    }

    public void addInside(String i) {
        inside += i;
    }

    public String getInside() {
        return inside;
    }

    public Attrib[] getTribs() {
        return tribs;
    }

    public boolean isAutoClose() {
        return autoClose;
    }

    public boolean isStart() {
        return start;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getRank() {
        return rank;
    }

    @Override
    public int compareTo(HtmlTagData o) {
        int rankComp = rank - o.rank;
        if(rankComp != 0){
            return rankComp;
        }
        int tribLenComp = tribs.length - o.tribs.length;
        if(tribLenComp != 0){
            return tribLenComp;
        }
        for(int i = 0; i < tribs.length; i++){
            int tribComp = tribs[i].toString().compareTo(o.tribs[i].toString());
            if(tribComp != 0){
                return tribComp;
            }
        }
        return 0;
    }
}
