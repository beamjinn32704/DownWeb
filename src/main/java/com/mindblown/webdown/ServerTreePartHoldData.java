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
public class ServerTreePartHoldData {
    private ServerPartData data;

    public ServerTreePartHoldData(ServerPartData data) {
        this.data = data;
    }

    public ServerPartData getData() {
        return data;
    }

    @Override
    public String toString() {
        return data.toString();
    }
    
    
}
