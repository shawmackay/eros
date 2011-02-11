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
 * LegednItem.java
 *
 * Created on 17 March 2006, 23:24
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jini.projects.eros.ui;

import java.awt.Color;

/**
 *
 * @author calum.mackay
 */
public class LegendItem {
    
    private Color color;
    private String label;
    
    /** Creates a new instance of LegednItem */
    public LegendItem(Color color, String label) {
        this.color = color;
        this.label = label;
    }
    
    
    public Color getColor(){
        return this.color;
    }
    
    public String getLabel(){
        return this.label;
    }
}
