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

/*
 * ErrorViewer.java
 *
 * Created on 13 February 2006, 15:29
 */

package org.blarty.eros.ui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.Date;
import java.util.Iterator;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.blarty.eros.ui.erroranalyser.model.DefaultErrorModel;
import org.blarty.eros.ui.erroranalyser.model.DefaultErrorRecord;
import org.blarty.eros.ui.erroranalyser.model.ErrorModel;
import org.blarty.eros.ui.erroranalyser.model.ErrorRecord;
import org.blarty.eros.ui.erroranalyser.model.ErrorTableModel;

/**
 * 
 * @author calum.mackay
 */
public class ErrorViewer
                extends
                javax.swing.JPanel {

        ErrorView errView = new ErrorView();

        ErrorView errView2 = new ErrorView();

        ErrorView errView3 = new ErrorView();

        ErrorView errView4 = new ErrorView();

        ErrorView errView5 = new ErrorView();

        ErrorView errView6 = new ErrorView();

        DefaultListModel[] legendModels = new DefaultListModel[6];

        ErrorModel model = null;

        /** Creates new form ErrorViewer */
        public ErrorViewer(ErrorModel model) {

                this.model = model;

                initComponents();
                jTable1.setModel(new ErrorTableModel(model));
                jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                TableColumn column = null;

                for (int i = 0; i < 7; i++) {
                        column = jTable1.getColumnModel().getColumn(i);
                        column.sizeWidthToFit();
                        switch (i) {
                        case 3:
                                column.setMinWidth(200);
                                break;
                        case 4:
                                column.setMinWidth(50);
                                column.setWidth(50);
                                column.setPreferredWidth(50);
                                break;
                        default:
                                column.setMinWidth(60);
                                column.sizeWidthToFit();
                        }
                        model.addFilterListener(new FilterListener() {
                                public void filterAdded(String filter, String value) {
                                        jTabbedPane1.setSelectedIndex(0);
                                };

                                public void filterChanged(String filter, String value) {
                                        // TODO Auto-generated method stub
                                        jTabbedPane1.setSelectedIndex(0);
                                }

                                public void filterRemoved(String filter) {
                                        // TODO Auto-generated method stub
                                        jTabbedPane1.setSelectedIndex(0);
                                }
                        });
                        jTable1.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
                        ListSelectionModel rowSM = jTable1.getSelectionModel();
                        rowSM.addListSelectionListener(new ListSelectionListener() {
                                public void valueChanged(ListSelectionEvent e) {
                                        
                                        displayErrorRecord(jTable1.getSelectedRow());
                                }
                        });

                }

                for (int i = 0; i < legendModels.length; i++) {
                        legendModels[i] = new DefaultListModel();
                }

                jScrollPane4.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                setupGraphPanel(errView, model, graphPanel1, ErrorView.APPLICATION_VIEW_INDICATOR, legendModels[0]);
                setupGraphPanel(errView2, model, graphPanel2, ErrorView.EXCEPTIONNAME_VIEW_INDICATOR, legendModels[1]);
                setupGraphPanel(errView3, model, graphPanel3, ErrorView.MESSAGE_VIEW_INDICATOR, legendModels[2]);
                setupGraphPanel(errView4, model, graphPanel4, ErrorView.CLASSNAME_VIEW_INDICATOR, legendModels[3]);
                setupGraphPanel(errView5, model, graphPanel5, ErrorView.METHODNAME_VIEW_INDICATOR, legendModels[4]);
                setupGraphPanel(errView6, model, graphPanel6, ErrorView.ADDRESS_VIEW_INDICATOR, legendModels[5]);
                
                // jScrollPane1.invalidate();
                setupList(legendList1, legendModels[0],model,ErrorView.APPLICATION_FILTER);
                setupList(legendList2, legendModels[1], model, ErrorView.EXCEPTION_FILTER);
                setupList(legendList3, legendModels[2],model ,ErrorView.MESSAGE_FILTER);
                setupList(legendList4, legendModels[3],model, ErrorView.CLASS_FILTER);
                setupList(legendList5, legendModels[4],model, ErrorView.METHOD_FILTER );
                setupList(legendList6, legendModels[5], model, ErrorView.IPADDRESS_FILTER);

        }

        private void setupList(JList list, DefaultListModel model, final ErrorModel errmodel, final String filter) {
                list.setCellRenderer(new LegendListRenderer());
                list.setModel(model);
                
//                list.addListSelectionListener(new ListSelectionListener(){
//                        public void valueChanged(ListSelectionEvent e) {
//                                // TODO Auto-generated method stub
//                          JList list = (JList) e.getSource();                               
//                             final  LegendItem item = (LegendItem) list.getSelectedValue();
//                              Thread t = new Thread(new Runnable(){
//                                      public void run() {
//                                              errmodel.addFilter(filter,item.getLabel());
//                                      }
//                              });
//                              t.start();
//                             
//                        }
//                });
        }

        private void displayErrorRecord(int index) {
                if (index > -1) {
                        ErrorRecord record = model.getRecord(index);

                        this.errorAppName.setText(record.getApplicationName());
                        this.errorMessage.setText(record.getErrorMessage());
                        this.errorAddress.setText(record.getIPAddress());
                        this.errorStackTrace.setText(record.getStackTrace());
                        this.errorArguments.setText(record.getArguments());
                        this.errorMethodName.setText(record.getMethodName());
                        this.errorExceptionName.setText(record.getExceptionName());
                        this.errorClassName.setText(record.getClassName());
                        this.errorGroup.setText(record.getGroup());
                        this.errorTime.setText(record.getTime().toString());
                        jTabbedPane1.setSelectedIndex(1);
                } else
                        jTabbedPane1.setSelectedIndex(0);
        }

        private void setUpColumn(TableColumnModel tcm, int index, int minWidth, int maxWidth) {
                TableColumn tc = new TableColumn(index);
                tc.setMinWidth(minWidth);
                tc.setMaxWidth(maxWidth);
                tc.sizeWidthToFit();
                tcm.addColumn(tc);
        }

        private void setupGraphPanel(final ErrorView errorView, final ErrorModel model, final JPanel container, int view, final DefaultListModel legendModel) {
                errorView.setLegendModel(legendModel);
                errorView.setErrorModel(model);
                errorView.setGraphType(ErrorView.CUMULATIVE_BAR);
                errorView.setViewIndicator(view);
                errorView.intiialiseLegend();
                GridBagConstraints gc = new GridBagConstraints();
                gc.insets = new Insets(3, 3, 3, 3);
                gc.gridx = 0;
                gc.gridy = GridBagConstraints.RELATIVE;
                gc.weightx = 1.0;
                gc.weighty = 1.0;
                gc.fill = GridBagConstraints.BOTH;

                errorView.setBorder(BorderFactory.createEtchedBorder());

                container.add(errorView, BorderLayout.CENTER);

        }

        /**
         * This method is called from within the constructor to initialize the
         * form. WARNING: Do NOT modify this code. The content of this method is
         * always regenerated by the Form Editor.
         */
        // <editor-fold defaultstate="collapsed" desc=" Generated Code
        // ">//GEN-BEGIN:initComponents
        private void initComponents() {
                java.awt.GridBagConstraints gridBagConstraints;

                jPanel1 = new javax.swing.JPanel();
                jLabel4 = new javax.swing.JLabel();
                jPanel4 = new javax.swing.JPanel();
                jScrollPane4 = new javax.swing.JScrollPane();
                jPanel3 = new javax.swing.JPanel();
                graphPanel1 = new javax.swing.JPanel();
                jScrollPane1 = new javax.swing.JScrollPane();
                legendList1 = new javax.swing.JList();
                graphPanel2 = new javax.swing.JPanel();
                jScrollPane8 = new javax.swing.JScrollPane();
                legendList2 = new javax.swing.JList();
                graphPanel3 = new javax.swing.JPanel();
                jScrollPane7 = new javax.swing.JScrollPane();
                legendList3 = new javax.swing.JList();
                graphPanel4 = new javax.swing.JPanel();
                jScrollPane6 = new javax.swing.JScrollPane();
                legendList4 = new javax.swing.JList();
                graphPanel5 = new javax.swing.JPanel();
                jScrollPane5 = new javax.swing.JScrollPane();
                legendList5 = new javax.swing.JList();
                graphPanel6 = new javax.swing.JPanel();
                jScrollPane9 = new javax.swing.JScrollPane();
                legendList6 = new javax.swing.JList();
                jTabbedPane1 = new javax.swing.JTabbedPane();
                jScrollPane2 = new javax.swing.JScrollPane();
                jTable1 = new javax.swing.JTable();
                jPanel2 = new javax.swing.JPanel();
                jLabel2 = new javax.swing.JLabel();
                errorAppName = new javax.swing.JLabel();
                jLabel5 = new javax.swing.JLabel();
                errorMessage = new javax.swing.JLabel();
                jLabel7 = new javax.swing.JLabel();
                errorTime = new javax.swing.JLabel();
                jLabel9 = new javax.swing.JLabel();
                errorClassName = new javax.swing.JLabel();
                jLabel11 = new javax.swing.JLabel();
                jLabel12 = new javax.swing.JLabel();
                errorMethodName = new javax.swing.JLabel();
                errorExceptionName = new javax.swing.JLabel();
                jLabel15 = new javax.swing.JLabel();
                errorAddress = new javax.swing.JLabel();
                jLabel17 = new javax.swing.JLabel();
                errorArguments = new javax.swing.JLabel();
                jLabel19 = new javax.swing.JLabel();
                errorGroup = new javax.swing.JLabel();
                jLabel21 = new javax.swing.JLabel();
                jScrollPane3 = new javax.swing.JScrollPane();
                errorStackTrace = new javax.swing.JTextArea();

                setLayout(new java.awt.BorderLayout(5, 5));

                jPanel1.setLayout(new java.awt.BorderLayout(5, 5));

                jPanel1.setPreferredSize(new java.awt.Dimension(1000, 600));
                jLabel4.setText(" ");
                jPanel1.add(jLabel4, java.awt.BorderLayout.SOUTH);

                jPanel4.setLayout(new java.awt.BorderLayout());

                jScrollPane4.setDoubleBuffered(true);
                jPanel3.setLayout(new java.awt.GridBagLayout());

                jPanel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
                graphPanel1.setLayout(new java.awt.BorderLayout());

                graphPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
                jScrollPane1.setMinimumSize(new java.awt.Dimension(250, 23));
                jScrollPane1.setPreferredSize(new java.awt.Dimension(250, 70));
                legendList1.setFont(new java.awt.Font("Verdana", 0, 9));
                legendList1.setModel(new javax.swing.AbstractListModel() {
                        String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };

                        public int getSize() {
                                return strings.length;
                        }

                        public Object getElementAt(int i) {
                                return strings[i];
                        }
                });
                jScrollPane1.setViewportView(legendList1);

                graphPanel1.add(jScrollPane1, java.awt.BorderLayout.EAST);

                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
                gridBagConstraints.weightx = 1.0;
                jPanel3.add(graphPanel1, gridBagConstraints);

                graphPanel2.setLayout(new java.awt.BorderLayout());

                graphPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
                jScrollPane8.setMinimumSize(new java.awt.Dimension(200, 23));
                jScrollPane8.setPreferredSize(new java.awt.Dimension(250, 70));
                legendList2.setFont(new java.awt.Font("Verdana", 0, 9));
                legendList2.setModel(new javax.swing.AbstractListModel() {
                        String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };

                        public int getSize() {
                                return strings.length;
                        }

                        public Object getElementAt(int i) {
                                return strings[i];
                        }
                });
                jScrollPane8.setViewportView(legendList2);

                graphPanel2.add(jScrollPane8, java.awt.BorderLayout.EAST);

                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 1;
                gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
                gridBagConstraints.weightx = 1.0;
                jPanel3.add(graphPanel2, gridBagConstraints);

                graphPanel3.setLayout(new java.awt.BorderLayout());

                graphPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
                jScrollPane7.setMinimumSize(new java.awt.Dimension(200, 23));
                jScrollPane7.setPreferredSize(new java.awt.Dimension(250, 70));
                legendList3.setFont(new java.awt.Font("Verdana", 0, 9));
                legendList3.setModel(new javax.swing.AbstractListModel() {
                        String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };

                        public int getSize() {
                                return strings.length;
                        }

                        public Object getElementAt(int i) {
                                return strings[i];
                        }
                });
                jScrollPane7.setViewportView(legendList3);

                graphPanel3.add(jScrollPane7, java.awt.BorderLayout.EAST);

                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 2;
                gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
                gridBagConstraints.weightx = 1.0;
                jPanel3.add(graphPanel3, gridBagConstraints);

                graphPanel4.setLayout(new java.awt.BorderLayout());

                graphPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
                jScrollPane6.setMinimumSize(new java.awt.Dimension(200, 23));
                jScrollPane6.setPreferredSize(new java.awt.Dimension(250, 70));
                legendList4.setFont(new java.awt.Font("Verdana", 0, 9));
                legendList4.setModel(new javax.swing.AbstractListModel() {
                        String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };

                        public int getSize() {
                                return strings.length;
                        }

                        public Object getElementAt(int i) {
                                return strings[i];
                        }
                });
                jScrollPane6.setViewportView(legendList4);

                graphPanel4.add(jScrollPane6, java.awt.BorderLayout.EAST);

                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 3;
                gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
                gridBagConstraints.weightx = 1.0;
                jPanel3.add(graphPanel4, gridBagConstraints);

                graphPanel5.setLayout(new java.awt.BorderLayout());

                graphPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());
                jScrollPane5.setMinimumSize(new java.awt.Dimension(200, 23));
                jScrollPane5.setPreferredSize(new java.awt.Dimension(250, 70));
                legendList5.setFont(new java.awt.Font("Verdana", 0, 9));
                legendList5.setModel(new javax.swing.AbstractListModel() {
                        String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };

                        public int getSize() {
                                return strings.length;
                        }

                        public Object getElementAt(int i) {
                                return strings[i];
                        }
                });
                jScrollPane5.setViewportView(legendList5);

                graphPanel5.add(jScrollPane5, java.awt.BorderLayout.EAST);

                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 4;
                gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
                gridBagConstraints.weightx = 1.0;
                jPanel3.add(graphPanel5, gridBagConstraints);

                graphPanel6.setLayout(new java.awt.BorderLayout());

                graphPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder());
                jScrollPane9.setMinimumSize(new java.awt.Dimension(200, 23));
                jScrollPane9.setPreferredSize(new java.awt.Dimension(250, 70));
                legendList6.setFont(new java.awt.Font("Verdana", 0, 9));
                legendList6.setModel(new javax.swing.AbstractListModel() {
                        String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };

                        public int getSize() {
                                return strings.length;
                        }

                        public Object getElementAt(int i) {
                                return strings[i];
                        }
                });
                jScrollPane9.setViewportView(legendList6);

                graphPanel6.add(jScrollPane9, java.awt.BorderLayout.EAST);

                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 5;
                gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
                gridBagConstraints.weightx = 1.0;
                jPanel3.add(graphPanel6, gridBagConstraints);

                jScrollPane4.setViewportView(jPanel3);
                
              

                jTabbedPane1.setFont(new java.awt.Font("Verdana", 0, 11));
                jScrollPane2.setBorder(null);
                jScrollPane2.setFont(new java.awt.Font("Verdana", 0, 11));
                jScrollPane2.setPreferredSize(new java.awt.Dimension(452, 252));
                jTable1.setModel(new javax.swing.table.DefaultTableModel(new Object[][] { { null, null, null, null }, { null, null, null, null }, { null, null, null, null }, { null, null, null, null } }, new String[] { "Title 1", "Title 2", "Title 3", "Title 4" }));
                jScrollPane2.setViewportView(jTable1);

                jTabbedPane1.addTab("Error List", jScrollPane2);

                jPanel2.setLayout(new java.awt.GridBagLayout());

                jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
                jPanel2.setFont(new java.awt.Font("Tahoma", 1, 11));
                jLabel2.setFont(new java.awt.Font("Verdana", 0, 11));
                jLabel2.setText("Application Name:");
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 0;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
                gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
                jPanel2.add(jLabel2, gridBagConstraints);

                errorAppName.setBackground(new java.awt.Color(255, 255, 255));
                errorAppName.setFont(new java.awt.Font("Verdana", 0, 11));
                errorAppName.setText("\n");
                errorAppName.setBorder(javax.swing.BorderFactory.createEtchedBorder());
                errorAppName.setOpaque(true);
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 1;
                gridBagConstraints.gridy = 0;
                gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
                gridBagConstraints.weightx = 1.0;
                gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
                jPanel2.add(errorAppName, gridBagConstraints);

                jLabel5.setFont(new java.awt.Font("Verdana", 0, 11));
                jLabel5.setText("Error Message:");
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 1;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
                gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
                jPanel2.add(jLabel5, gridBagConstraints);

                errorMessage.setBackground(new java.awt.Color(255, 255, 255));
                errorMessage.setFont(new java.awt.Font("Verdana", 0, 11));
                errorMessage.setText("\n");
                errorMessage.setBorder(javax.swing.BorderFactory.createEtchedBorder());
                errorMessage.setOpaque(true);
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 1;
                gridBagConstraints.gridy = 1;
                gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
                gridBagConstraints.weightx = 1.0;
                gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
                jPanel2.add(errorMessage, gridBagConstraints);

                jLabel7.setFont(new java.awt.Font("Verdana", 0, 11));
                jLabel7.setText("Time:");
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 2;
                gridBagConstraints.gridy = 0;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
                gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
                jPanel2.add(jLabel7, gridBagConstraints);

                errorTime.setBackground(new java.awt.Color(255, 255, 255));
                errorTime.setFont(new java.awt.Font("Verdana", 0, 11));
                errorTime.setText("\n");
                errorTime.setBorder(javax.swing.BorderFactory.createEtchedBorder());
                errorTime.setOpaque(true);
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 3;
                gridBagConstraints.gridy = 0;
                gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
                gridBagConstraints.weightx = 1.0;
                gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
                jPanel2.add(errorTime, gridBagConstraints);

                jLabel9.setFont(new java.awt.Font("Verdana", 0, 11));
                jLabel9.setText("Class Name:");
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 2;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
                gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
                jPanel2.add(jLabel9, gridBagConstraints);

                errorClassName.setBackground(new java.awt.Color(255, 255, 255));
                errorClassName.setFont(new java.awt.Font("Verdana", 0, 11));
                errorClassName.setText("\n");
                errorClassName.setBorder(javax.swing.BorderFactory.createEtchedBorder());
                errorClassName.setOpaque(true);
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 1;
                gridBagConstraints.gridy = 2;
                gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
                gridBagConstraints.weightx = 1.0;
                gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
                jPanel2.add(errorClassName, gridBagConstraints);

                jLabel11.setFont(new java.awt.Font("Verdana", 0, 11));
                jLabel11.setText("Method Name:");
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 2;
                gridBagConstraints.gridy = 2;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
                gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
                jPanel2.add(jLabel11, gridBagConstraints);

                jLabel12.setFont(new java.awt.Font("Verdana", 0, 11));
                jLabel12.setText("Exception Name:");
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 2;
                gridBagConstraints.gridy = 1;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
                gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
                jPanel2.add(jLabel12, gridBagConstraints);

                errorMethodName.setBackground(new java.awt.Color(255, 255, 255));
                errorMethodName.setFont(new java.awt.Font("Verdana", 0, 11));
                errorMethodName.setText("\n");
                errorMethodName.setBorder(javax.swing.BorderFactory.createEtchedBorder());
                errorMethodName.setOpaque(true);
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 3;
                gridBagConstraints.gridy = 2;
                gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
                gridBagConstraints.weightx = 1.0;
                gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
                jPanel2.add(errorMethodName, gridBagConstraints);

                errorExceptionName.setBackground(new java.awt.Color(255, 255, 255));
                errorExceptionName.setFont(new java.awt.Font("Verdana", 0, 11));
                errorExceptionName.setText("\n");
                errorExceptionName.setBorder(javax.swing.BorderFactory.createEtchedBorder());
                errorExceptionName.setOpaque(true);
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 3;
                gridBagConstraints.gridy = 1;
                gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
                gridBagConstraints.weightx = 1.0;
                gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
                jPanel2.add(errorExceptionName, gridBagConstraints);

                jLabel15.setFont(new java.awt.Font("Verdana", 0, 11));
                jLabel15.setText("Arguments");
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 2;
                gridBagConstraints.gridy = 3;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
                gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
                jPanel2.add(jLabel15, gridBagConstraints);

                errorAddress.setBackground(new java.awt.Color(255, 255, 255));
                errorAddress.setFont(new java.awt.Font("Verdana", 0, 11));
                errorAddress.setText("\n");
                errorAddress.setBorder(javax.swing.BorderFactory.createEtchedBorder());
                errorAddress.setOpaque(true);
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 1;
                gridBagConstraints.gridy = 3;
                gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
                gridBagConstraints.weightx = 1.0;
                gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
                jPanel2.add(errorAddress, gridBagConstraints);

                jLabel17.setFont(new java.awt.Font("Verdana", 0, 11));
                jLabel17.setText("Jini Group");
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 4;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
                gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
                jPanel2.add(jLabel17, gridBagConstraints);

                errorArguments.setBackground(new java.awt.Color(255, 255, 255));
                errorArguments.setFont(new java.awt.Font("Verdana", 0, 11));
                errorArguments.setText("\n");
                errorArguments.setBorder(javax.swing.BorderFactory.createEtchedBorder());
                errorArguments.setOpaque(true);
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 3;
                gridBagConstraints.gridy = 3;
                gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
                gridBagConstraints.weightx = 1.0;
                gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
                jPanel2.add(errorArguments, gridBagConstraints);

                jLabel19.setFont(new java.awt.Font("Verdana", 0, 11));
                jLabel19.setText("IP Address");
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 3;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
                gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
                jPanel2.add(jLabel19, gridBagConstraints);

                errorGroup.setBackground(new java.awt.Color(255, 255, 255));
                errorGroup.setFont(new java.awt.Font("Verdana", 0, 11));
                errorGroup.setText("\n");
                errorGroup.setBorder(javax.swing.BorderFactory.createEtchedBorder());
                errorGroup.setOpaque(true);
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 1;
                gridBagConstraints.gridy = 4;
                gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
                gridBagConstraints.weightx = 1.0;
                gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 6);
                jPanel2.add(errorGroup, gridBagConstraints);

                jLabel21.setFont(new java.awt.Font("Verdana", 0, 11));
                jLabel21.setText("Stack Trace");
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 5;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
                gridBagConstraints.insets = new java.awt.Insets(6, 6, 0, 0);
                jPanel2.add(jLabel21, gridBagConstraints);

                errorStackTrace.setColumns(20);
                errorStackTrace.setFont(new java.awt.Font("Verdana", 0, 11));
                errorStackTrace.setRows(5);
                jScrollPane3.setViewportView(errorStackTrace);

                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 1;
                gridBagConstraints.gridy = 5;
                gridBagConstraints.gridwidth = 3;
                gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
                gridBagConstraints.ipadx = 100;
                gridBagConstraints.weightx = 1.0;
                gridBagConstraints.weighty = 1.0;
                gridBagConstraints.insets = new java.awt.Insets(6, 6, 12, 6);
                jPanel2.add(jScrollPane3, gridBagConstraints);

                jTabbedPane1.addTab("Error Detail", jPanel2);
                JSplitPane splitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
                splitter.setLeftComponent(jScrollPane4);
                splitter.setRightComponent(jTabbedPane1);
               splitter.setDividerLocation(400);
               splitter.setOneTouchExpandable(true);
                jPanel4.add(splitter, java.awt.BorderLayout.CENTER);
              

                jPanel1.add(jPanel4, java.awt.BorderLayout.CENTER);

                add(jPanel1, java.awt.BorderLayout.CENTER);

        }// </editor-fold>//GEN-END:initComponents

        /**
         * @param args
         *                the command line arguments
         */
        public static void main(String args[]) {
                java.awt.EventQueue.invokeLater(new Runnable() {
                        public void run() {
                                JFrame viewer = new JFrame("Eros Error Analyser");
                                ErrorViewer err_viewer = new ErrorViewer(null);
                                viewer.getContentPane().setLayout(new BorderLayout());
                                viewer.add(err_viewer, BorderLayout.CENTER);
                                viewer.setVisible(true);
                                viewer.setSize(800, 600);
                                viewer.pack();

                        }
                });
        }

        public void setErrorModel(ErrorModel model) {

        }

        // Variables declaration - do not modify//GEN-BEGIN:variables
        private javax.swing.JLabel errorAddress;

        private javax.swing.JLabel errorAppName;

        private javax.swing.JLabel errorArguments;

        private javax.swing.JLabel errorClassName;

        private javax.swing.JLabel errorExceptionName;

        private javax.swing.JLabel errorGroup;

        private javax.swing.JLabel errorMessage;

        private javax.swing.JLabel errorMethodName;

        private javax.swing.JTextArea errorStackTrace;

        private javax.swing.JLabel errorTime;

        private javax.swing.JPanel graphPanel1;

        private javax.swing.JPanel graphPanel2;

        private javax.swing.JPanel graphPanel3;

        private javax.swing.JPanel graphPanel4;

        private javax.swing.JPanel graphPanel5;

        private javax.swing.JPanel graphPanel6;

        private javax.swing.JLabel jLabel11;

        private javax.swing.JLabel jLabel12;

        private javax.swing.JLabel jLabel15;

        private javax.swing.JLabel jLabel17;

        private javax.swing.JLabel jLabel19;

        private javax.swing.JLabel jLabel2;

        private javax.swing.JLabel jLabel21;

        private javax.swing.JLabel jLabel4;

        private javax.swing.JLabel jLabel5;

        private javax.swing.JLabel jLabel7;

        private javax.swing.JLabel jLabel9;

        private javax.swing.JPanel jPanel1;

        private javax.swing.JPanel jPanel2;

        private javax.swing.JPanel jPanel3;

        private javax.swing.JPanel jPanel4;

        private javax.swing.JScrollPane jScrollPane1;

        private javax.swing.JScrollPane jScrollPane2;

        private javax.swing.JScrollPane jScrollPane3;

        private javax.swing.JScrollPane jScrollPane4;

        private javax.swing.JScrollPane jScrollPane5;

        private javax.swing.JScrollPane jScrollPane6;

        private javax.swing.JScrollPane jScrollPane7;

        private javax.swing.JScrollPane jScrollPane8;

        private javax.swing.JScrollPane jScrollPane9;

        private javax.swing.JTabbedPane jTabbedPane1;

        private javax.swing.JTable jTable1;

        private javax.swing.JList legendList1;

        private javax.swing.JList legendList2;

        private javax.swing.JList legendList3;

        private javax.swing.JList legendList4;

        private javax.swing.JList legendList5;

        private javax.swing.JList legendList6;
        // End of variables declaration//GEN-END:variables

}
