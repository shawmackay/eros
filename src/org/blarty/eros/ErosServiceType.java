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
package org.blarty.eros;

import java.awt.Image;
import javax.swing.ImageIcon;
import java.beans.BeanInfo;

import net.jini.lookup.entry.ServiceType;

/**
 * Provides additional information regarding the service.
 * Used by service browsers to provide more meaningful information about a 
 * service rather than jsut the interfaces available.
 */
public class ErosServiceType extends ServiceType {

    private Image image = null;
    private String imageFile = "eros_icon.png";

    public ErosServiceType() {
    }

    public String getDisplayName() {
        return "Eros Service";
    }

    public String getShortDescription() {
        return "Jini service that provides distributed error logging.";
    }

    public Image getIcon(int iconType) {
        try {
            ImageIcon imageIcon = new ImageIcon(getClass().getResource(imageFile));
            image = imageIcon.getImage();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        if (iconType == BeanInfo.ICON_COLOR_16x16) {
            return image.getScaledInstance(16, 16, Image.SCALE_DEFAULT);
        } else if (iconType == BeanInfo.ICON_COLOR_32x32) {
            return image.getScaledInstance(32, 32, Image.SCALE_DEFAULT);
        } else if (iconType == BeanInfo.ICON_MONO_16x16) {
            return image.getScaledInstance(16, 16, Image.SCALE_DEFAULT);
        } else if (iconType == BeanInfo.ICON_COLOR_32x32) {
            return image.getScaledInstance(32, 32, Image.SCALE_DEFAULT);
        } else {
            return null;
        }
    }

}
