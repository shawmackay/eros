/*
   Copyright 2006 thor.jini.org Project

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
 * thor.jini.org : org.blarty.thor.service.ui
 * 
 * 
 * GradientLabel.java
 * Created on 08-Apr-2004
 * 
 * GradientLabel
 *
 */
package org.blarty.eros.ui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.Icon;
import javax.swing.JLabel;

/**
 * @author calum
 */
public class GradientLabel extends JLabel {
	/**
	 * 
	 */
	public GradientLabel() {
		super();
		// URGENT Complete constructor stub for GradientLabel
	}
	/**
	 * @param text
	 */
	public GradientLabel(String text) {
		super(text);
		// URGENT Complete constructor stub for GradientLabel
	}
	/**
	 * @param text
	 * @param horizontalAlignment
	 */
	public GradientLabel(String text, int horizontalAlignment) {
		super(text, horizontalAlignment);
		// URGENT Complete constructor stub for GradientLabel
	}
	/**
	 * @param text
	 * @param icon
	 * @param horizontalAlignment
	 */
	public GradientLabel(String text, Icon icon, int horizontalAlignment) {
		super(text, icon, horizontalAlignment);
		// URGENT Complete constructor stub for GradientLabel
	}
	/**
	 * @param image
	 */
	public GradientLabel(Icon image) {
		super(image);
		// URGENT Complete constructor stub for GradientLabel
	}
	/**
	 * @param image
	 * @param horizontalAlignment
	 */
	public GradientLabel(Icon image, int horizontalAlignment) {
		super(image, horizontalAlignment);
		// URGENT Complete constructor stub for GradientLabel	
    }
    
    private Color backgroundTo = Color.WHITE;
    
    public void setBackgroundTo(Color color){
        backgroundTo= color;
    }
    
    public Color getBackgroundTo(){
        setOpaque(false);
        return backgroundTo;
    }
	/* @see java.awt.Component#paint(java.awt.Graphics)
	 */
    
	public void paint(Graphics g) {
		// TODO Complete method stub for paint
		Graphics2D g2 = (Graphics2D) g;
        GradientPaint paint = new GradientPaint(0,0,this.getBackground(), getWidth(),0,backgroundTo);
        g2.setPaint(paint);
        g2.fillRect(0, 0, getWidth(), getHeight());
        super.paint(g2);
	}
}
