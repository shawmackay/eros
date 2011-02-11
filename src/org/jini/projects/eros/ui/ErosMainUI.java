/*
 Copyright 2006 Eros Project

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 */

/**
 * Title:        Eros Logging Service<p>
 * Description:  Project to provide distributed system logging.<p>
 * Copyright:    Copyright (c) C. Lunn<p>
 * Company:      Countrywide Assured<p>
 * @author C. Lunn
 * @version 1.0
 */
package org.jini.projects.eros.ui;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import net.jini.lookup.ui.MainUI;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.tree.*;

import org.jini.projects.eros.ErosServiceStats;
import org.jini.projects.eros.LogLevel;
import org.jini.projects.eros.ui.erroranalyser.model.ErrorModel;

import java.awt.event.*;

/**
 * MainUI for the Eros service. Provides all the details for the service
 * utilising the ServiceUI api.
 */
public class ErosMainUI
                extends
                JPanel implements MainUI,Serializable {

        private ErosServiceUI eros = null;

        private ErosServiceStats stats = null;

        private LevelTreeRenderer levelTreeRenderer = new LevelTreeRenderer();

        private LevelListRenderer levelListRenderer = new LevelListRenderer();

        private JList logsPerOS = new JList();

        private JList logsPerUser = new JList();

        private JPanel analyserPanel = new JPanel();

        private ErrorModel errorModel = null;

        BorderLayout borderLayout1 = new BorderLayout();

        JTabbedPane mainTabbedPane = new JTabbedPane();

        JPanel serviceCountsPanel = new JPanel();

        BorderLayout borderLayout2 = new BorderLayout();

        JPanel serviceCountsLeftPanel = new JPanel();

        JPanel serviceCountsRightPanel = new JPanel();

        BorderLayout borderLayout3 = new BorderLayout();

        JScrollPane perApplicationLogs = new JScrollPane();

        JTree appCountsTree = new JTree();

        JScrollPane internalLogScroll = new JScrollPane();

        JEditorPane internalLogPanel = new JEditorPane();

        JScrollPane jScrollPane1 = new JScrollPane();

        JScrollPane jScrollPane2 = new JScrollPane();

        JList serviceCountList = new JList();

        JList serviceLevelCountList = new JList();

        JScrollPane jScrollPane3 = new JScrollPane();

        JList updateableCounts = new JList();

        JButton refreshButton = new JButton();

        private JTextField analyseDateFrom = new JTextField();

        private JTextField analyseDateTo = new JTextField();

        private boolean getAllRecords;

        public ErosMainUI(ErosServiceUI eros) {
                try {
                        System.out.println("Eros MainUI Constructor");
                        this.eros = eros;
                        stats = eros.getStats();
                        jbInit();
                        init();
                        UpdateThread t = new UpdateThread();
                        t.start();
                } catch (Exception exc) {
                        exc.printStackTrace();
                }
        }

        public ErosMainUI() {
                try {
                        jbInit();
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        private void jbInit() throws Exception {
                GridBagConstraints gridBagConstraints = null;
                this.setLayout(new GridBagLayout());

                serviceCountsPanel.setLayout(new GridBagLayout());
                serviceCountsLeftPanel.setLayout(new GridBagLayout());
                internalLogPanel.setText("jEditorPane1");
                internalLogPanel.setEditable(false);
                serviceCountsLeftPanel.setPreferredSize(new Dimension(200, 200));
                jScrollPane1.setPreferredSize(new Dimension(150, 150));
                jScrollPane2.setPreferredSize(new Dimension(259, 125));
                perApplicationLogs.setPreferredSize(new Dimension(350, 400));
                jScrollPane3.setPreferredSize(new Dimension(259, 80));
                refreshButton.setText("Refresh All Data");
                refreshButton.addActionListener(new RefreshAction());
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
                gridBagConstraints.ipadx = 200;
                gridBagConstraints.weightx = 1.0;
                gridBagConstraints.weighty = 1.0;
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 1;

                gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
                this.add(mainTabbedPane, gridBagConstraints);
                mainTabbedPane.add(serviceCountsPanel, "Service Statistics");
                JPanel logQCounts = new JPanel();
                logQCounts.setLayout(new BorderLayout());
                logQCounts.add(jScrollPane3, BorderLayout.CENTER);

                JPanel levelCounts = new JPanel();
                levelCounts.setLayout(new BorderLayout());
                levelCounts.add(jScrollPane2, BorderLayout.CENTER);
                JPanel instanceInfo = new JPanel();
                instanceInfo.setLayout(new BorderLayout());
                instanceInfo.add(jScrollPane1, BorderLayout.CENTER);
                GridBagConstraints gc = new GridBagConstraints();
                gc.insets = new Insets(6, 6, 0, 5);
                gc.gridx = 0;
                gc.gridy = 1;
                gc.weightx = 1.0;
                gc.weighty = 1.0;
                gc.fill = GridBagConstraints.BOTH;
                serviceCountsPanel.add(serviceCountsLeftPanel, gc);
                GradientLabel glabel = new GradientLabel(new ImageIcon(getClass().getResource("/org/jini/projects/eros/eros_icon.png")));
                glabel.setText(" Error Service");
                glabel.setFont(new java.awt.Font("Dialog", Font.BOLD, 18));
                glabel.setHorizontalAlignment(SwingConstants.LEFT);
                glabel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
                glabel.setBackground(Color.WHITE);
                glabel.setBackgroundTo(new Color(202, 202, 255));
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
                gridBagConstraints.ipadx = 200;
                gridBagConstraints.gridx = 0;
                gridBagConstraints.weightx = 1.0;
                gridBagConstraints.gridy = 0;
                gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
                serviceCountsPanel.add(glabel, gridBagConstraints);
                gc = new GridBagConstraints();
                gc.insets = new Insets(6, 6, 0, 5);
                gc.gridx = 0;
                gc.gridy = 1;
                gc.weightx = 1.0;
                gc.weighty = 1.0;
                gc.fill = GridBagConstraints.BOTH;
                serviceCountsLeftPanel.add(logQCounts, gc);
                gc = new GridBagConstraints();
                gc.insets = new Insets(6, 6, 0, 5);
                gc.gridx = 0;
                gc.gridy = 0;
                gc.weightx = 1.0;
                gc.weighty = 2.0;
                gc.fill = GridBagConstraints.BOTH;
                serviceCountsLeftPanel.add(instanceInfo, gc);
                jScrollPane1.getViewport().add(serviceCountList, null);
                gc = new GridBagConstraints();
                gc.insets = new Insets(6, 6, 6, 5);
                gc.gridx = 0;
                gc.gridy = 2;
                gc.weightx = 1.0;
                gc.weighty = 1.0;
                gc.fill = GridBagConstraints.BOTH;
                serviceCountsLeftPanel.add(levelCounts, gc);
                logQCounts.setBorder(new TitledBorder(new EtchedBorder(), "Queue Statistics"));
                levelCounts.setBorder(new TitledBorder(new EtchedBorder(), "Level Totals"));
                instanceInfo.setBorder(new TitledBorder(new EtchedBorder(), "Instance Information"));
                jScrollPane3.getViewport().add(updateableCounts, null);
                jScrollPane2.getViewport().add(serviceLevelCountList, null);
                gc = new GridBagConstraints();
                gc.insets = new Insets(6, 6, 0, 5);
                gc.gridx = 1;
                gc.gridy = 0;
                gc.weightx = 1.0;
                gc.weighty = 2.0;
                gc.fill = GridBagConstraints.BOTH;
                JPanel perAppLogsPanel = new JPanel();
                perAppLogsPanel.setLayout(new BorderLayout());

                perApplicationLogs.setBorder(new TitledBorder(new EtchedBorder(), "Logs per Application"));
                perAppLogsPanel.add(perApplicationLogs, BorderLayout.CENTER);

                serviceCountsLeftPanel.add(perAppLogsPanel, gc);
                // mainTabbedPane.add(internalLogScroll, "Eros Log");
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
                gridBagConstraints.ipadx = 200;
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 2;
                gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
                this.add(refreshButton, gridBagConstraints);
                internalLogScroll.getViewport().add(internalLogPanel, null);
                perApplicationLogs.getViewport().add(appCountsTree, null);

                JPanel osPanel = new JPanel();
                osPanel.setLayout(new BorderLayout());
                osPanel.add(logsPerOS, BorderLayout.CENTER);
                logsPerOS.setBorder(new EtchedBorder());
                logsPerUser.setBorder(new EtchedBorder());
                JPanel userPanel = new JPanel();
                userPanel.setLayout(new BorderLayout());
                userPanel.add(logsPerUser, BorderLayout.CENTER);
                osPanel.setBorder(BorderFactory.createTitledBorder(new EtchedBorder(), "Logs Per OS"));
                userPanel.setBorder(BorderFactory.createTitledBorder(new EtchedBorder(), "Logs Per User"));

                gc = new GridBagConstraints();
                gc.insets = new Insets(6, 6, 0, 5);
                gc.gridx = 1;
                gc.gridy = 1;
                gc.weightx = 1.0;
                gc.weighty = 1.0;
                gc.fill = GridBagConstraints.BOTH;
                serviceCountsLeftPanel.add(osPanel, gc);

                gc = new GridBagConstraints();
                gc.insets = new Insets(6, 6, 6, 5);
                gc.gridx = 1;
                gc.gridy = 2;
                gc.weightx = 1.0;
                gc.weighty = 1.0;
                gc.fill = GridBagConstraints.BOTH;
                serviceCountsLeftPanel.add(userPanel, gc);

        }

        private void init() throws Exception {
                int selected = mainTabbedPane.getSelectedIndex();
                if (selected == -1)
                        selected = 0;
                stats = eros.getStats();
                getServiceDetails(serviceCountList);
                serviceLevelCountList.setCellRenderer(levelListRenderer);
                getLevelCounts(serviceLevelCountList);
                appCountsTree.setCellRenderer(levelTreeRenderer);
                ((DefaultTreeModel) appCountsTree.getModel()).setRoot(getApplicationDetails());
                this.getLoggingDetials(internalLogPanel);
                this.getLogCounts();
                loadRecords();
                mainTabbedPane.add(analyserPanel, "Error Analyser");
                mainTabbedPane.setSelectedIndex(selected);
        }

        private void loadRecords() {

                analyserPanel.removeAll();
                analyserPanel.setLayout(new GridBagLayout());
                GridBagConstraints gc = null;
                gc = new GridBagConstraints();
                gc.gridx = 0;
                gc.gridy = 0;
                gc.gridwidth = 2;
                gc.anchor = GridBagConstraints.CENTER;
                JLabel topLabel = new JLabel("Please select a date range:");     
               
                topLabel.setFont(new java.awt.Font("Verdana", 0, 13));
                gc.fill = GridBagConstraints.HORIZONTAL;

                gc.insets = new Insets(12, 12, 12, 12);
                analyserPanel.add(topLabel,gc);

                gc = new GridBagConstraints();
                gc.gridx = 0;
                gc.gridy = 1;
                gc.fill = GridBagConstraints.HORIZONTAL;

                gc.insets = new Insets(12, 12, 12, 12);
                JLabel fromLabel = new JLabel("From: (dd/MM/yyyy)");                
                fromLabel.setFont(new java.awt.Font("Verdana", 0, 11));
                analyserPanel.add(fromLabel,gc);
                
                
                JLabel toLabel = new JLabel("To: (dd/MM/yyyy)");
                toLabel.setFont(new java.awt.Font("Verdana", 0, 11));
                analyseDateFrom.setFont(new java.awt.Font("Verdana", 0, 11));
                analyseDateTo.setFont(new java.awt.Font("Verdana", 0, 11));
                gc = new GridBagConstraints();
                gc.gridx = 1;
                gc.gridy = 1;
                gc.weightx = 1.0;
                gc.fill = GridBagConstraints.HORIZONTAL;               
                gc.insets = new Insets(12, 12, 12, 12);
                analyserPanel.add(analyseDateFrom, gc);

                gc = new GridBagConstraints();
                gc.gridx = 0;
                gc.gridy = 2;

                gc.fill = GridBagConstraints.HORIZONTAL;

                gc.insets = new Insets(12, 12, 12, 12);
                analyserPanel.add(toLabel, gc);

                gc = new GridBagConstraints();
                gc.gridx = 1;
                gc.gridy = 2;

                gc.fill = GridBagConstraints.HORIZONTAL;
             
                gc.insets = new Insets(12, 12, 12, 12);
                analyserPanel.add(analyseDateTo, gc);

               
                
                JButton getButton = new JButton("Get Data");
                getButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                                // TODO Auto-generated method stub
                                getAllRecords = false;
                                loadRecordsFromServer();
                        }
                });
                getAllRecords = false;
                JPanel buttonPanel = new JPanel();
                buttonPanel.setLayout(new GridLayout(1,0,5,5));
                buttonPanel.add(getButton);

                JButton allButton = new JButton("All Data");
                allButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                                // TODO Auto-generated method stub
                                getAllRecords = true;
                                loadRecordsFromServer();
                        }
                });

               buttonPanel.add(allButton);
                
                gc = new GridBagConstraints();
                gc.gridx = 1;
                gc.gridy = 3;
                gc.gridwidth=2;
                gc.anchor = GridBagConstraints.EAST;
                gc.weightx = 1.0;
                gc.fill = GridBagConstraints.HORIZONTAL;
               
                gc.insets = new Insets(12, 12, 12, 12);
                analyserPanel.add(buttonPanel, gc);

        }

        private void loadRecordsFromServer() {
          
                SwingUtilities.invokeLater(new Runnable() {

                        public void run() {
                                GridBagConstraints gc;
                                analyserPanel.removeAll();
                                analyserPanel.updateUI();
                                final JProgressBar progressBar = new JProgressBar();
                                gc = new GridBagConstraints();
                                gc.gridx = 0;
                                gc.gridy = 0;
                                gc.weightx = 1.0;
                                gc.fill = GridBagConstraints.HORIZONTAL;
                                gc.weighty = 1.0;
                                gc.insets = new Insets(12, 12, 12, 12);
                                analyserPanel.add(progressBar, gc);
                                progressBar.setStringPainted(true);
                                progressBar.setMaximumSize(new Dimension(600, 24));
                                progressBar.setPreferredSize(new Dimension(600, 24));
                                progressBar.setString("Loading records from server");

                                progressBar.setIndeterminate(true);
                                Runnable loadAsyncRecords = new Runnable() {
                                        public void run() {
                                                try {
                                                        if (analyseDateFrom.getText().trim().equals("") || getAllRecords)
                                                                errorModel = eros.getErrorRecords(null, null);
                                                        else {
                                                                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                                                                try {
                                                                        java.util.Date dateFrom = format.parse(analyseDateFrom.getText());
                                                                        java.util.Date dateTo = format.parse(analyseDateTo.getText());                                                                    
                                                                        errorModel = eros.getErrorRecords(dateFrom, dateTo);
                                                                } catch (ParseException e) {
                                                                        // TODO
                                                                        // Auto-generated
                                                                        // catch
                                                                        // block
                                                                        e.printStackTrace();
                                                                        errorModel = eros.getErrorRecords(null, null);
                                                                }
                                                        }

                                                        SwingUtilities.invokeLater(new Runnable() {
                                                                public void run() {
                                                                        analyserPanel.remove(progressBar);
                                                                        analyserPanel.setLayout(new BorderLayout());
                                                                        analyserPanel.add(new ErrorViewer(errorModel), BorderLayout.CENTER);
                                                                        analyserPanel.updateUI();
                                                                };
                                                        });

                                                } catch (RemoteException e) {
                                                        // TODO Auto-generated
                                                        // catch block
                                                        SwingUtilities.invokeLater(new Runnable() {
                                                                public void run() {
                                                                        analyserPanel.remove(progressBar);
                                                                        analyserPanel.setLayout(new BorderLayout());
                                                                        analyserPanel.add(new JLabel("Unable to communicate with server"), BorderLayout.CENTER);
                                                                        analyserPanel.updateUI();
                                                                }
                                                        });
                                                }

                                        };
                                };
                                Thread t = new Thread(loadAsyncRecords);
                                t.start();
                        }
                });
        }

        private void getServiceDetails(JList list) throws Exception {
                DefaultListModel dlm = new DefaultListModel();

                dlm.addElement("Host Name : " + stats.getHostName());
                dlm.addElement("Host Address : " + stats.getHostAddress());
                dlm.addElement("Central Store : " + stats.getCentralStoreInfo());
                dlm.addElement("Total Logs : " + stats.getTotalLogs());
                dlm.addElement("Total Loggers Supplied : " + stats.getTotalLoggersSupplied());
                long upTime = stats.getUpTime();
                long days = upTime / (24 * 60 * 60 * 1000);
                long hours = (upTime - (days * 24 * 60 * 60 * 1000)) / (60 * 60 * 1000);
                long mins = (upTime - (days * 24 * 60 * 60 * 1000) - (hours * 60 * 60 * 1000)) / (60 * 1000);
                long secs = (upTime - (days * 24 * 60 * 60 * 1000) - (hours * 60 * 60 * 1000) - (mins * 60 * 1000)) / (1000);

                java.text.DecimalFormat df = new java.text.DecimalFormat("00");

                dlm.addElement("Service Uptime - " + df.format(days) + ":" + df.format(hours) + ":" + df.format(mins) + ":" + df.format(secs));

                long lastLog = stats.getLastLogTime();
                java.util.Date lastLogDate = lastLog > 0 ? new java.util.Date(lastLog) : new java.util.Date();
                dlm.addElement("Last Log : " + new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(lastLogDate));

                dlm.addElement("Total Memory     : " + stats.getTotalMemory() / 1024 + "K");
                dlm.addElement("Allocated Memory : " + (stats.getTotalMemory() - stats.getFreeMemory()) / 1024 + "K");

                list.setModel(dlm);
        }

        private void getLevelCounts(JList list) throws Exception {
                DefaultListModel dlm = new DefaultListModel();

                Hashtable levels = stats.getLogsPerLevel();
                Iterator iter = levels.entrySet().iterator();

                dlm.setSize(levels.size());
                dlm.ensureCapacity(10);

                while (iter.hasNext()) {
                        Map.Entry ent = (Map.Entry) iter.next();
                        LogLevel level = (LogLevel) ent.getKey();
                        Long count = (Long) ent.getValue();
                        // Use setElement() otherwise levels will not appear in
                        // the
                        // correct order
                        dlm.addElement(level + " - " + count);
                }
                list.setModel(dlm);
        }

        private TreeNode getApplicationDetails() {
                DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Applications");

                Hashtable appDetails = stats.getLogsPerApp();
                Iterator appIter = appDetails.entrySet().iterator();
                while (appIter.hasNext()) {
                        Map.Entry ent = (Map.Entry) appIter.next();
                        String appName = (String) ent.getKey();
                        DefaultMutableTreeNode appNode = new DefaultMutableTreeNode();

                        Hashtable appLevels = (Hashtable) ent.getValue();
                        java.util.Vector levelNodes = new java.util.Vector();
                        levelNodes.setSize(appLevels.size());

                        Iterator levelIter = appLevels.entrySet().iterator();
                        long appTotal = 0;
                        while (levelIter.hasNext()) {
                                Map.Entry levelEntry = (Map.Entry) levelIter.next();
                                LogLevel level = (LogLevel) levelEntry.getKey();
                                Long levelCount = (Long) levelEntry.getValue();
                                appTotal += levelCount.longValue();
                                DefaultMutableTreeNode levelNode = new DefaultMutableTreeNode(level + " - " + levelCount);
                                levelNodes.add(levelNode);
                        }

                        for (int i = 0; i < levelNodes.size(); i++) {
                                MutableTreeNode node = (MutableTreeNode) levelNodes.get(i);
                                if (node != null) {
                                        appNode.add(node);
                                }
                        }

                        appNode.setUserObject(appName + " (" + appTotal + ")");
                        rootNode.add(appNode);
                }

                return rootNode;
        }

        private void getLogCounts() {
                Hashtable lpOS = this.stats.getLogsPerOS();
                Hashtable lpUser = this.stats.getLogsPerUser();

                DefaultListModel dlm = new DefaultListModel();

                Iterator iter = lpOS.entrySet().iterator();
                while (iter.hasNext()) {
                        Map.Entry ent = (Map.Entry) iter.next();
                        dlm.addElement(ent.getKey() + " - " + ent.getValue());
                }
                logsPerOS.setModel(dlm);
                logsPerOS.updateUI();

                dlm = new DefaultListModel();

                iter = lpUser.entrySet().iterator();
                while (iter.hasNext()) {
                        Map.Entry ent = (Map.Entry) iter.next();
                        dlm.addElement(ent.getKey() + " - " + ent.getValue());
                }
                logsPerUser.setModel(dlm);
                logsPerUser.updateUI();
        }

        private JPanel getOtherDetails() {
                JPanel panel = new JPanel(new BorderLayout());
                return panel;
        }

        private void getLoggingDetials(JEditorPane editor) {
                editor.setEditable(false);
                editor.setText(stats.getInternalLog());
        }

        class UpdateThread
                        extends
                        Thread {
                public UpdateThread() {
                        super("MainUIUpdateThread");
                }

                public void run() {
                        while (true) {
                                try {
                                        DefaultListModel dlm = new DefaultListModel();
                                        dlm.addElement("Logs Recieved : " + eros.getRecievedCount());
                                        dlm.addElement("Worker Queue Size : " + eros.getCurrentQueueSize());
                                        dlm.addElement("Error Queue Size  : " + eros.getErrorQueueSize());
                                        updateableCounts.setModel(dlm);
                                } catch (Exception exc) {
                                        exc.printStackTrace();
                                }
                                try {
                                        Thread.sleep(2000);
                                } catch (Exception exc) {
                                }
                        }
                }
        }

        class LevelTreeRenderer
                        extends
                        DefaultTreeCellRenderer {

                private ImageIcon fatalIcon = null;

                private ImageIcon errorIcon = null;

                private ImageIcon warnIcon = null;

                private ImageIcon infoIcon = null;

                private ImageIcon debugIcon = null;

                private ImageIcon appIcon = null;

                public LevelTreeRenderer() {
                        fatalIcon = new ImageIcon(this.getClass().getResource("con-red.gif"));
                        errorIcon = new ImageIcon(this.getClass().getResource("con-pink.gif"));
                        warnIcon = new ImageIcon(this.getClass().getResource("con-oran.gif"));
                        infoIcon = new ImageIcon(this.getClass().getResource("con-green.gif"));
                        debugIcon = new ImageIcon(this.getClass().getResource("con-blue.gif"));
                        appIcon = new ImageIcon(this.getClass().getResource("computer-green.gif"));
                }

                public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

                        setEnabled(tree.isEnabled());
                        if (sel) {
                                setForeground(getTextSelectionColor());
                        } else {
                                setForeground(getTextNonSelectionColor());
                        }

                        String stringVal = tree.convertValueToText(value, sel, expanded, leaf, row, hasFocus);
                        if (leaf) {
                                if (stringVal.toUpperCase().startsWith("FATAL")) {
                                        setIcon(fatalIcon);
                                } else if (stringVal.toUpperCase().startsWith("ERROR")) {
                                        setIcon(errorIcon);
                                } else if (stringVal.toUpperCase().startsWith("WARN")) {
                                        setIcon(warnIcon);
                                } else if (stringVal.toUpperCase().startsWith("INFO")) {
                                        setIcon(infoIcon);
                                } else if (stringVal.toUpperCase().startsWith("DEBUG")) {
                                        setIcon(debugIcon);
                                } else {
                                        setIcon(null);
                                }
                                stringVal = stringVal.toUpperCase();
                        } else {
                                if (row != 0) {
                                        setIcon(appIcon);
                                } else {
                                        setIcon(null);
                                }
                        }

                        setText(stringVal);

                        this.hasFocus = hasFocus;
                        this.selected = sel;
                        return this;
                }
        }

        class LevelListRenderer
                        extends
                        DefaultListCellRenderer {

                private ImageIcon fatalIcon = null;

                private ImageIcon errorIcon = null;

                private ImageIcon warnIcon = null;

                private ImageIcon infoIcon = null;

                private ImageIcon debugIcon = null;

                public LevelListRenderer() {
                        fatalIcon = new ImageIcon(this.getClass().getResource("con-red.gif"));
                        errorIcon = new ImageIcon(this.getClass().getResource("con-pink.gif"));
                        warnIcon = new ImageIcon(this.getClass().getResource("con-oran.gif"));
                        infoIcon = new ImageIcon(this.getClass().getResource("con-green.gif"));
                        debugIcon = new ImageIcon(this.getClass().getResource("con-blue.gif"));
                }

                public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

                        setEnabled(list.isEnabled());
                        String stringVal = "";
                        if (value != null)
                                stringVal = value.toString();
                        setText(stringVal.toUpperCase());

                        this.setBackground(list.getBackground());
                        this.setForeground(list.getForeground());

                        if (stringVal.toUpperCase().startsWith("FATAL")) {
                                setIcon(fatalIcon);
                        } else if (stringVal.toUpperCase().startsWith("ERROR")) {
                                setIcon(errorIcon);
                        } else if (stringVal.toUpperCase().startsWith("WARN")) {
                                setIcon(warnIcon);
                        } else if (stringVal.toUpperCase().startsWith("INFO")) {
                                setIcon(infoIcon);
                        } else if (stringVal.toUpperCase().startsWith("DEBUG")) {
                                setIcon(debugIcon);
                        } else {
                                setIcon(null);
                        }

                        return this;
                }
        }

        /*
         * class LevelTreeItem { private int level; private long count;
         * 
         * public LevelTreeItem(int level, long count) { this.level = level;
         * this.count = count; }
         * 
         * public int getLevel() { return level; }
         * 
         * public String getLevelName() { String levelName = ""; switch( level ) {
         * case 4 : levelName = "FATAL"; break; case 3 : levelName = "ERROR";
         * break; case 2 : levelName = "WARN"; break; case 1 : levelName =
         * "INFO"; break; case 0 : levelName = "DEBUG"; break; } return
         * levelName; }
         * 
         * public long getCount() { return count; } }
         */

        class RefreshAction implements java.awt.event.ActionListener {
                public void actionPerformed(ActionEvent e) {
                        try {
                                init();
                        } catch (Exception exc) {
                                exc.printStackTrace();
                        }

                }
        }

        public static void main(String[] args) {
                try {

                        System.setSecurityManager(new java.rmi.RMISecurityManager());

                        Object obj = new Object();
                        net.jini.discovery.LookupDiscovery ld = new net.jini.discovery.LookupDiscovery(net.jini.discovery.LookupDiscovery.NO_GROUPS);

                        ld.setGroups(args);

                        System.out.println("Waiting.....");
                        for (int i = 0; i < 200; i++) {
                                try {
                                        Thread.yield();
                                        Thread.sleep(25);
                                } catch (Exception exc) {

                                }
                        }

                        net.jini.core.lookup.ServiceRegistrar[] sr = ld.getRegistrars();
                        System.out.println("Number of registrars found = " + sr.length);
                        Class[] classes = new Class[] { Class.forName("org.jini.projects.eros.ErosService") };
                        ErosServiceUI erosUI = null;
                        for (int i = 0; i < sr.length && erosUI == null; i++) {
                                erosUI = (ErosServiceUI) sr[i].lookup(new net.jini.core.lookup.ServiceTemplate(null, classes, null));
                        }

                        if (erosUI != null) {
                                ErosMainUI mainUI = new ErosMainUI(erosUI);
                                JFrame jf = new JFrame();
                                jf.setDefaultCloseOperation(jf.EXIT_ON_CLOSE);
                                jf.getContentPane().add(mainUI);
                                jf.setSize(mainUI.getPreferredSize());
                                jf.show();
                        } else {
                                System.out.println("Unable to locate Eros service");
                        }

                } catch (Exception exc) {
                        exc.printStackTrace();
                }
        }

}
