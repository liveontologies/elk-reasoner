package org.semanticweb.elk.explanations.list;
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

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JList;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.framelist.OWLFrameListRenderer;
import org.semanticweb.owlapi.model.OWLObject;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 19/03/2012
 */
public class ProofFrameListRenderer extends OWLFrameListRenderer {

    public ProofFrameListRenderer(OWLEditorKit owlEditorKit) {
        super(owlEditorKit);
        //setHighlightUnsatisfiableClasses(false);
        //setHighlightUnsatisfiableProperties(false);
    }

    @Override
    protected OWLObject getIconObject(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        return null;
    }

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		JComponent c = (JComponent) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		
		//c.setBorder(BorderFactory.createLineBorder(Color.red));
		
		return c;
	}
    
    
}
