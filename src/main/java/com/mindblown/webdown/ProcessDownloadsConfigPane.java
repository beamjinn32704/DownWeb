package com.mindblown.webdown;


import javax.swing.JTabbedPane;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author beamj
 */
public class ProcessDownloadsConfigPane extends JTabbedPane {

    /**
     * Creates new form ProcessDownloadsConfigurationPane
     */
    public ProcessDownloadsConfigPane() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        processConfigScrollPane = new ProcessConfigScrollPane();
        urlConfigScrollPane = new UrlConfigScrollPane();

        addTab("Processing Configurations", processConfigScrollPane);
        addTab("Url Configurations", urlConfigScrollPane);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ProcessConfigScrollPane processConfigScrollPane;
    private UrlConfigScrollPane urlConfigScrollPane;
    // End of variables declaration//GEN-END:variables
}
