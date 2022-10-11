package com.mindblown.webdown;


import com.formdev.flatlaf.FlatDarkLaf;
import java.net.MalformedURLException;
import java.net.URISyntaxException;


/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

/**
 *
 * @author beamj
 */
public class Main {
    
    private static final String[] javascriptVoids = new String[]{"javascript:void(0)", "javascript:void(0);", "javascript:", 
    "javascript:;"};
    
    public static void main(String[] args) throws MalformedURLException, URISyntaxException{
        
        FlatDarkLaf.setup();

//        new TRY().setVisible(true);
        new MainFrame().setVisible(true);
    }
}