
import javax.swing.JScrollPane;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author beamj
 */
public class UrlConfigScrollPane extends JScrollPane {
    
    UrlConfigModel model;

    /**
     * Creates new form ProcessConfigScrollPane
     */
    public UrlConfigScrollPane() {
        //Create a new model, add a default config to it and set the default config as the selected item
        model = new UrlConfigModel();
        DefaultUrlConfig defaultConfig = new DefaultUrlConfig();
        model.addConfig(defaultConfig);
        model.setSelectedItem(defaultConfig);
        
        initComponents();
        //Initialize scroll bar speeds
        getVerticalScrollBar().setUnitIncrement(16);
        getHorizontalScrollBar().setUnitIncrement(16);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        urlConfigPanel = new javax.swing.JPanel();
        configSelectBox = new javax.swing.JComboBox<>();
        nameLabel = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        extEditPanel = new javax.swing.JPanel();
        extSaveButton = new javax.swing.JButton();
        removeExtButton = new javax.swing.JButton();
        urlLabel = new javax.swing.JLabel();
        urlBox = new javax.swing.JComboBox<>();
        urlAddressLabel = new javax.swing.JLabel();
        urlAddressField = new javax.swing.JTextField();
        urlParentButton = new javax.swing.JCheckBox();
        saveButton = new javax.swing.JButton();
        increaseRankButton = new javax.swing.JButton();
        decreaseRankButton = new javax.swing.JButton();

        configSelectBox.setModel(model);

        nameLabel.setText("Configuration Name:");

        extEditPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Add and Edit Urls", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP));

        extSaveButton.setText("Save Url");

        removeExtButton.setText("Remove Url From List");

        urlLabel.setText("Url to Edit:");

        urlAddressLabel.setText("Url Address:");

        urlParentButton.setText("Apply to Urls Under It");

        javax.swing.GroupLayout extEditPanelLayout = new javax.swing.GroupLayout(extEditPanel);
        extEditPanel.setLayout(extEditPanelLayout);
        extEditPanelLayout.setHorizontalGroup(
            extEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(extEditPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(extEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(removeExtButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(extSaveButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(extEditPanelLayout.createSequentialGroup()
                        .addGroup(extEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(urlLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(urlAddressLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(14, 14, 14)
                        .addGroup(extEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(urlAddressField)
                            .addComponent(urlBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(extEditPanelLayout.createSequentialGroup()
                        .addComponent(urlParentButton)
                        .addGap(0, 486, Short.MAX_VALUE)))
                .addContainerGap())
        );
        extEditPanelLayout.setVerticalGroup(
            extEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, extEditPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(extEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(urlLabel)
                    .addComponent(urlBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(extEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(urlAddressLabel)
                    .addComponent(urlAddressField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(urlParentButton)
                .addGap(10, 10, 10)
                .addComponent(removeExtButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(extSaveButton)
                .addContainerGap())
        );

        saveButton.setText("Save Configuration");

        increaseRankButton.setText("Increase In Rank");

        decreaseRankButton.setText("Decrease In Rank");

        javax.swing.GroupLayout urlConfigPanelLayout = new javax.swing.GroupLayout(urlConfigPanel);
        urlConfigPanel.setLayout(urlConfigPanelLayout);
        urlConfigPanelLayout.setHorizontalGroup(
            urlConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(urlConfigPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(urlConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(configSelectBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(saveButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(extEditPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(urlConfigPanelLayout.createSequentialGroup()
                        .addComponent(nameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nameField))
                    .addComponent(increaseRankButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(decreaseRankButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        urlConfigPanelLayout.setVerticalGroup(
            urlConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(urlConfigPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(configSelectBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(urlConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameLabel)
                    .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(extEditPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(increaseRankButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(decreaseRankButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saveButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setViewportView(urlConfigPanel);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<UrlConfig> configSelectBox;
    private javax.swing.JButton decreaseRankButton;
    private javax.swing.JPanel extEditPanel;
    private javax.swing.JButton extSaveButton;
    private javax.swing.JButton increaseRankButton;
    private javax.swing.JTextField nameField;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JButton removeExtButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JTextField urlAddressField;
    private javax.swing.JLabel urlAddressLabel;
    private javax.swing.JComboBox<UrlConfig.Url> urlBox;
    private javax.swing.JPanel urlConfigPanel;
    private javax.swing.JLabel urlLabel;
    private javax.swing.JCheckBox urlParentButton;
    // End of variables declaration//GEN-END:variables
}
