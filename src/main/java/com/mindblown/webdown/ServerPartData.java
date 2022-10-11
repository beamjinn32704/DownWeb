package com.mindblown.webdown;


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
public class ServerPartData {
    private File file;
    private Ref ref;

    public ServerPartData(File file, Ref ref) {
        this.file = file;
        this.ref = ref;
    }

    public File getFile() {
        return file;
    }

    public Ref getRef() {
        return ref;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setRef(Ref ref) {
        this.ref = ref;
    }

    @Override
    public String toString() {
        return file.toString();
    }
}
