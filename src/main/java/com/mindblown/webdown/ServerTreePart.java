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
public class ServerTreePart extends TreePart<ServerPartData> {
    
    public ServerTreePart(String name, Branch p, ServerPartData obj) {
        super(name, p, obj);
    }
    
    public static ServerTreePart convertTo(TreePart<ServerPartData> o){
        return new ServerTreePart(o.getBranchName(), o.getParent(), o.getObj());
    }
}
