package com.mindblown.webdown;


import java.util.ArrayList;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileFilter;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;


/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

/**
 *
 * @author beamj
 */
public class MainFrame extends javax.swing.JFrame {
    
    private boolean atStart = true;
    
    //Server Download Section
    private FileAction fa;
    public static MainFrame main;
    private boolean inFail = false;
    private int backupPerDownloadsNum;
    private StyledDocument detailedLogStyleDocument;
    private DetailedLogDocumentFilter filter;
    
    private Style redStyle;
    private Style greenStyle;
    private Style yellowStyle;
    private Style blueStyle;
    private Style orangeStyle;
    private Style defaultStyle;
    
    private static Color redColor = new Color(255, 40, 40);
//    private static Color redColor = new Color(255, 30, 30);
    private static Color blueColor = new Color(52, 108, 187);
    private static Color greenColor = new Color(182, 137, 98);
    private static Color orangeColor = new Color(209, 184, 59);
    private static Color yellowColor = new Color(140, 255, 0);
    private static Color defaultColor = new Color(187, 187, 187);
    
    //Start Page Section
    private File mainServerFolder = new File("C:\\Users\\" + Util.getPCUserName() + "\\Servers");
    
    //WEHEN ADDING NEW FILES, UPDATE THE FILE ACEEPTOR IN SERVER TREE
    
    //Head File is the Specific Server Folder (Like C:\Servers\ss64.com)
    public static File headFile;
    //Urls To Down Folder is the UTD folder inside ProgramFiles
    public static File urlsToDownFolder;
    //Down Fail File is the failLog inside ProgramFiles
    public static File downFailFile;
    //Program Files folder next to program
    public static File programFiles = new File("ProgramFiles").getAbsoluteFile();
    //Cache File is right inside ProgramFiles and tells which the starting configs
    public static File cacheFile;
    //Server Container Head File is inside mainServerFolder and has the name of the main server trying to download.
    //It contains all the folders that contain the downloaded files
    //An example would be like (C:\Servers\ss64.com)
    public static File serverContainerHeadFile;
    
    public static final String cacheFileName = "server-cache";
    
    public static final String urlsToDownFolderName = "UTD";
    public static final String downFailFileName = "failLog";
    
    public static File programFilesSubFolder;
    public static File serverProgramFilesFolder;
    
    private static final String serverIdentifierName = "ServerFolder";
    
    private static File logFolder;
    private static final String logFolderName = "logs";
    
    private CodeGenerator codeGenerator;
    
    public JDialog currentLoadingDial;
    private StartupConfigurations startupConfig;
    
    private boolean closeNormally = true;
    
    private boolean addedDetailedLogYet = false;
    private boolean addedBasicLogYet = false;
    
    
    /**
     * The model that holds the Non-Resource-Hosts.
     */
    private MultiModel<Ref> nrhModel;
    
    private static final FileFilter programFilesServerDirsFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if(!pathname.isDirectory()){
                    return false;
                }
                if(!CodeGenerator.acceptable(pathname.getName())){
                    return false;
                }
                
                File[] serverFolderFile = pathname.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File pathName) {
                        return pathName.getName().equals(serverIdentifierName) && pathName.isFile();
                    }
                });
                return serverFolderFile.length != 0;
            }
        };
    
    private File prevMainServerFolder = null;
    private Ref prevServerRef = null;
    
    //The preferred dimensions of the downloading panel.
    private static final int PREF_DOWN_WIDTH = 900;
    private static final int PREF_DOWN_HEIGHT = 575;
    
    //The preferred dimensions of the starting panel.
    private static final int PREF_START_WIDTH = 540;
    private static final int PREF_START_HEIGHT = 250;
    
    private int numDownloadedFiles = 0;
    
    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        nrhModel = new MultiModel<>();
        initComponents();
        //Initialize Stuff
        restartServerDownloadButton.setVisible(false);
        enableGenUrlsSection(false);
        main = this;
        
        //Just to make sure that the first panel is shown
        switchToStartPanel();
        
        loadConfig();
    }
    
    /**
     * Set the size of the MainFrame to the preferred size that best fits the starting panel's GUI.
     */
    private void setToStartSize(){
        setSize(PREF_START_WIDTH, PREF_START_HEIGHT);
    }
    
    /**
     * Set the size of the MainFrame to the preferred size that best fits the downloading panel's GUI.
     */
    private void setToDownSize(){
        setSize(PREF_DOWN_WIDTH, PREF_DOWN_HEIGHT);
    }
    
    /**
     * Switch the panel that MainFrame is showing to the starting panel. This function will 
     * also modify the size of the MainFrame so that it best fits the starting panel.
     */
    private void switchToStartPanel(){
        add(startPagePanel);
        remove(serverDownPanel);
        setToStartSize();
    }
    
    /**
     * Switch the panel that MainFrame is showing to the downloading panel. This function will 
     * also modify the size of the MainFrame so that it best fits the downloading panel.
     */
    private void switchToDownPanel(){
        remove(startPagePanel);
        add(serverDownPanel);
        setToDownSize();
    }
    
    private void loadConfig(){
        programFiles.mkdirs();
        cacheFile = new File(programFiles, cacheFileName);
        codeGenerator = CodeGenerator.createCodeGeneratorBasedOnFileNames(programFiles, "000001", new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
        /*
        1. Load StartupConfigurations Cache File;
        2. Check if Theres a folder called the code and has the ServerFolder file.
        3. If not, Then discard trhe Startup Config and Do the Startup Config == null Thing;
        4. Else, Then Get the Server Folder from the ServerFolder file. If the folder doesnt exist, then
        do startup config == null thing, else continue with this function.
        
        
        When doing setting a new startup config:
        1. check to see if there's already a folder with the server container existing.
        2. If there is, then use that server code
        3. else, make a new server code, make a new folder, and make a new server identifier.
        */
        CacheScanner<StartupConfigurations> scan = StartupConfigurations.getScan();
        scan.setParams(Util.toArr(Util.getText(cacheFile)));
        startupConfig = scan.next();
        if(startupConfig == null){
            setStartupConfig();
            return;
        }
        
        File serverIdentifierFile = null;
        boolean keepOnGoing = false;
        if(CodeGenerator.acceptable(startupConfig.serverCode)){
            File serverFolder = new File(programFiles, startupConfig.serverCode);
            if(serverFolder.isDirectory()){
                File serverIdentifierContainer = new File(serverFolder, serverIdentifierName);
                if(serverIdentifierContainer.isFile()){
                    String serverIdentifier = Util.getText(serverIdentifierContainer);
                    serverIdentifierFile = new File(serverIdentifier);
                    if(serverIdentifierFile.getAbsoluteFile().toString().equals(serverIdentifier) && 
                            serverIdentifierFile.isDirectory()){
                        keepOnGoing = true;
                    }
                }
            }
        }
        
        if(!keepOnGoing){
            setStartupConfig();
            return;
        }
        
        setMainServerFolder(serverIdentifierFile);
        
        String serverToDown = startupConfig.serverToDown;
        String startUrl = startupConfig.startUrl;
        
        serverToDownloadBox.setSelectedItem(serverToDown);
        startingUrlField.setText(startUrl);
        
        boolean genUrlsToDownByData = startupConfig.genUrlsToDownByData;
        genUrlsToDownFromDataButton.setSelected(genUrlsToDownByData);
        genUrlsToDownFromFilesButton.setSelected(!genUrlsToDownByData);
        
        FileAction.setBackupPerDownloadsNum(startupConfig.backupPerDowns);
        
        String genUrlsToDownVal = "Generate Urls to Download";
        if(genUrlsToDownByData){
            genUrlsToDownVal += " By Data";
        } else {
            genUrlsToDownVal += " By Processing Server Tree";
        }
        
        if(Util.yesNo(this, "Would you like to auto start with previous configurations:\n"
                + "Main Server Folder: " + mainServerFolder + "\n"
                        + "Server to Download: " + serverToDown + "\n"
                                + "Start Url: " + startUrl + "\n"
                + genUrlsToDownVal, "Pre-Load?")){
            genServerTreeButtonActionPerformed(null);
            startServerDownButtonActionPerformed(null);
        }
    }
    
    private void enableGenUrlsSection(boolean enabled){
        genUrlsToDownPanel.setEnabled(enabled);
        genUrlsToDownFromDataButton.setEnabled(enabled);
        genUrlsToDownFromFilesButton.setEnabled(enabled);
        genUrlsToDownLabel.setEnabled(enabled);
        startServerDownButton.setEnabled(enabled);
        backToChooseServerButton.setEnabled(enabled);
    }
    
    private void enableGenServerTreeSection(boolean enabled){
        serverToDownloadPanel.setEnabled(enabled);
        serverToDownloadBox.setEnabled(enabled);
        serverToDownloadLabel.setEnabled(enabled);
        startingUrlLabel.setEnabled(enabled);
        startingUrlField.setEnabled(enabled);
        genServerTreeButton.setEnabled(enabled);
        mainServerFolderButton.setEnabled(enabled);
        mainServerFolderField.setEnabled(enabled);
        mainServerFolderLabel.setEnabled(enabled);
    }
    
    public void finishedServerDownload(){
        restartServerDownloadButton.setVisible(true);
        enableFrame(false);
        failedUrlsPanel.setEnabled(true);
        failedUrlsPanel.thoroughEnable(true);
        closeNormally = true;
    }
    
    public void restartServerDownload(){
        while(fa.isAlive()){
            
        }
        fa.reset();
        failedUrlsPanel.getModel().setData(new ArrayList<>());
        fa.save(false);
        restartServerDownloadButton.setVisible(false);
        fa.initDown(startupConfig.startUrl);
        fa.begin();
        basicLog.setText("");
        detailedLog.setText("");
        enableFrame(true);
        closeNormally = false;
    }
    
    public void enableFrame(boolean enable){
        basicLog.setEnabled(enable);
        basicLogScrollPane.setEnabled(enable);
        detailedLog.setEnabled(enable);
        detailedLogScrollPane.setEnabled(enable);
        failedUrlsPanel.setEnabled(enable);
        failedUrlsPanel.thoroughEnable(enable);
        backupPerDownloadsLabel.setEnabled(enable);
        backupPerDownloadsSlider.setEnabled(enable);
        progressLabel.setEnabled(enable);
        progressBar.setEnabled(enable);
        totalProgressLabel.setEnabled(enable);
        totalProgressBar.setEnabled(enable);
        saveButton.setEnabled(enable);
        exitButton.setEnabled(enable);
        safeSaveButton.setEnabled(enable);
        unsafeExitButton.setEnabled(enable);
    }
    
    public void disposeLoadingDial(){
        if(currentLoadingDial != null){
            currentLoadingDial.dispose();
        }
    }
    
    private void startServerDownMode(){    
        atStart = false;
        closeNormally = false;
        
        initTotalProgressBar();
        
        failedUrlsPanel.setFailedUrls(downFailFile, fa);
        //Switch the panel that MainFrame is showing to the downloading panel.
        switchToDownPanel();
        
        backupPerDownloadsNum = FileAction.getBackupPerDownloadsNum();
        
        setTitle("Server Download - " + fa.getServerTree().getServerRef().toFile());
        
        backupPerDownloadsSlider.setMaximum(FileAction.maxBackupPerDownloadsNum);
        backupPerDownloadsSlider.setValue(backupPerDownloadsNum);
        updateBackupPerDownloads();
        
        basicLogScrollPane.getVerticalScrollBar().setBlockIncrement(1);
        
        fa.setFailedUrlsPanel(failedUrlsPanel);
        
        progressBar.setMinimum(0);
        totalProgressBar.setMinimum(0);
        progressBar.setMaximum(100);
        totalProgressBar.setMaximum(100);
        
        documentPrompt();
        setStyles();
    }
    
    private void documentPrompt(){
        detailedLogStyleDocument = detailedLog.getStyledDocument();
        filter = new DetailedLogDocumentFilter();
        ((DefaultStyledDocument)detailedLog.getDocument()).setDocumentFilter(filter);
    }
    
    private void setStyles(){
        redStyle = detailedLog.addStyle("Red", null);
        StyleConstants.setForeground(redStyle, redColor);
        
        blueStyle = detailedLog.addStyle("Blue", null);
        StyleConstants.setForeground(blueStyle, blueColor);
        
        greenStyle = detailedLog.addStyle("Green", null);
        StyleConstants.setForeground(greenStyle, greenColor);
        
        yellowStyle = detailedLog.addStyle("Yellow", null);
        StyleConstants.setForeground(yellowStyle, yellowColor);
        
        orangeStyle = detailedLog.addStyle("Orange", null);
        StyleConstants.setForeground(orangeStyle, orangeColor);
        
        defaultStyle = detailedLog.addStyle("Default", null);
        StyleConstants.setForeground(defaultStyle, defaultColor);
    }
    
    private void setMainServerFolder(File dir){
        mainServerFolder = dir;
        setMainServerFolderText();
        File[] possibleServerDirs = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory();
            }
        });
        
        if(possibleServerDirs.length > 0){
            startingUrlField.setText(possibleServerDirs[0].getName());
            for(File serverDir : possibleServerDirs){
                String serverDirName = serverDir.getName();
                serverToDownloadBox.addItem(serverDirName);
            }
        }
    }
    
    private void setMainServerFolderText(){
        if(mainServerFolder == null){
            mainServerFolderField.setText("");
        } else {
            mainServerFolderField.setText(mainServerFolder.toString());
        }
    }
    
    public void changeInNumOfDownloadedFiles(int change){
        if(atStart){
            return;
        }
        numDownloadedFiles += change;
    }
    
    public void doProgressBar(){
        if(atStart){
            return;
        }
        double result = Util.map(numDownloadedFiles, 0, fa.getNumOfUrlsToDown() + numDownloadedFiles, 0, 100);
        totalProgressBar.setValue((int)result);
        totalProgressLabel.setText(Util.removeDecimal(result, 2) + "%");
    }
    
    private void initTotalProgressBar(){
        assert fa != null;
        numDownloadedFiles = fa.getNumOfLeaves();
        doProgressBar();
    }
    
    public void setCurrentProcess(String process, int percentage){
        progressLabel.setText(process);
        progressBar.setValue(percentage);
    }
    
    /**
     * Determines whether the given scroll bar has reached the bottom.
     * @param bar the scroll bar.
     * @return whether the scroll bar has reached the bottom.
     */
    private boolean isScrollAtBottom(JScrollBar bar){
        int val = bar.getValue();
        int extent = bar.getModel().getExtent();
        int max = bar.getMaximum();
        boolean atBottom = (val+extent) == max;
        return atBottom;
    }
    
    public void addBasicLog(String log){
        JScrollBar vertScrollBar = basicLogScrollPane.getVerticalScrollBar();
        boolean atBottom = isScrollAtBottom(vertScrollBar) && !vertScrollBar.getValueIsAdjusting();
        try {
            String addition;
            if(addedBasicLogYet){
                addition = "\n";
            } else {
                addedBasicLogYet = true;
                addition = "";
            }
            basicLog.getDocument().insertString(basicLog.getDocument().getLength(), addition + log, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if(atBottom){
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    JScrollBar vertScrollBar = basicLogScrollPane.getVerticalScrollBar();
                    vertScrollBar.setValue(vertScrollBar.getMaximum());
                }
            });
        }
    }
    
    public void ending(){
        serverDownPane.remove(failedUrlsPanel);
        repaint();
        JLabel l = new JLabel("Saving and Exiting...");
        l.setHorizontalAlignment(SwingConstants.CENTER);
        Util.show(l, "Exiting", false, true, false).setVisible(true);
    }
    
    public static final int GOOD = 0;
    public static final int FAILURE = 1;
    public static final int PROCESS = 2;
    public static final int NOTIFICATION = 3;
    public static final int WARNING = 4;
    
    /**
     *
     * @param log
     * @param type This is what color to display the text.
     * g is for yellow. (Good)
     * e is for red. (Error)
     * p is for blue. (Process)
     * n is for green. (Notification)
     * w is for orange. (Warning)
     */
    public void addDetailedLog(String log, Integer type){
        JScrollBar vertScrollBar = detailedLogScrollPane.getVerticalScrollBar();
        boolean atBottom = isScrollAtBottom(vertScrollBar) && !vertScrollBar.getValueIsAdjusting();
        
        Style styleToUse;
        
        switch(type) {
            case GOOD:
                styleToUse = yellowStyle;
                break;
            case FAILURE:
                styleToUse = redStyle;
                break;
            case PROCESS:
                styleToUse = blueStyle;
                break;
            case NOTIFICATION:
                styleToUse = greenStyle;
                break;
            case WARNING:
                styleToUse = orangeStyle;
                break;
            default:
                styleToUse = defaultStyle;
        }
        
        try {
            filter.allowChange();
            String addition;
            if(addedDetailedLogYet){
                addition = "\n";
            } else {
                addedDetailedLogYet = true;
                addition = "";
            }
            detailedLogStyleDocument.insertString(detailedLog.getDocument().getLength(), addition + log, styleToUse);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if(atBottom){
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    JScrollBar vertScrollBar = detailedLogScrollPane.getVerticalScrollBar();
                    vertScrollBar.setValue(vertScrollBar.getMaximum());
                }
            });
        }
    }
    
    public boolean inFail(){
        return inFail;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        genUrlsToDownButtonGroup = new javax.swing.ButtonGroup();
        startPagePanel = new javax.swing.JPanel();
        startPagePane = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        mainServerFolderLabel = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        mainServerFolderField = new javax.swing.JTextField();
        mainServerFolderButton = new javax.swing.JButton();
        serverDownConfigPanel = new javax.swing.JPanel();
        serverToDownloadPanel = new javax.swing.JPanel();
        serverToDownloadBox = new javax.swing.JComboBox<>();
        serverToDownloadLabel = new javax.swing.JLabel();
        startingUrlLabel = new javax.swing.JLabel();
        startingUrlField = new javax.swing.JTextField();
        genServerTreeButton = new javax.swing.JButton();
        genUrlsToDownPanel = new javax.swing.JPanel();
        genUrlsToDownLabel = new javax.swing.JLabel();
        genUrlsToDownFromDataButton = new javax.swing.JRadioButton();
        genUrlsToDownFromFilesButton = new javax.swing.JRadioButton();
        startServerDownButton = new javax.swing.JButton();
        backToChooseServerButton = new javax.swing.JButton();
        serverDownPanel = new javax.swing.JPanel();
        serverDownPane = new javax.swing.JTabbedPane();
        basicLogScrollPane = new javax.swing.JScrollPane();
        basicLog = new javax.swing.JTextPane();
        detailedLogScrollPane = new javax.swing.JScrollPane();
        detailedLog = new javax.swing.JTextPane();
        failedUrlsPanel = new com.mindblown.webdown.FailedUrlsPanel();
        progressBar = new javax.swing.JProgressBar();
        totalProgressBar = new javax.swing.JProgressBar();
        progressLabel = new javax.swing.JLabel();
        totalProgressLabel = new javax.swing.JLabel();
        saveButton = new javax.swing.JButton();
        exitButton = new javax.swing.JButton();
        unsafeExitButton = new javax.swing.JButton();
        backupPerDownloadsLabel = new javax.swing.JLabel();
        backupPerDownloadsSlider = new javax.swing.JSlider();
        safeSaveButton = new javax.swing.JButton();
        restartServerDownloadButton = new javax.swing.JButton();

        jScrollPane1.setBorder(null);
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        mainServerFolderLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        mainServerFolderLabel.setText("Main Server Folder:");

        jScrollPane2.setBorder(null);

        mainServerFolderField.setEditable(false);
        mainServerFolderField.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane2.setViewportView(mainServerFolderField);

        mainServerFolderButton.setText("Change Folder");
        mainServerFolderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mainServerFolderButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainServerFolderLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mainServerFolderButton)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(mainServerFolderLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(mainServerFolderButton)))
                .addContainerGap())
        );

        serverDownConfigPanel.setLayout(new java.awt.GridLayout(1, 2));

        serverToDownloadBox.setEditable(true);
        serverToDownloadBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                serverToDownloadBoxItemStateChanged(evt);
            }
        });

        serverToDownloadLabel.setText("Server To Download:");

        startingUrlLabel.setText("Starting Url:");

        genServerTreeButton.setText("Generate Server Tree");
        genServerTreeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genServerTreeButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout serverToDownloadPanelLayout = new javax.swing.GroupLayout(serverToDownloadPanel);
        serverToDownloadPanel.setLayout(serverToDownloadPanelLayout);
        serverToDownloadPanelLayout.setHorizontalGroup(
            serverToDownloadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(serverToDownloadPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(serverToDownloadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(serverToDownloadLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
                    .addGroup(serverToDownloadPanelLayout.createSequentialGroup()
                        .addGroup(serverToDownloadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(serverToDownloadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(startingUrlLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(serverToDownloadBox, javax.swing.GroupLayout.Alignment.LEADING, 0, 198, Short.MAX_VALUE)
                                .addComponent(startingUrlField, javax.swing.GroupLayout.Alignment.LEADING))
                            .addComponent(genServerTreeButton))
                        .addGap(0, 28, Short.MAX_VALUE))))
        );
        serverToDownloadPanelLayout.setVerticalGroup(
            serverToDownloadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(serverToDownloadPanelLayout.createSequentialGroup()
                .addComponent(serverToDownloadLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(serverToDownloadBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(startingUrlLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(startingUrlField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                .addComponent(genServerTreeButton)
                .addGap(0, 0, 0))
        );

        serverDownConfigPanel.add(serverToDownloadPanel);

        genUrlsToDownLabel.setText("Generate Urls to Download From:");

        genUrlsToDownButtonGroup.add(genUrlsToDownFromDataButton);
        genUrlsToDownFromDataButton.setSelected(true);
        genUrlsToDownFromDataButton.setText("Data From Application Files");

        genUrlsToDownButtonGroup.add(genUrlsToDownFromFilesButton);
        genUrlsToDownFromFilesButton.setText("Processing Server Tree");

        startServerDownButton.setText("Start Server Download");
        startServerDownButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startServerDownButtonActionPerformed(evt);
            }
        });

        backToChooseServerButton.setText("Back");
        backToChooseServerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backToChooseServerButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout genUrlsToDownPanelLayout = new javax.swing.GroupLayout(genUrlsToDownPanel);
        genUrlsToDownPanel.setLayout(genUrlsToDownPanelLayout);
        genUrlsToDownPanelLayout.setHorizontalGroup(
            genUrlsToDownPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(genUrlsToDownPanelLayout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addGroup(genUrlsToDownPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(genUrlsToDownFromFilesButton)
                    .addComponent(genUrlsToDownLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(genUrlsToDownPanelLayout.createSequentialGroup()
                        .addComponent(backToChooseServerButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(startServerDownButton))
                    .addComponent(genUrlsToDownFromDataButton))
                .addContainerGap())
        );
        genUrlsToDownPanelLayout.setVerticalGroup(
            genUrlsToDownPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(genUrlsToDownPanelLayout.createSequentialGroup()
                .addComponent(genUrlsToDownLabel)
                .addGap(18, 18, 18)
                .addComponent(genUrlsToDownFromDataButton)
                .addGap(4, 4, 4)
                .addComponent(genUrlsToDownFromFilesButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                .addGroup(genUrlsToDownPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(backToChooseServerButton)
                    .addComponent(startServerDownButton))
                .addGap(0, 0, 0))
        );

        serverDownConfigPanel.add(genUrlsToDownPanel);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(serverDownConfigPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 20, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(serverDownConfigPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jScrollPane1.setViewportView(jPanel4);

        startPagePane.addTab("Start Page", jScrollPane1);

        javax.swing.GroupLayout startPagePanelLayout = new javax.swing.GroupLayout(startPagePanel);
        startPagePanel.setLayout(startPagePanelLayout);
        startPagePanelLayout.setHorizontalGroup(
            startPagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(startPagePanelLayout.createSequentialGroup()
                .addComponent(startPagePane, javax.swing.GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        startPagePanelLayout.setVerticalGroup(
            startPagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(startPagePanelLayout.createSequentialGroup()
                .addComponent(startPagePane, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        serverDownPanel.setMinimumSize(new java.awt.Dimension(618, 327));

        basicLog.setFont(new java.awt.Font("Microsoft Tai Le", 0, 12)); // NOI18N
        basicLog.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                basicLogKeyPressed(evt);
            }
        });
        basicLogScrollPane.setViewportView(basicLog);

        serverDownPane.addTab("Basic Log", basicLogScrollPane);

        detailedLog.setFont(new java.awt.Font("Microsoft Tai Le", 0, 12)); // NOI18N
        detailedLog.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                detailedLogKeyPressed(evt);
            }
        });
        detailedLogScrollPane.setViewportView(detailedLog);

        serverDownPane.addTab("Detailed Log", detailedLogScrollPane);
        serverDownPane.addTab("Failed Downloads", failedUrlsPanel);

        progressLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        progressLabel.setText("Doing Process....");

        totalProgressLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        totalProgressLabel.setText("37%");

        saveButton.setText("Save");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        exitButton.setText("Exit");
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
        });

        unsafeExitButton.setText("Unsafe Exit");
        unsafeExitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                unsafeExitButtonActionPerformed(evt);
            }
        });

        backupPerDownloadsLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        backupPerDownloadsLabel.setText("Backup Every 5 Downloads");

        backupPerDownloadsSlider.setMajorTickSpacing(5);
        backupPerDownloadsSlider.setMinorTickSpacing(1);
        backupPerDownloadsSlider.setPaintLabels(true);
        backupPerDownloadsSlider.setPaintTicks(true);
        backupPerDownloadsSlider.setSnapToTicks(true);
        backupPerDownloadsSlider.setValue(5);
        backupPerDownloadsSlider.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                backupPerDownloadsSliderMouseDragged(evt);
            }
        });
        backupPerDownloadsSlider.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                backupPerDownloadsSliderMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                backupPerDownloadsSliderMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                backupPerDownloadsSliderMouseReleased(evt);
            }
        });

        safeSaveButton.setText("Safe Save");
        safeSaveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                safeSaveButtonActionPerformed(evt);
            }
        });

        restartServerDownloadButton.setText("Backup and Restart Server Download");
        restartServerDownloadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                restartServerDownloadButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout serverDownPanelLayout = new javax.swing.GroupLayout(serverDownPanel);
        serverDownPanel.setLayout(serverDownPanelLayout);
        serverDownPanelLayout.setHorizontalGroup(
            serverDownPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(serverDownPane)
            .addComponent(backupPerDownloadsLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, serverDownPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(serverDownPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(backupPerDownloadsSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(totalProgressLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(progressLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(totalProgressBar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(progressBar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(serverDownPanelLayout.createSequentialGroup()
                        .addComponent(restartServerDownloadButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(saveButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(exitButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(safeSaveButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(unsafeExitButton)))
                .addContainerGap())
        );
        serverDownPanelLayout.setVerticalGroup(
            serverDownPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(serverDownPanelLayout.createSequentialGroup()
                .addComponent(serverDownPane, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
                .addGap(15, 15, 15)
                .addComponent(backupPerDownloadsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(backupPerDownloadsSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(progressLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(totalProgressLabel)
                .addGap(3, 3, 3)
                .addComponent(totalProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addGroup(serverDownPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(saveButton)
                    .addComponent(exitButton)
                    .addComponent(unsafeExitButton)
                    .addComponent(safeSaveButton)
                    .addComponent(restartServerDownloadButton))
                .addGap(10, 10, 10))
        );

        getContentPane().add(serverDownPanel);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void backupPerDownloadsSliderMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_backupPerDownloadsSliderMouseDragged
        updateBackupPerDownloads();
    }//GEN-LAST:event_backupPerDownloadsSliderMouseDragged
    
    private void backupPerDownloadsSliderMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_backupPerDownloadsSliderMouseClicked
        updateBackupPerDownloads();
        updateFileActionBackupPerDownloads();
    }//GEN-LAST:event_backupPerDownloadsSliderMouseClicked
    
    private void backupPerDownloadsSliderMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_backupPerDownloadsSliderMousePressed
        //Don't update the actual file action backuper per downloads because it's still being slided
        updateBackupPerDownloads();
    }//GEN-LAST:event_backupPerDownloadsSliderMousePressed
    
    private void backupPerDownloadsSliderMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_backupPerDownloadsSliderMouseReleased
        updateBackupPerDownloads();
        updateFileActionBackupPerDownloads();
    }//GEN-LAST:event_backupPerDownloadsSliderMouseReleased
    
    private void mainServerFolderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mainServerFolderButtonActionPerformed
        File dir = Util.getFile(Util.dirsOnly, "Choose Main Server Folder", mainServerFolder);
        if(dir != null){
            setMainServerFolder(dir);
        }
    }//GEN-LAST:event_mainServerFolderButtonActionPerformed
    
    private void basicLogKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_basicLogKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_S && evt.isControlDown()){
            File savedLog = saveLog(basicLog.getText(), "Basic_Log_");
            Util.openDir(logFolder);
            try {
                Thread.sleep(500);
            } catch (Exception ex) {
                
            }
            Util.open(savedLog);
        }
    }//GEN-LAST:event_basicLogKeyPressed
    
    private void detailedLogKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_detailedLogKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_S && evt.isControlDown()){
            File savedLog = saveLog(detailedLog.getText(), "Detailed_Log_");
            Util.openDir(logFolder);
            try {
                Thread.sleep(500);
            } catch (Exception ex) {
                
            }
            Util.open(savedLog);
        }
    }//GEN-LAST:event_detailedLogKeyPressed
    
    private void restartServerDownloadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_restartServerDownloadButtonActionPerformed
        restartServerDownload();
    }//GEN-LAST:event_restartServerDownloadButtonActionPerformed
    
    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        fa.save(false);
    }//GEN-LAST:event_saveButtonActionPerformed
    
    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitButtonActionPerformed
        fa.stopDownloading();
    }//GEN-LAST:event_exitButtonActionPerformed
    
    private void safeSaveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_safeSaveButtonActionPerformed
        fa.doSuddenBackup();
    }//GEN-LAST:event_safeSaveButtonActionPerformed
    
    private void unsafeExitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_unsafeExitButtonActionPerformed
        fa.end();
    }//GEN-LAST:event_unsafeExitButtonActionPerformed
    
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if(closeNormally){
            System.exit(0);
        } else {
            setExtendedState(JFrame.ICONIFIED);
        }
    }//GEN-LAST:event_formWindowClosing
    
    private void backToChooseServerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backToChooseServerButtonActionPerformed
        //IMPLEMENT GEN URLS TO DOWN BUTTONS
        enableGenServerTreeSection(true);
        enableGenUrlsSection(false);
    }//GEN-LAST:event_backToChooseServerButtonActionPerformed
    
    private void startServerDownButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startServerDownButtonActionPerformed
        boolean byData = genUrlsToDownFromDataButton.isSelected();
        Cacheable.cache(startupConfig, cacheFile);
        JLabel label = new JLabel("Generating Urls To Download...");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        currentLoadingDial = Util.show(label, "Loading...", false, true, false);
        
        UrlsToDownGenerator urlsToDownGen = new UrlsToDownGenerator(byData);
        urlsToDownGen.begin();
        
        currentLoadingDial.setVisible(true);
        
        startServerDownMode();
        fa.begin();
    }//GEN-LAST:event_startServerDownButtonActionPerformed
    
    private void genServerTreeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genServerTreeButtonActionPerformed
        if(mainServerFolder == null){
            Util.message(this, "The main server folder isn't chosen!", "Error");
            return;
        }
        
        if(!mainServerFolder.isDirectory()){
            Util.message(this, mainServerFolder.getName() + " is not a folder!", "Error");
            return;
        }
        
        String serverToDownload = (String)serverToDownloadBox.getSelectedItem();
        if(serverToDownload == null || Util.isBlank(serverToDownload)){
            Util.message(this, "The server to download hasn't been chosen!", "Error");
            return;
        }
        
        String startUrl = startingUrlField.getText();
        if(Util.isBlank(startUrl)){
            startUrl = serverToDownload + "";
        }
        
        
        mainServerFolder.mkdirs();
        String serverParent = mainServerFolder.toString();
        if(!serverParent.endsWith("/")){
            serverParent += "/";
        }
        
        String serverName = new Ref(serverToDownload).getHost();
        serverContainerHeadFile = new File(serverParent + serverName);
        serverContainerHeadFile.mkdirs();
        headFile = new File(serverContainerHeadFile, serverName);
        headFile.mkdirs();
        
        /*
        Once a server is chosen, create a new folder inside the programFilesSubFolder with the server's name on it.
        Inside that folder is where the urlstodownfolder and the downfailfile is put.
        */
        setStartupConfig();
        serverProgramFilesFolder = new File(programFilesSubFolder, serverName);
        serverProgramFilesFolder.mkdirs();
        urlsToDownFolder = new File(serverProgramFilesFolder, urlsToDownFolderName);
        urlsToDownFolder.mkdirs();
        downFailFile = new File(serverProgramFilesFolder, downFailFileName);
        logFolder = new File(serverProgramFilesFolder, logFolderName);
        logFolder.mkdirs();
        
        
        //If it's the same server, don;t change. Set start url if diff
        Ref newServerRef = new Ref(serverToDownload);
        
        boolean serverChanged = true;
        
        if(prevMainServerFolder == null || prevServerRef == null){
            
        } else {
            if(mainServerFolder.equals(prevMainServerFolder) && newServerRef.sameHost(prevServerRef)){
                serverChanged = false;
            }
        }
        prevMainServerFolder = mainServerFolder;
        prevServerRef = newServerRef;
        if(serverChanged){
            JLabel label = new JLabel("Generating Server Tree...");
            label.setHorizontalAlignment(SwingConstants.CENTER);
            currentLoadingDial = Util.show(label, "Loading...", false, true, false);
            
            ServerTreeGenerator treeGen = new ServerTreeGenerator(startUrl, serverToDownload);
            treeGen.begin();
            
            currentLoadingDial.setVisible(true);
        } else {
            fa.setStartupUrl(startUrl);
        }
        
        enableGenServerTreeSection(false);
        enableGenUrlsSection(true);
    }//GEN-LAST:event_genServerTreeButtonActionPerformed
    
    private void serverToDownloadBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_serverToDownloadBoxItemStateChanged
        startingUrlField.setText((String)serverToDownloadBox.getSelectedItem());
    }//GEN-LAST:event_serverToDownloadBoxItemStateChanged


    public MultiModel<Ref> getNrhModel() {
        return nrhModel;
    }
    
    private File saveLog(String log, String startOfLogName){
        File logSave = new File(logFolder, startOfLogName + CalendarHelper.nowFormat() + ".txt");
        Cacheable.cache(log, logSave);
        return logSave;
    }
    
    public void setStartupConfig(){
        String serverCode = null;
        File[] serverFolders = programFiles.listFiles(programFilesServerDirsFilter);
        for(int i = 0; i < serverFolders.length; i++){
            File serverFolder = serverFolders[i];
            File serverIdentifier = new File(serverFolder, serverIdentifierName);
            String inside = Util.getText(serverIdentifier);
            if(inside.equals(mainServerFolder.toString())){
                i = serverFolders.length;
                serverCode = serverFolder.getName();
                programFilesSubFolder = serverFolder.getAbsoluteFile();
            }
        }
        if(serverCode == null){
            serverCode = codeGenerator.next();
            File newServerDir = new File(programFiles, serverCode);
            newServerDir.mkdirs();
            File newServerIdentifier = new File(newServerDir, serverIdentifierName);            
            Util.writeToFile(newServerIdentifier, mainServerFolder.toString());
            programFilesSubFolder = newServerDir;
        }
        
        
        
        startupConfig = new StartupConfigurations(serverCode,
                (String)serverToDownloadBox.getSelectedItem(), startingUrlField.getText(),
                genUrlsToDownFromDataButton.isSelected(), FileAction.getBackupPerDownloadsNum());
    }
    
    /**
     * Set the BackupPerDownloadsLabel to correspond with the value of the backup per downloads slider.
     */
    private void updateBackupPerDownloads(){
        backupPerDownloadsNum = backupPerDownloadsSlider.getValue();
        String backupPerDownloadsText;
        switch (backupPerDownloadsNum) {
            case 0:
                backupPerDownloadsText = "No Backups";
                break;
            case 1:
                backupPerDownloadsText = "Backup Every Download";
                break;
            default:
                backupPerDownloadsText = "Backup Every " + backupPerDownloadsNum + " Downloads";
                break;
        }
        backupPerDownloadsLabel.setText(backupPerDownloadsText);
    }
    
    private void updateFileActionBackupPerDownloads(){
        FileAction.setBackupPerDownloadsNum(backupPerDownloadsNum);
        startupConfig.backupPerDowns = backupPerDownloadsNum;
        Cacheable.cache(startupConfig, cacheFile);
    }
    
    private class UrlsToDownGenerator implements Runnable{
        
        private final boolean byData;
        
        public UrlsToDownGenerator(boolean byData) {
            this.byData = byData;
        }
        
        @Override
        public void run() {
            if(byData){
                boolean hasUrlsToDown = false;
                try(DirectoryStream<Path> stream = Files.newDirectoryStream(MainFrame.urlsToDownFolder.toPath())){
                    Iterator<Path> iterator = stream.iterator();
                    if(iterator.hasNext()){
                        hasUrlsToDown = true;
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
                if(hasUrlsToDown){
                    
                } else {
                    fa.processTree();
                }
            } else {
                fa.processTree();
            }
            MainFrame.main.disposeLoadingDial();
        }
        
        public void begin(){
            new Thread(this).start();
        }
    }
    
    private class ServerTreeGenerator implements Runnable {
        private String startUrl;
        private String serverToDownload;
        
        public ServerTreeGenerator(String startUrl, String serverToDownload) {
            this.startUrl = startUrl;
            this.serverToDownload = serverToDownload;
        }
        
        public void begin(){
            new Thread(this).start();
        }
        
        @Override
        public void run() {
            ServerTree serverTree = new ServerTree(new Ref(serverToDownload), nrhModel);
            fa = new FileAction(startUrl, serverTree);
            MainFrame.main.disposeLoadingDial();
        }
    }
    
    private static class StartupConfigurations implements Cacheable<StartupConfigurations>{
        private String serverToDown;
        private String startUrl;
        private boolean genUrlsToDownByData;
        private int backupPerDowns;
        private String serverCode;
        
        public StartupConfigurations(String serverCode, String serverToDown, 
                String startUrl, boolean genUrlsToDownByData, int backupPerDowns) {
            this.serverToDown = serverToDown;
            this.startUrl = startUrl;
            this.genUrlsToDownByData = genUrlsToDownByData;
            this.backupPerDowns = backupPerDowns;
            this.serverCode = serverCode;
        }
        
        @Override
        public String cache() {
            return "\\*\\%\\SPECCODE-WORLDPEACE_START" + serverCode + "\n" 
                    + serverToDown + "\n" + startUrl + "\n" + genUrlsToDownByData
                    + "\n" + backupPerDowns + "\\*\\%\\SPECCODE-WORLDPEACE_END";
        }
        
        private static CacheScanner<StartupConfigurations> getScan() {
            return new CacheScanner<StartupConfigurations>() {
                private String text;
                
                @Override
                public StartupConfigurations next() {
                    String starter = "\\*\\%\\SPECCODE-WORLDPEACE_START";
                    String ender = "\\*\\%\\SPECCODE-WORLDPEACE_END";
                    int start = text.indexOf(starter);
                    int end = text.indexOf(ender, start+1);
                    if(start == -1 || end == -1){
                        return null;
                    }
                    String code = text.substring(start+starter.length(), end);
                    String[] parts = code.split("\n");
                    if(parts.length < 5){
                        return null;
                    }
                    String serverCode = parts[0];
                    String serverToDown = parts[1];
                    String startUrl = parts[2];
                    boolean genUrlsToDownByData = Boolean.parseBoolean(parts[3].toLowerCase());
                    int backupPerDowns = Integer.parseInt(parts[4]);
                    
                    text = text.substring(end+ender.length());
                    return new StartupConfigurations(serverCode, serverToDown, startUrl, genUrlsToDownByData, backupPerDowns);
                }
                
                @Override
                public void analyze() {
                    
                }
                
                @Override
                public ArrayList<StartupConfigurations> scanned() {
                    return null;
                }
                
                @Override
                public void setParams(Object... p) {
                    text = (String)p[0];
                }
            };
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backToChooseServerButton;
    private javax.swing.JLabel backupPerDownloadsLabel;
    private javax.swing.JSlider backupPerDownloadsSlider;
    private javax.swing.JTextPane basicLog;
    private javax.swing.JScrollPane basicLogScrollPane;
    private javax.swing.JTextPane detailedLog;
    private javax.swing.JScrollPane detailedLogScrollPane;
    private javax.swing.JButton exitButton;
    private com.mindblown.webdown.FailedUrlsPanel failedUrlsPanel;
    private javax.swing.JButton genServerTreeButton;
    private javax.swing.ButtonGroup genUrlsToDownButtonGroup;
    private javax.swing.JRadioButton genUrlsToDownFromDataButton;
    private javax.swing.JRadioButton genUrlsToDownFromFilesButton;
    private javax.swing.JLabel genUrlsToDownLabel;
    private javax.swing.JPanel genUrlsToDownPanel;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton mainServerFolderButton;
    private javax.swing.JTextField mainServerFolderField;
    private javax.swing.JLabel mainServerFolderLabel;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel progressLabel;
    private javax.swing.JButton restartServerDownloadButton;
    private javax.swing.JButton safeSaveButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JPanel serverDownConfigPanel;
    private javax.swing.JTabbedPane serverDownPane;
    private javax.swing.JPanel serverDownPanel;
    private javax.swing.JComboBox<String> serverToDownloadBox;
    private javax.swing.JLabel serverToDownloadLabel;
    private javax.swing.JPanel serverToDownloadPanel;
    private javax.swing.JTabbedPane startPagePane;
    private javax.swing.JPanel startPagePanel;
    private javax.swing.JButton startServerDownButton;
    private javax.swing.JTextField startingUrlField;
    private javax.swing.JLabel startingUrlLabel;
    private javax.swing.JProgressBar totalProgressBar;
    private javax.swing.JLabel totalProgressLabel;
    private javax.swing.JButton unsafeExitButton;
    // End of variables declaration//GEN-END:variables
}