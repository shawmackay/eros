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
 * ColorBlockIcon.java
 *
 * Created on 18 March 2006, 00:31
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jini.projects.eros.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;

/**
 *
 * @author calum.mackay
 */
public class ColorBlockIcon implements Icon{
    
    private Color col;
    
    /** Creates a new instance of ColorBlockIcon */
    public ColorBlockIcon(Color col) {
        this.col = col;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.setColor(col);
        g.fillRect(4,4,12,12);
        g.setColor(Color.BLACK);
        g.drawRect(4,4,12,12);
    }

    public int getIconWidth() {
        return 18;
    }

    public int getIconHeight() {
        return 18;
    }
    
}
