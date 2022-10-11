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
public interface BasicTagProcessor {
    void process(FileAction action, Object[] objects, Class<? extends HtmlTag> caster);
}
