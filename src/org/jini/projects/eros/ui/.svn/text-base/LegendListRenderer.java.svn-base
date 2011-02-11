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
 * LegendListRenderer.java
 *
 * Created on 17 March 2006, 23:27
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jini.projects.eros.ui;

import java.awt.Component;
import java.awt.Graphics;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 *
 * @author calum.mackay
 */
public class LegendListRenderer extends DefaultListCellRenderer{
    
    private LegendItem item;
    
    /** Creates a new instance of LegendListRenderer */
    public LegendListRenderer() {
    }
    
   public Component getListCellRendererComponent(
        JList list,
	Object value,
        int index,
        boolean isSelected,
        boolean cellHasFocus)
    {
     if(value instanceof LegendItem){
         item = (LegendItem) value;
         setFont(list.getFont());
         setIcon(new ColorBlockIcon(item.getColor()));
         setBackground(list.getBackground());
         setText(item.getLabel());
        
         return this;
     }
     else
         return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
   }
   
   
}
