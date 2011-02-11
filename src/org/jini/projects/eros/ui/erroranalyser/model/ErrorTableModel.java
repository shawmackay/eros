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
 * ErrorTableModel.java
 *
 * Created on 14 March 2006, 22:14
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jini.projects.eros.ui.erroranalyser.model;

import java.util.ArrayList;
import java.util.Date;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.jini.projects.eros.ui.FilterListener;

/**
 *
 * @author calum.mackay
 */
public class ErrorTableModel implements TableModel{
    
    private ErrorModel model;
    
    private ArrayList listeners = new ArrayList();
    
    private FilterListener myListener = new FilterListener() {
        public void filterAdded(String filter, String value) {
            fireTableChangeListeners();
        }
        public void filterChanged(String filter, String value) {
            fireTableChangeListeners();
        }
        public void filterRemoved(String filter) {
            fireTableChangeListeners();
        }
    };
    
    public ErrorTableModel(ErrorModel model){
        this.model = model;
        model.addFilterListener(myListener);
    }
    public int getRowCount() {
        return (int)model.getNumRecords();
    }

    public int getColumnCount() {
        return 7;
    }

    public String getColumnName(int columnIndex) {
        switch(columnIndex){
            case 0: return "Application Name";
            case 1: return "Error Message";
            case 2: return "Time";
            case 3: return "IP Address";
            case 4: return "Level";
            case 5: return "Method";            
            case 6: return "Exception";
            default: return "Unknown";
        }
    }

    public Class getColumnClass(int columnIndex) {
        switch(columnIndex){
            case 2: return Date.class;
            
            default: return String.class;
        }
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        ErrorRecord record = model.getRecord(rowIndex);
        if(record!=null){
        switch(columnIndex){
            case 0: return record.getApplicationName();
            case 1: return record.getErrorMessage();
            case 2: return record.getTime();
            case 3: return record.getIPAddress();
            case 4: return record.getLevel();
            //case 5: return record.getGroup();
            //case 6: return record.getArguments();
            //case 7: return record.getStackTrace();
            case 5: return record.getClassName() + "." + record.getMethodName();            
            case 6: return record.getExceptionName();
            default: return "Unknown";
        }
        }
        return "";
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    }

    public void addTableModelListener(TableModelListener l) {
        
        listeners.add(l);
    }

    public void removeTableModelListener(TableModelListener l) {
        System.out.println("Removing Table Listener");        
        listeners.remove(l);
    }
    
    public void setErrorModel(ErrorModel model){
        this.model.removeFilterListener(myListener);
        this.model = model;
        this.model.addFilterListener(myListener);
    }

    public ArrayList getListeners() {
        return listeners;
    }
    
    private void fireTableChangeListeners(){
        for(int i=0;i<listeners.size();i++){
            ((TableModelListener) listeners.get(i)).tableChanged(new TableModelEvent(this));
        }
    }
}
