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
 * ErrorModel.java
 *
 * Created on 13 February 2006, 15:29
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.blarty.eros.ui.erroranalyser.model;

import java.io.Serializable;
import java.util.List;

import org.blarty.eros.ui.FilterListener;

/**
 *
 * @author calum.mackay
 */
public interface ErrorModel extends  Serializable {
    public static final String APPLICATION_FILTER="application";
    public static final String MESSAGE_FILTER="message";
    public static final String IPADDRESS_FILTER="ipaddress";
    public static final String METHOD_FILTER="method";
    public static final String CLASS_FILTER="class";
    public static final String EXCEPTION_FILTER="exception";
    public static final String GROUP_FILTER="group";
    
    public static final int FILTER_ADDED = 0;
    public static final int FILTER_CHANGED = 1;
    public static final int FILTER_REMOVED = 2;
    
    public long getNumRecords();
    public ErrorRecord getRecord(long index);
    public List getAllRecords();
    public void addFilterListener(FilterListener listener);
    public void removeFilterListener(FilterListener listener);
    public void addFilter(String column, String value);
    public void removeFilter(String column);
    public boolean isFilterSet(String column);
    public String getFilterFor(String column);
}
