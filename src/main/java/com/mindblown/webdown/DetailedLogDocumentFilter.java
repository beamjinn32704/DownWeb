package com.mindblown.webdown;

/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/


import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 *
 * @author beamj
 */
public class DetailedLogDocumentFilter extends DocumentFilter{
    
    private boolean readonly = true;
    private boolean oneTime = false;
    
    public void allowChange(){
        readonly = false;
        oneTime = true;
    }
    
    @Override
    public void remove(FilterBypass fb, int offset, int length) throws BadLocationException{
        if (!readonly){
            if(oneTime){
                readonly = true;
                oneTime = false;
            }
            super.remove(fb, offset, length);
        }
    }
    
    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException{
        if (!readonly){
            if(oneTime){
                readonly = true;
                oneTime = false;
            }
            super.insertString(fb, offset, string, attr);
        }
    }
    
    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException{
        if (!readonly){
            super.replace(fb, offset, length, text, attrs);
        }
    }
}