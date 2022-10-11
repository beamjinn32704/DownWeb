package com.mindblown.webdown;


import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author beamj
 */
public class FileExtensionAnalyzer {
    public static boolean isHtmlFile(File file){
        //Open up a stream to the file and look at the first 50 characters.
        try (BufferedInputStream bi = new BufferedInputStream(new FileInputStream(file))){
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            bo.write(bi.readNBytes(1000));
            String firstChars = bo.toString(StandardCharsets.UTF_8);
//            firstChars = Util.strip(firstChars);
            //If the first 50 characters contain doctype html or the opening html tag, then
            //the file's probably an html file, and if so, return true.
            boolean doc = firstChars.contains("<!DOCTYPE html>");
            boolean html = firstChars.contains("<html");
            if(doc || html){
                return true;
            }
            return false;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
