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
 * ErrorView.java
 *
 * Created on 13 February 2006, 15:35
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jini.projects.eros.ui;

import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Rectangle2D;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.logging.Level;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jini.projects.eros.ui.erroranalyser.model.ErrorModel;
import org.jini.projects.eros.ui.erroranalyser.model.ErrorRecord;

/**
 * 
 * @author calum.mackay
 */
public class ErrorView
                extends
                JComponent {
        public static final int APPLICATION_VIEW_INDICATOR = 1;

        public static final int MESSAGE_VIEW_INDICATOR = 2;

        public static final int ADDRESS_VIEW_INDICATOR = 3;

        public static final int TIME_VIEW_INDICATOR = 4;

        public static final int LEVEL_VIEW_INDICATOR = 5;

        public static final int STACKTRACE_VIEW_INDICATOR = 6;

        public static final int ARGUMENTS_VIEW_INDICATOR = 7;

        public static final int GROUP_VIEW_INDICATOR = 8;

        public static final int METHODNAME_VIEW_INDICATOR = 9;

        public static final int CLASSNAME_VIEW_INDICATOR = 10;

        public static final int EXCEPTIONNAME_VIEW_INDICATOR = 11;

        public static final String[] VIEW_NAMES = new String[] { "Application", "Error Message", "IP Address", "Time Logged", "Log Level", "Stack Trace", "Arguments", "Jini Group", "Method Name", "Class Name", "Exception Name" };

        public static final String APPLICATION_FILTER = "application";

        public static final String MESSAGE_FILTER = "message";

        public static final String IPADDRESS_FILTER = "ipaddress";

        public static final String METHOD_FILTER = "method";

        public static final String CLASS_FILTER = "class";

        public static final String EXCEPTION_FILTER = "exception";

        public static final String GROUP_FILTER = "group";

        public static final String NO_FILTER = "none";

        private boolean isFiltered = false;

        public static final int PIE_CHART = 11;

        public static final int BAR_CHART = 12;

        public static final int CUMULATIVE_BAR = 13;

        private int graphType = PIE_CHART;

        private Rectangle cumalativeBarRect = null;

        Robot robot = null;

        private Map colorStateMap;

        private int totalNumRecords = 0;

        private int viewIndicator = APPLICATION_VIEW_INDICATOR;

        private String viewFilterName = APPLICATION_FILTER;

        private ErrorModel model;

        private TreeMap statmap = new TreeMap();

        private Font font = new Font("Dialog", Font.BOLD, 12);

        private Color[] cols = new Color[] { Color.GREEN, Color.BLUE, Color.RED, new Color(180, 5, 235), Color.YELLOW, Color.CYAN, Color.MAGENTA, Color.ORANGE, Color.GRAY, Color.PINK, new Color(200, 200, 255), new Color(200, 255, 200), new Color(255, 200, 200), new Color(40, 40, 100), new Color(40, 100, 40), new Color(100, 40, 40) };

        private String valueSelected;

        private Map angleTable;

        private Point toolTipLocation;

        private String nameTooltip;

        private ArrayList cumulativeRects = new ArrayList();

        private ArrayList selectionListeners;

        private FilterListener internalListener;

        private int displayLegendAt = 72;

        private DefaultListModel legend;

        private int barheight;

        /** Creates a new instance of ErrorView */
        public ErrorView() {
                try {
                        robot = new Robot();
                        angleTable = new TreeMap();
                        colorStateMap = new TreeMap();
                        selectionListeners = new ArrayList();
                        selectionListeners.add(new ChartSelectionListener() {
                                public void selectionChanged(Object source, Object category, Object value) {
                                        if (getModel().isFilterSet((String) category)) {
                                                getModel().removeFilter((String) category);
                                                isFiltered = false;
                                        } else {
                                                getModel().addFilter((String) category, (String) value);
                                                isFiltered = true;
                                        }

                                }
                        });
                        this.addComponentListener(new ComponentAdapter() {
                                public void componentResized(ComponentEvent e) {

                                        final int i = statmap.size();

                                }
                        });
                        internalListener = new FilterListener() {
                                public void filterAdded(String filter, String value) {

                                        collateStats();
                                        repaint();
                                }

                                public void filterChanged(String filter, String value) {
                                        collateStats();
                                        repaint();
                                }

                                public void filterRemoved(String filter) {

                                        collateStats();
                                        repaint();
                                }
                        };
                } catch (AWTException ex) {
                        ex.printStackTrace();
                }
        }

        public void setLegendModel(DefaultListModel legendModel) {
                this.legend = legendModel;
        }

        public void setViewIndicator(int viewIndicator) {
                this.viewIndicator = viewIndicator;
                collateStats();
                initialiseColorState();
                invalidate();

        }

        private void initialiseColorState() {
                int loop = 0;

                Random col_Random = new Random();
                for (Iterator iter = statmap.entrySet().iterator(); iter.hasNext();) {
                        Map.Entry entr = (Map.Entry) iter.next();
                        String entity = (String) entr.getKey();
                        Color col = new Color(64 + col_Random.nextInt(192), 64 + col_Random.nextInt(192), 64 + col_Random.nextInt(192));

                        colorStateMap.put(entity, col);
                }
        }

        public void setGraphType(int graphType) {
                this.graphType = graphType;
                if (this.graphType < 10)
                        this.graphType += 10;
                invalidate();
        }

        public void setErrorModel(ErrorModel model) {
                if (this.model != null) {
                        model.removeFilterListener(internalListener);
                }
                this.model = model;
                model.addFilterListener(internalListener);
                collateStats();
                initialiseColorState();
                addMouseListener(new MouseAdapter() {
                        public void mousePressed(MouseEvent evt) {
                                doMouseEvents(evt);
                        }
                });
                addMouseMotionListener(new MouseMotionAdapter() {
                        public void mouseMoved(MouseEvent evt) {
                                doMouseEvents(evt);
                        }

                });
        }

        private void setToolTipLocation(Point p) {
                if (p != null) {
                        Point offset = new Point((int) p.getX() + 8, (int) p.getY() + 8);
                        this.toolTipLocation = offset;
                } else
                        toolTipLocation = null;
        }

        private void setNameTooltip(String name) {
                this.nameTooltip = name;
        }

        private void doMouseEvents(MouseEvent evt) {
                if (this.graphType == PIE_CHART)
                        doPieMouseEvents(evt);
                if (this.graphType == CUMULATIVE_BAR) {
                        doCumulativeMouseEvents(evt);

                }
                repaint();

        }

        private void doCumulativeMouseEvents(final MouseEvent evt) {
                int x = evt.getX();
                int y = evt.getY();
                boolean foundcontainer = false;
                for (int i = 0; i < cumulativeRects.size(); i++) {
                        TaggedRectangle r = (TaggedRectangle) cumulativeRects.get(i);
                        setToolTipLocation(evt.getPoint());
                        nameTooltip = r.value;
                        if (r.bounds.contains(evt.getPoint())) {
                                if (valueSelected == null || !valueSelected.equals(r.value)) {
                                        nameTooltip = r.value;
                                        valueSelected = r.value;
                                }
                                foundcontainer = true;

                                break;
                        }

                }
                if (evt.getPoint() != null && cumulativeRects != null) {
                        if (cumalativeBarRect != null)
                                if (cumalativeBarRect.contains(evt.getPoint()) && evt.getClickCount() == 1) {
                                        fireSelectionListeners();
                                }
                }
                if (!foundcontainer) {
                        setToolTipLocation(null);
                        setNameTooltip(null);
                }

        }

        private class TaggedRectangle {
                String value;

                Rectangle bounds;

                public TaggedRectangle(String value, Rectangle bounds) {
                        this.value = value;
                        this.bounds = bounds;

                }
        }

        private void doPieMouseEvents(final MouseEvent evt) {
                int width = getWidth();
                int height = getHeight();
                if (width > height) {
                        width = height;
                } else {
                        height = width;
                }
                int diameter = (width - 20);
                int centerpoint = diameter / 2 + 10;
                int diffx = centerpoint - evt.getX();
                // Check that the line is within the diameter from the
                // centerpoint;
                double lengthAngledLine = Math.sqrt((Math.abs(centerpoint - evt.getX()) * Math.abs(centerpoint - evt.getX())) + (Math.abs(centerpoint - evt.getY()) * Math.abs(centerpoint - evt.getY())));
                if (lengthAngledLine <= diameter / 2) {
                        setToolTipLocation(evt.getPoint());
                        double angle = calcAngle(centerpoint + diffx, evt.getY(), diameter / 2 + 10, diameter / 2 + 10);
                        for (Iterator iter = angleTable.entrySet().iterator(); iter.hasNext();) {
                                Map.Entry entr = (Map.Entry) iter.next();
                                if (((Integer) entr.getKey()).intValue() > angle) {
                                        String value = (String) entr.getValue();
                                        if (!value.equals(valueSelected)) {
                                                valueSelected = value;

                                        }

                                        break;
                                }
                        }
                        if (evt.getClickCount() > 0) {

                                fireSelectionListeners();
                        }
                } else
                        setToolTipLocation(null);
        }

        private static double calcAngle(float x1, float y1, float x2, float y2) {
                float dx = x2 - x1;
                float dy = y2 - y1;
                double angle = 0.0d;

                // Calculate angle
                if (dx == 0.0) {
                        if (dy == 0.0)
                                angle = 0.0;
                        else if (dy > 0.0)
                                angle = Math.PI / 2.0;
                        else
                                angle = Math.PI * 3.0 / 2.0;
                } else if (dy == 0.0) {
                        if (dx > 0.0)
                                angle = 0.0;
                        else
                                angle = Math.PI;
                } else {
                        if (dx < 0.0)
                                angle = Math.atan(dy / dx) + Math.PI;
                        else if (dy < 0.0)
                                angle = Math.atan(dy / dx) + (2 * Math.PI);
                        else
                                angle = Math.atan(dy / dx);
                }

                // Convert to degrees
                angle = angle * 180 / Math.PI;

                // Return
                return (angle);
        }

        public ErrorModel getModel() {
                return model;
        }

        private void getColorAt(MouseEvent evt) {

                System.out.println("MouseDown:");
                Point p = evt.getPoint();
                Graphics2D g2 = (Graphics2D) getGraphics();
                Dimension screen_size = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
                Rectangle rect = new Rectangle((int) getLocationOnScreen().getX(), (int) getLocationOnScreen().getY(), (int) getLocationOnScreen().getX() + getWidth(), (int) getLocationOnScreen().getY() + getHeight());
                robot.createScreenCapture(rect);
                System.out.println("Color: " + robot.getPixelColor(evt.getX(), evt.getY()));
        }

        public void collateStats() {
                List l = model.getAllRecords();

                angleTable.clear();
                statmap.clear();
                // valueSelected = null;

                for (Iterator iter = l.iterator(); iter.hasNext();) {
                        ErrorRecord record = (ErrorRecord) iter.next();
                        String viewItem = null;
                        switch (viewIndicator) {
                        case MESSAGE_VIEW_INDICATOR:
                                viewItem = record.getErrorMessage();
                                viewFilterName = MESSAGE_FILTER;
                                break;
                        case ADDRESS_VIEW_INDICATOR:
                                viewItem = record.getIPAddress();
                                viewFilterName = IPADDRESS_FILTER;
                                break;
                        case TIME_VIEW_INDICATOR:
                                Date d = record.getTime();
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                                viewItem = sdf.format(d);
                                viewFilterName = NO_FILTER;
                                break;
                        case ARGUMENTS_VIEW_INDICATOR:
                                viewItem = record.getArguments();
                                viewFilterName = NO_FILTER;
                                break;
                        case LEVEL_VIEW_INDICATOR:
                                viewItem = record.getLevel();
                                viewFilterName = NO_FILTER;
                                break;
                        case CLASSNAME_VIEW_INDICATOR:
                                viewItem = record.getClassName();
                                viewFilterName = CLASS_FILTER;
                                break;
                        case METHODNAME_VIEW_INDICATOR:
                                viewItem = record.getMethodName();
                                viewFilterName = METHOD_FILTER;
                                break;
                        case STACKTRACE_VIEW_INDICATOR:
                                viewItem = record.getStackTrace();
                                viewFilterName = NO_FILTER;
                                break;
                        case EXCEPTIONNAME_VIEW_INDICATOR:
                                viewItem = record.getExceptionName();
                                viewFilterName = EXCEPTION_FILTER;
                                break;
                        case GROUP_VIEW_INDICATOR:
                                viewItem = record.getGroup();
                                viewFilterName = GROUP_FILTER;
                                break;
                        default:
                                viewItem = record.getApplicationName();
                                viewFilterName = APPLICATION_FILTER;
                        }
                        if (viewItem != null) {
                            if(viewIndicator==MESSAGE_VIEW_INDICATOR){
                                System.out.println("Checking Level Indicator");
                                if (record.getLevel().toUpperCase().equals("INFO") || record.getLevel().toUpperCase().equals("DEBUG")){
                                    //Because of the high level of info and lower level messages, we need to group them as their messages may be different but using different parameters
                                    System.out.println("Should trim data");  
                                    if(viewItem.length()>10){
                                        viewItem = viewItem.substring(0,10) + "...";
                                    }
                                }
                            }
                                if (statmap.containsKey(viewItem)) {
                                        int value = ((Integer) statmap.get(viewItem)).intValue();
                                        statmap.put(viewItem, new Integer(value + 1));
                                        totalNumRecords++;
                                } else {
                                        statmap.put(viewItem, new Integer(1));
                                        totalNumRecords++;
                                }
                        } else
                                System.out.println("View Item is null");
                }
                int i = statmap.size();

                int y = 102;

                intiialiseLegend();
        }

        public void intiialiseLegend() {

                this.legend.clear();

                for (Iterator iter = statmap.entrySet().iterator(); iter.hasNext();) {
                        Map.Entry entr = (Map.Entry) iter.next();
                        String entity = (String) entr.getKey();
                        LegendItem item = new LegendItem((Color) colorStateMap.get(entity), entity);
                        legend.addElement(item);

                }
        }

        protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                switch (graphType) {
                case PIE_CHART:
                        displayPieChart(g2);
                        break;
                case CUMULATIVE_BAR:
                        displayCumulativeBarChart(g2);
                        break;
                case BAR_CHART:
                        displayBarGraph(g2);
                }

        }

        // private void displayLegend(final Graphics2D g2, final int y){
        // g2.setColor(Color.WHITE);
        // final int i= statmap.size();
        //
        //
        // if(i%3!=0){
        // g2.fillRoundRect(24, y, getWidth()-48, (((i/3)+1) * 14)+6,6,6);
        //
        // }else{
        // g2.fillRoundRect(24, y, getWidth()-48, (((i/3)) * 14)+6,6,6);
        //
        //
        // }
        // g2.setColor(Color.BLACK);
        // if(i%3!=0){
        // g2.drawRoundRect(24, y, getWidth()-48, (((i/3)+1) * 14)+6,6,6);
        // g2.setClip(24, y, getWidth()-48, (((i/3)+1) * 14)+6);
        // } else{
        // g2.drawRoundRect(24, y, getWidth()-48, (((i/3)) * 14)+6,6,6);
        // g2.setClip(24, y, getWidth()-48, (((i/3)) * 14)+6);
        // }
        // int legendLoop = 0;
        // int linefeedLoop = 0;
        // int increment_x = (getWidth()-48)/3;
        //
        // int increment_y = 14;
        // int current_y =4;
        // int current_x=12;
        // g2.translate(24,y);
        //
        // for(Iterator iter = statmap.entrySet().iterator();iter.hasNext();){
        //
        // Map.Entry entr = (Map.Entry) iter.next();
        // String entity = (String)entr.getKey();
        // g2.setColor((Color) colorStateMap.get(entity));
        // g2.fillRect(current_x+3,current_y+3,8,8);
        // g2.setColor(Color.BLACK);
        // g2.drawString(entity,current_x + 14, current_y + 12);
        // linefeedLoop++;
        // if(linefeedLoop==3){
        // current_y+=increment_y;
        // current_x=12;
        // linefeedLoop = 0;
        // } else{
        // current_x+= increment_x;
        // }
        //
        // }
        // g2.translate(-24,-y);
        // g2.setClip(0,0,getWidth(), getHeight());
        // }

        private void displayCumulativeBarChart(final Graphics2D g2) {
                g2.setColor(Color.BLACK);
                int headeroriginx = 12;
                int headeroriginy = 18;

                g2.drawString(VIEW_NAMES[this.viewIndicator - 1], headeroriginx, headeroriginy);
                Rectangle2D font_rect = null;
                if (isFiltered) {
                        font_rect = font.getStringBounds("Filtered", g2.getFontRenderContext());
                        g2.setColor(Color.RED);
                        g2.drawString("Filtered", getWidth() - (int) font_rect.getWidth() - 12, headeroriginy);
                }

                int originx = 30;
                int originy = 24;
                int height = this.getHeight();
                int width = this.getWidth();
                Color grad1Color = new Color(100, 100, 255);
                Color solidColor = new Color(200, 200, 255);
                Color grad2Color = new Color(50, 50, 200);
                Color green1Color = new Color(100, 255, 100);
                Color greenColor = new Color(200, 255, 200);
                Color green2Color = new Color(50, 200, 50);
                barheight = 36;
                int barlength = width - (originx * 2);

                cumalativeBarRect = new Rectangle(originx, originy, barlength, barheight);
                double cumulative_x = originx;
                int currentcolIndex = 0;
                cumulativeRects.clear();
                int iteration = 0;

                for (Iterator iter = statmap.entrySet().iterator(); iter.hasNext();) {

                        Map.Entry entr = (Map.Entry) iter.next();
                        Color baseColor = (Color) colorStateMap.get(entr.getKey());
                        if (baseColor != null) {
                                int numberForApplication = ((Integer) entr.getValue()).intValue();

                                double value = (double) numberForApplication / (double) model.getNumRecords();
                                double itemLength = barlength * value;

                                cumulativeRects.add(new TaggedRectangle((String) entr.getKey(), new Rectangle((int) cumulative_x, originy, (int) itemLength, barheight)));
                                drawGradientBarAt(g2, (int) cumulative_x, originy, (int) itemLength, barheight, baseColor.darker().darker(), baseColor, baseColor.darker());
                                cumulative_x += itemLength;
                        } else
                                System.out.println(colorStateMap);
                }
                // displayLegend(g2, originy + barheight + 6);
                if (this.toolTipLocation != null) {
                        drawToolTip(g2);
                }

                // drawGradientBarAt(g2,originx+barlength, originy, barlength/2,
                // barheight,green2Color, greenColor, green1Color);
                // g2.drawRect(0,bottombar_start,barlength,bottombar_end-bottombar_start);
                // System.out.println("Bottombar Start:End " + bottombar_start +
                // ":" + bottombar_end);
        }

        private void displayBarGraph(Graphics2D g2) {
                int highestNumber = 0;
                int number_of_divisions = statmap.size();
                int graphwidth = getWidth() - 60;
                int graphheight = getHeight() - 60;
                int originx = 30;
                int originy = 30;
                double split = graphwidth / number_of_divisions;
                graphwidth = (int) split * number_of_divisions;

                int currentcolIndex = 0;
                int currentItemIndex = 0;
                for (Iterator iter = statmap.entrySet().iterator(); iter.hasNext();) {
                        Map.Entry entr = (Map.Entry) iter.next();
                        int numberForApplication = ((Integer) entr.getValue()).intValue();
                        if (numberForApplication > highestNumber)
                                highestNumber = numberForApplication;

                }
                double unitheight = graphheight / highestNumber;
                // System.out.println("UnitHeight /split is:" + unitheight + "/"
                // + split);
                for (Iterator iter = statmap.entrySet().iterator(); iter.hasNext();) {
                        Color baseColor = cols[currentcolIndex++];
                        Map.Entry entr = (Map.Entry) iter.next();
                        int numberForApplication = ((Integer) entr.getValue()).intValue();
                        // Systemd.out.println("Application " + entr.getKey()+ "
                        // is " + numberForApplication );

                        g2.setColor(baseColor);
                        int pointx = originx + (int) (currentItemIndex * split);
                        int pointy = originy + graphheight - ((int) (numberForApplication * unitheight));
                        int pointx1 = (int) split;
                        int pointy1 = (int) (numberForApplication * unitheight);
                        drawVerticalGradientBarAt(g2, (int) pointx, pointy, (int) pointx1, pointy1, baseColor.darker().darker(), baseColor, baseColor.darker());
                        currentItemIndex++;
                        // g2.fillRect(pointx,
                        // pointy,
                        // pointx1,
                        // pointy1);
                        //
                        // g2.setColor(Color.BLACK);
                        // g2.drawRect(pointx,
                        // pointy,
                        // pointx1,
                        // pointy1);
                }

        }

        private void drawGradientBarAt(final Graphics2D g2, final int x, final int y, final int width, final int height, final Color grad2Color, final Color solidColor, final Color grad1Color) {
                g2.translate(x, y);
                float bar_split = (float) (height * .25);
                float solidbar_height = (float) (bar_split / 2);
                int topbar_start = 0;
                int topbar_end = (int) (bar_split);
                int middlebar_start = (int) (topbar_end + solidbar_height);
                int middlebar_end = (int) bar_split;
                int bottombar_start = middlebar_start + middlebar_end;
                int bottombar_end = height;
                GradientPaint bar_back_top = null;
                GradientPaint bar_back_middle = null;
                GradientPaint bar_back_bottom = null;

                bar_back_top = new GradientPaint(0, topbar_start, grad1Color, 0, topbar_end, solidColor, false);

                bar_back_middle = new GradientPaint(0f, middlebar_start, solidColor, 0f, middlebar_start + middlebar_end, grad1Color, false);
                bar_back_bottom = new GradientPaint(0, bottombar_start, grad1Color, 0f, bottombar_end, grad2Color, false);
                g2.setColor(solidColor);
                g2.fillRect(0, 0, width, height);

                g2.setPaint(bar_back_top);
                g2.fillRect(0, 0, (int) width, topbar_end);
                g2.setColor(Color.BLACK);
                // g2.drawRect(0,0,(int)barlength,topbar_end);

                g2.setPaint(bar_back_middle);
                g2.fillRect(0, middlebar_start, width, middlebar_end);
                g2.setColor(Color.BLACK);
                // g2.drawRect(0,middlebar_start,barlength,middlebar_end);
                g2.setPaint(bar_back_bottom);
                g2.fillRect(0, bottombar_start, width, bottombar_end - bottombar_start);
                g2.setColor(Color.BLACK);
                g2.drawRect(0, 0, width, height);
                g2.translate(-x, -y);
        }

        private void drawVerticalGradientBarAt(final Graphics2D g2, final int x, final int y, final int width, final int height, final Color grad2Color, final Color solidColor, final Color grad1Color) {
                g2.translate(x, y);
                float bar_split = (float) (width * .25);
                float solidbar_width = (float) (bar_split / 2);
                int topbar_start = 0;
                int topbar_end = (int) (bar_split);
                int middlebar_start = (int) (topbar_end + solidbar_width);
                int middlebar_end = (int) bar_split;
                int bottombar_start = middlebar_start + middlebar_end;
                int bottombar_end = width;
                GradientPaint bar_back_top = null;
                GradientPaint bar_back_middle = null;
                GradientPaint bar_back_bottom = null;

                bar_back_top = new GradientPaint(topbar_start, 0, grad1Color, topbar_end, 0, solidColor, false);

                bar_back_middle = new GradientPaint(middlebar_start, 0, solidColor, middlebar_start + middlebar_end, 0, grad1Color, false);
                bar_back_bottom = new GradientPaint(bottombar_start, 0, grad1Color, bottombar_end, 0, grad2Color, false);
                g2.setColor(solidColor);
                g2.fillRect(0, 0, width, height);

                g2.setPaint(bar_back_top);
                g2.fillRect(0, 0, topbar_end, height);
                g2.setColor(Color.BLACK);
                // g2.drawRect(0,0,(int)barlength,topbar_end);

                g2.setPaint(bar_back_middle);
                g2.fillRect(middlebar_start, 0, middlebar_end, height);
                g2.setColor(Color.BLACK);
                // g2.drawRect(0,middlebar_start,barlength,middlebar_end);
                g2.setPaint(bar_back_bottom);
                g2.fillRect(bottombar_start, 0, bottombar_end - bottombar_start, height);
                g2.setColor(Color.BLACK);
                g2.drawRect(0, 0, width, height);
                g2.translate(-x, -y);
        }

        private void displayPieChart(final Graphics2D g2) {
                int currentArcStart = 0;
                int currentcolIndex = 0;

                int height = this.getHeight();
                int width = this.getWidth();

                if (width > height) {
                        width = height;
                } else {
                        height = width;
                }
                int diameter = (width - 20);
                float radius = diameter / 2;
                double a = 0.0;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setFont(font);
                int numdone = 0;
                for (Iterator iter = statmap.entrySet().iterator(); iter.hasNext();) {
                        Map.Entry entr = (Map.Entry) iter.next();
                        if (entr.getKey().equals(valueSelected)) {
                                g2.setColor(cols[currentcolIndex++]);
                        } else
                                g2.setColor(cols[currentcolIndex++].darker());

                        int numberForApplication = ((Integer) entr.getValue()).intValue();
                        // System.out.println("Application " + entr.getKey()+ "
                        // is " + numberForApplication );
                        // System.out.println("Num For APplication: " +
                        // numberForApplication + "; TotalNumRecords: " +
                        // model.getNumRecords());
                        double value = (double) numberForApplication / (double) model.getNumRecords();
                        long ratio = (long) (value * 360);
                        // System.out.println("Angle Size is " + ratio + ": " +
                        // model.getNumRecords());

                        numdone++;
                        if (numdone == statmap.entrySet().size())
                                ratio = 360 - currentArcStart;

                        if (entr.getKey().equals(valueSelected)) {
                                g2.fillArc(5, 5, (width - 10), (height - 10), currentArcStart, (int) ratio);
                        } else
                                g2.fillArc(10, 10, (width - 20), (height - 20), currentArcStart, (int) ratio);

                        g2.setColor(Color.BLACK);

                        g2.setStroke(new BasicStroke(2.0f));
                        if (entr.getKey().equals(valueSelected)) {
                                g2.drawArc(5, 5, (width - 10), (height - 10), currentArcStart, (int) ratio);
                        } else
                                g2.drawArc(10, 10, (width - 20), (height - 20), currentArcStart, (int) ratio);

                        currentArcStart += ratio;
                        this.angleTable.put(new Integer(currentArcStart), entr.getKey());
                }
                Rectangle2D gradRect = font.getStringBounds("A", g2.getFontRenderContext());
                int gradientHeight = (int) gradRect.getHeight() + 12;
                // System.out.println("GradientHeight: " + gradientHeight);
                currentArcStart = 0;
                numdone = 0;
                float titleoffset = 0.9f;
                float pcoffset = 0.4f;
                float title_ratio_offset = 0.55f;
                float pc_ratio_offset = 0.65f;
                if (this.toolTipLocation != null) {
                        drawToolTip(g2);
                }
                drawPieLabels(radius, currentArcStart, width, g2, numdone, titleoffset, pc_ratio_offset, pcoffset, title_ratio_offset, height);
        }

        private void drawToolTip(Graphics2D g2) {
                if (valueSelected != null && statmap.containsKey(valueSelected)) {
                        int numberForApplication = ((Integer) statmap.get(valueSelected)).intValue();
                        // System.out.println("Application " + entr.getKey()+ "
                        // is " + numberForApplication );
                        double value = (double) numberForApplication / (double) model.getNumRecords();
                        int pc = (int) (value * 1000);
                        Rectangle2D font_rect = null;
                        if (nameTooltip != null)
                                font_rect = font.getStringBounds(nameTooltip + ": " + Float.toString(pc / 10f) + "%(" + numberForApplication + ")", g2.getFontRenderContext());
                        else
                                font_rect = font.getStringBounds(Float.toString(pc / 10f) + "%(" + numberForApplication + ")", g2.getFontRenderContext());

                        g2.setColor(new Color(255, 255, 255, 152));
                        if((toolTipLocation.getX() +  (int) font_rect.getWidth() + 10) > this.getWidth())
                                toolTipLocation.setLocation(getWidth() - ((int) font_rect.getWidth() + 10), toolTipLocation.getY()); 
                        
                        if((toolTipLocation.getY() +  (int) font_rect.getHeight() + 10) > this.getHeight())
                                toolTipLocation.setLocation(toolTipLocation.getX(),getHeight() - ((int) font_rect.getHeight() + 10)); 
                        g2.fillRoundRect((int) toolTipLocation.getX(), (int) toolTipLocation.getY(), (int) font_rect.getWidth() + 8, (int) font_rect.getHeight() + 8, 5, 5);
                        g2.setColor(Color.BLACK);
                        g2.setColor(Color.BLACK);
                        if (nameTooltip != null)
                                g2.drawString(nameTooltip + ": " + Float.toString(pc / 10f) + "%(" + numberForApplication + ")", (int) toolTipLocation.getX() + 4, (int) toolTipLocation.getY() + (int) font_rect.getHeight());
                        else
                                g2.drawString(Float.toString(pc / 10f) + "%(" + numberForApplication + ")", (int) toolTipLocation.getX() + 4, (int) toolTipLocation.getY() + (int) font_rect.getHeight());
                }
        }

        private void drawPieLabels(final float radius, int currentArcStart, final int width, final Graphics2D g2, int numdone, final float titleoffset, final float pc_ratio_offset, final float pcoffset, final float title_ratio_offset, final int height) {
                for (Iterator iter = statmap.entrySet().iterator(); iter.hasNext();) {
                        Map.Entry entr = (Map.Entry) iter.next();
                        int numberForApplication = ((Integer) entr.getValue()).intValue();
                        // System.out.println("Application " + entr.getKey()+ "
                        // is " + numberForApplication );
                        double value = (double) numberForApplication / (double) model.getNumRecords();
                        int pc = (int) (value * 1000);
                        long ratio = (long) (value * 360);
                        numdone++;
                        if (numdone == statmap.entrySet().size())
                                ratio = 360 - currentArcStart;
                        currentArcStart += ratio;
                        // System.out.println("Angle Size is " + ratio + ": " +
                        // model.getNumRecords());
                        double angle = (double) currentArcStart * (double) 2 * Math.PI;

                        int x = (width / 2) + (int) (radius * Math.cos(Math.toRadians(-currentArcStart)));
                        int y = (height / 2) + (int) (radius * Math.sin(Math.toRadians(-currentArcStart)));
                        g2.setColor(Color.BLACK);
                        if (ratio != 360)
                                g2.drawLine(width / 2, height / 2, x, y);
                        // g2.drawLine(width/2, height/2,(int)((width/2) +
                        // xpoint), (int)((height/2) + ypoint));

                        float titlex = (width / 2) + (int) ((radius * titleoffset) * Math.cos(Math.toRadians(-(currentArcStart - (ratio * title_ratio_offset)))));
                        float titley = (height / 2) + (int) ((radius * titleoffset) * Math.sin(Math.toRadians(-(currentArcStart - (ratio * title_ratio_offset)))));
                        float pcx = (width / 2) + (int) ((radius * pcoffset) * Math.cos(Math.toRadians(-(currentArcStart - (ratio * pc_ratio_offset)))));
                        float pcy = (height / 2) + (int) ((radius * pcoffset) * Math.sin(Math.toRadians(-(currentArcStart - (ratio * pc_ratio_offset)))));
                        Rectangle2D font_rect = font.getStringBounds((String) entr.getKey(), g2.getFontRenderContext());

                        g2.setColor(new Color(255, 255, 255, 152));
                        g2.fillRoundRect((int) titlex - 4, (int) titley - (int) font_rect.getHeight(), (int) font_rect.getWidth() + 8, (int) font_rect.getHeight() + 8, 5, 5);
                        g2.setColor(Color.BLACK);

                        g2.drawString((String) entr.getKey(), titlex, titley);
                        g2.setColor(Color.WHITE);
                        // g2.drawString((String) entr.getKey(),titlex-2,
                        // titley-2);

                }
        }

        protected void fireSelectionListeners() {
                for (Iterator iter = selectionListeners.iterator(); iter.hasNext();) {
                        ChartSelectionListener listener = (ChartSelectionListener) iter.next();
                        listener.selectionChanged(this, this.viewFilterName, valueSelected);
                }

        }

        public void addSelectionListener(ChartSelectionListener l) {
                selectionListeners.add(l);
        }

        public void removeSelectionListener(ChartSelectionListener l) {
                selectionListeners.remove(l);
        }
}
