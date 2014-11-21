package org.semanticweb.elk.explanations;
/*
 * #%L
 * Explanation Workbench
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.protege.editor.core.Disposable;
import org.protege.editor.owl.OWLEditorKit;
/*
 * Copyright (C) 2008, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


/**
 * Author: Matthew Horridge<br> The University Of Manchester<br> Information Management Group<br> Date:
 * 19-Oct-2008<br><br>
 */
public class ExplanationDisplayList extends JPanel implements Disposable {

    private OWLEditorKit editorKit;

    private ExplanationDisplay display;

    private JCheckBox displayLaconicCheckBox = new JCheckBox();

    private int explanationNumber;

    private int entailmentNumber;

    private WorkbenchManager workbenchManager;

    public ExplanationDisplayList(OWLEditorKit editorKit, WorkbenchManager workbenchManager, ExplanationDisplay display, int explanationNumber) {
        this.editorKit = editorKit;
        this.workbenchManager = workbenchManager;
        this.display = display;
        this.explanationNumber = explanationNumber;
        createUI();

    }


    private void createUI() {
        setLayout(new BorderLayout(2, 2));
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel("Proof tree");
        
        headerPanel.add(label);
        add(headerPanel, BorderLayout.NORTH);

        JPanel displayHolder = new JPanel(new BorderLayout());
        Border marginBorder = BorderFactory.createEmptyBorder(0, 20, 0, 0);
        Border lineBorder = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
        displayHolder.setBorder(BorderFactory.createCompoundBorder(marginBorder, lineBorder));
        displayHolder.add((Component) display);
        add(displayHolder);
   }

    public void dispose() {
        display.dispose();
    }

}
