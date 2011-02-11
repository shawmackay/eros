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
 * DefaultErrorModel.java
 *
 * Created on 13 February 2006, 15:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.blarty.eros.ui.erroranalyser.model;

import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.blarty.eros.InstanceDetail;
import org.blarty.eros.ui.FilterListener;

/**
 * 
 * @author calum.mackay
 */
public class DefaultErrorModel implements ErrorModel {

    private ArrayList store;

    private Map filters;

    private ArrayList filteredStore;

    private String[] filterNames = new String[] { APPLICATION_FILTER, CLASS_FILTER, EXCEPTION_FILTER, GROUP_FILTER, IPADDRESS_FILTER, MESSAGE_FILTER, METHOD_FILTER };

    private ArrayList listeners;

    private Map instanceDetails = new HashMap();

    /** Creates a new instance of DefaultErrorModel */
    public DefaultErrorModel() {
        this.store = new ArrayList();
        this.listeners = new ArrayList();
        this.filters = new TreeMap();
        this.filteredStore = this.store;
    }

    public void addInstanceRecord(InstanceDetail instanceDetail) {

        instanceDetails.put(new Long(instanceDetail.getInstanceIdentifier()), instanceDetail);
    }

    public void addError(long instancekey, String message, Date time, String level, String stacktrace, String arguments, String methodName, String className, String exceptionName, int linenum) {
        store.add(new DefaultErrorRecord(instancekey, message, time, level, stacktrace, arguments, methodName, className, exceptionName, linenum));
    }

    public void addError(ErrorRecord record) {
        store.add(record);
    }

    public long getNumRecords() {

        return filteredStore.size();
    }

    public ErrorRecord getRecord(long index) {

        if (index > -1) {
            DefaultErrorRecord rec = (DefaultErrorRecord) filteredStore.get((int) index);
            if (rec.getInstanceDetail() == null) {

                rec.setInstanceDetail((InstanceDetail) instanceDetails.get(new Long(rec.getInstanceKey())));
            }
            return rec;
        } else
            return null;
    }

    public List getAllRecords() {
        for (Iterator iter = filteredStore.iterator(); iter.hasNext();) {

            DefaultErrorRecord rec = (DefaultErrorRecord) iter.next();
            if (rec.getInstanceDetail() == null) {

                rec.setInstanceDetail((InstanceDetail) instanceDetails.get(new Long(rec.getInstanceKey())));
            }
        }
        return filteredStore;
    }

    public void addFilter(String column, String value) {
        boolean isAdded = true;
        if (filters.containsKey(column.toLowerCase()))
            isAdded = false;
        filters.put(column.toLowerCase(), value);
        applyFilters();
        if (isAdded) {
            fireFilterListeners(column.toLowerCase(), value, FILTER_ADDED);
        } else {
            fireFilterListeners(column.toLowerCase(), value, FILTER_CHANGED);
        }

    }

    public void removeFilter(String column) {
        filters.remove(column);
        applyFilters();
        fireFilterListeners(column, null, FILTER_REMOVED);

    }

    public String getFilterFor(String column) {
        if (filters.containsKey(column))
            return ((String) filters.get(column)).toLowerCase();
        return null;
    }

    private void applyFilters() {
        if (filters.size() == 0) {
            filteredStore = store;
        } else {
            filteredStore = new ArrayList();

            for (int i = 0; i < store.size(); i++) {
                DefaultErrorRecord record = (DefaultErrorRecord) store.get(i);
                if (record.getInstanceDetail() == null) {
                    record.setInstanceDetail((InstanceDetail) instanceDetails.get(new Long(record.getInstanceKey())));
                }
                boolean useRecord = true;
                for (int j = 0; j < filterNames.length; j++) {
                    boolean filterAccepted = true;
                    String filterValue = getFilterFor(filterNames[j]);
                    if (filterValue != null) {
                        if (filterNames[j].equalsIgnoreCase(APPLICATION_FILTER))
                            if (!record.getApplicationName().equalsIgnoreCase(filterValue))
                                filterAccepted = false;
                        if (filterNames[j].equalsIgnoreCase(CLASS_FILTER))
                            if (record.getClassName() == null || !record.getClassName().equalsIgnoreCase(filterValue))
                                filterAccepted = false;
                        if (filterNames[j].equalsIgnoreCase(EXCEPTION_FILTER))
                            if (!record.getExceptionName().equalsIgnoreCase(filterValue))
                                filterAccepted = false;
                        if (filterNames[j].equalsIgnoreCase(GROUP_FILTER))
                            if (!record.getGroup().equalsIgnoreCase(filterValue))
                                filterAccepted = false;
                        if (filterNames[j].equalsIgnoreCase(IPADDRESS_FILTER))
                            if (!record.getIPAddress().equalsIgnoreCase(filterValue))
                                filterAccepted = false;
                        if (filterNames[j].equalsIgnoreCase(MESSAGE_FILTER)) {
                            if (filterValue.endsWith("...")) {
                                System.out.println("Checking trimmed messages: " + filterValue + "=> " + record.getErrorMessage());
                                
                                if (!record.getErrorMessage().toLowerCase().startsWith(filterValue.toLowerCase().substring(0, filterValue.length() - 3)))
                                    filterAccepted = false;
                            } else if (!record.getErrorMessage().equalsIgnoreCase(filterValue))
                                filterAccepted = false;
                        }
                        if (filterNames[j].equalsIgnoreCase(METHOD_FILTER))
                            if (!record.getMethodName().equalsIgnoreCase(filterValue))
                                filterAccepted = false;

                    }
                    if (!filterAccepted) {
                        useRecord = false;
                        break;
                    }
                }
                if (useRecord) {

                    filteredStore.add(record);
                }
            }
        }
    }

    public void addFilterListener(FilterListener listener) {
        listeners.add(listener);
    }

    public void removeFilterListener(FilterListener listener) {
        listeners.remove(listener);
    }

    private void fireFilterListeners(String filter, String value, int type) {
        for (int i = 0; i < listeners.size(); i++) {
            FilterListener l = (FilterListener) listeners.get(i);
            switch (type) {
            case FILTER_ADDED:
                l.filterAdded(filter, value);
                break;
            case FILTER_CHANGED:
                l.filterChanged(filter, value);
                break;
            case FILTER_REMOVED:
                l.filterRemoved(filter);
                break;
            }
        }
    }

    public boolean isFilterSet(String column) {
        return filters.containsKey(column);
    }
}
