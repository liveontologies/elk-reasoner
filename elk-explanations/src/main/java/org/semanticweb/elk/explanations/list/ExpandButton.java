/**
 * 
 */
package org.semanticweb.elk.explanations.list;
/*
 * #%L
 * Explanation Workbench
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;

import org.protege.editor.core.ui.list.MListButton;

/**
 * @author	Pavel Klinov
 * 			pavel.klinov@uni-ulm.de
 *
 */
public class ExpandButton extends MListButton {

    public ExpandButton(ActionListener actionListener) {
        super("Explain inference", new Color(100, 40, 140), actionListener);
    }


    public void paintButtonContent(Graphics2D gIn) {
    	Graphics2D g = (Graphics2D) gIn.create();
        int size = getBounds().height;
        int thickness = (Math.round(size / 8.0f) / 2) * 2;

        int x = getBounds().x;
        int y = getBounds().y;

        int insetX = size / 4;
        int insetY = size / 4;
        int insetHeight = size / 2;
        int insetWidth = size / 2;
        g.fillRect(x + size / 2  - thickness / 2, y + insetY, thickness, insetHeight);
        g.fillRect(x + insetX, y + size / 2 - thickness / 2, insetWidth, thickness);
    }
}
