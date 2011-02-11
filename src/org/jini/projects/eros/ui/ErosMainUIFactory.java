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

import net.jini.core.lookup.ServiceItem;
import net.jini.lookup.ui.factory.JComponentFactory;
import net.jini.lookup.ui.factory.JFrameFactory;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;

import java.awt.BorderLayout;
import java.io.Serializable;

/**
 * The factory class for the MainUI for the Eros service as defined in the 
 * ServiceUI specification.
 */
public class ErosMainUIFactory implements JFrameFactory, Serializable {

    public ErosMainUIFactory() {
    }
    

    public JFrame getJFrame(Object roleObj) {
        ServiceItem si = (ServiceItem) roleObj;
        JFrame jf = new JFrame("Eros Service UI");
        jf.getContentPane().setLayout(new BorderLayout());
        jf.setIconImage(new ImageIcon(getClass().getResource("/org/jini/projects/eros/eros_icon.png")).getImage());
        ErosMainUI myUI = new ErosMainUI((ErosServiceUI) si.service);
        jf.getContentPane().add(myUI, BorderLayout.CENTER);
        jf.setSize(1000,800);
        return jf;
    }
}
