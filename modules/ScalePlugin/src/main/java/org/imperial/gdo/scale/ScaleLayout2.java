/*
 Original source by code from Gephi
 Modifications made by Huangsong Wei
 Modified for Individual Project for completion of degree in Msc Computing 
 */

/*
 Copyright 2008-2010 Gephi
 Authors : Helder Suzuki <heldersuzuki@gephi.org>
 Website : http://www.gephi.org

 This file is part of Gephi.

 DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

 Copyright 2011 Gephi Consortium. All rights reserved.

 The contents of this file are subject to the terms of either the GNU
 General Public License Version 3 only ("GPL") or the Common
 Development and Distribution License("CDDL") (collectively, the
 "License"). You may not use this file except in compliance with the
 License. You can obtain a copy of the License at
 http://gephi.org/about/legal/license-notice/
 or /cddl-1.0.txt and /gpl-3.0.txt. See the License for the
 specific language governing permissions and limitations under the
 License.  When distributing the software, include this License Header
 Notice in each file and include the License files at
 /cddl-1.0.txt and /gpl-3.0.txt. If applicable, add the following below the
 License Header, with the fields enclosed by brackets [] replaced by
 your own identifying information:
 "Portions Copyrighted [year] [name of copyright owner]"

 If you wish your version of this file to be governed by only the CDDL
 or only the GPL Version 3, indicate your decision by adding
 "[Contributor] elects to include this software in this distribution
 under the [CDDL or GPL Version 3] license." If you do not indicate a
 single choice of license, a recipient has the option to distribute
 your version of this file under either the CDDL, the GPL Version 3 or
 to extend the choice of license to its licensees as provided above.
 However, if you add GPL Version 3 code and therefore, elected the GPL
 Version 3 license, then the option applies only if the new code is
 made subject to such option by the copyright holder.

 Contributor(s):

 Portions Copyrighted 2011 Gephi Consortium.
 */
package org.imperial.gdo.scale;


import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;

import org.gephi.layout.spi.Layout;
import org.gephi.layout.spi.LayoutBuilder;
import org.gephi.layout.spi.LayoutProperty;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;


public class ScaleLayout2 extends AbstractLayout implements Layout {

    private double x_scale,y_scale;
    private int part_start, part_end;
    private Graph graph;

    public ScaleLayout2(LayoutBuilder layoutBuilder, double x_scale, double y_scale, int part_start, int part_end) {
        super(layoutBuilder);
        this.x_scale = x_scale;
        this.y_scale = y_scale;
        this.part_start=part_start;
        this.part_end=part_end;
    }

    @Override
    public void initAlgo() {
        setConverged(false);
    }

    @Override
    public void goAlgo() {
        graph = graphModel.getGraphVisible();
        graph.readLock();
        
       
        
        try {
            for (int currentPart = part_start; currentPart <= part_end; currentPart++) {
                int partNodeCount=0;
                for (Node n : graph.getNodes()) {
                    
                    if (n.getAttribute("part").equals(currentPart)) {
                        partNodeCount += 1;
                    }
                    
                }
                
                double xMean = 0, yMean = 0;
                for (Node n : graph.getNodes()) {
                    if (n.getAttribute("part").equals(currentPart)) {
                        xMean += n.x();
                        yMean += n.y();
                    }
                }
                xMean /= partNodeCount;
                yMean /= partNodeCount;
                
                for (Node n : graph.getNodes()) {
                    if (n.getAttribute("part").equals(currentPart)) {
                        double dx = (n.x() - xMean) * getXScale();
                        double dy = (n.y() - yMean) * getYScale();
                        
                        n.setX((float) (xMean + dx));
                        n.setY((float) (yMean + dy));
                    }
                }
            }
       
            setConverged(true);
        } 
        catch(Exception e){
            Logger.getLogger("").log(Level.INFO, "algorithm did not run");
        }
        finally{        
            graph.readUnlockAll();
    }
    }

    @Override
    public void endAlgo() {
    }

    @Override
    public LayoutProperty[] getProperties() {
        List<LayoutProperty> properties = new ArrayList<>();
        try {
            properties.add(LayoutProperty.createProperty(
                    this, Double.class,
                    NbBundle.getMessage(getClass(), "ScaleLayout.XscaleFactor.name"),
                    null,
                    "ScaleLayout.XscaleFactor.name",
                    NbBundle.getMessage(getClass(), "ScaleLayout.XscaleFactor.desc"),
                    "getXScale", "setXScale"));
            properties.add(LayoutProperty.createProperty(
                   this, Double.class,
                   NbBundle.getMessage(getClass(), "ScaleLayout.YscaleFactor.name"),
                   null,
                   "ScaleLayout.YscaleFactor.name",
                   NbBundle.getMessage(getClass(), "ScaleLayout.YscaleFactor.desc"),
                   "getYScale", "setYScale"));
            properties.add(LayoutProperty.createProperty(
                    this, Integer.class,
                    NbBundle.getMessage(getClass(), "ScaleLayout.scalePart_start.name"),
                    null,
                    "ScaleLayout.scalePart_start.name",
                    NbBundle.getMessage(getClass(), "ScaleLayout.scalePart_start.desc"),
                    "getPart_start", "setPart_start"));
            properties.add(LayoutProperty.createProperty(
                    this, Integer.class,
                    NbBundle.getMessage(getClass(), "ScaleLayout.scalePart_end.name"),
                    null,
                    "ScaleLayout.scalePart_end.name",
                    NbBundle.getMessage(getClass(), "ScaleLayout.scalePart_end.desc"),
                    "getPart_end", "setPart_end"));
        } catch (Exception e) {
            Exceptions.printStackTrace(e);
        }
        return properties.toArray(new LayoutProperty[0]);
    }

    @Override
    public void resetPropertiesValues() {
    }

    
    
    /**
     * @return the Xscale
     */
    public Double getXScale() {
        return x_scale;
    }

    /**
     * @param Xscale the Xscale to set
     */
    public void setXScale(Double x_scale) {
        this.x_scale = x_scale;
    }
    
    /**
     * @return the Yscale
     */
    public Double getYScale() {
        return y_scale;
    }

    /**
     * @param Yscale the Yscale to set
     */
    public void setYScale(Double y_scale) {
        this.y_scale = y_scale;
    }
    
    /**
     * @return the part number
     */
    public Integer getPart_start() {
        return this.part_start;
    }

    /**
     * @param part the part to set
     */
    public void setPart_start(Integer part_start) {
        this.part_start = part_start;
    }
    
    /**
     * @return the part number
     */
    public Integer getPart_end() {
        return this.part_end;
    }

    /**
     * @param part the part to set
     */
    public void setPart_end(Integer part_end) {
        this.part_end = part_end;
    }
    
    
}
