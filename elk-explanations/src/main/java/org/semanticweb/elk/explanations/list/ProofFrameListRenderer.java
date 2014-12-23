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
import java.util.HashSet;
import java.util.Set;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.semanticweb.owlapi.model.OWLEntity;

/**
 * 
 * @author	Pavel Klinov
 * 			pavel.klinov@uni-ulm.de
 *
 */
public class ProofFrameListRenderer implements ListCellRenderer<Object> {

    private OWLCellRenderer owlCellRenderer;

    private ListCellRenderer<Object> separatorRenderer;

    private boolean highlightKeywords;

    private boolean highlightUnsatisfiableClasses;

    private boolean highlightUnsatisfiableProperties;

    private Set<OWLEntity> crossedOutEntities;

    public ProofFrameListRenderer(OWLEditorKit owlEditorKit) {
        owlCellRenderer = new OWLCellRenderer(owlEditorKit);
        separatorRenderer = new DefaultListCellRenderer();
        highlightKeywords = true;
        highlightUnsatisfiableClasses = true;
        highlightUnsatisfiableProperties = true;
        crossedOutEntities = new HashSet<OWLEntity>();
    }

    public void setHighlightKeywords(boolean highlightKeywords) {
        this.highlightKeywords = highlightKeywords;
    }

    public OWLCellRenderer getOWLCellRenderer() {
        return owlCellRenderer;
    }

    public void setHighlightUnsatisfiableClasses(boolean b) {
        highlightUnsatisfiableClasses = b;
    }


    public boolean isHighlightUnsatisfiableClasses() {
        return highlightUnsatisfiableClasses;
    }


    public boolean isHighlightUnsatisfiableProperties() {
        return highlightUnsatisfiableProperties;
    }


    public void setHighlightUnsatisfiableProperties(boolean h) {
        highlightUnsatisfiableProperties = h;
    }


    public void setCrossedOutEntities(Set<OWLEntity> entities) {
        crossedOutEntities.clear();
        crossedOutEntities.addAll(entities);
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                                                  boolean cellHasFocus) {


        if (value instanceof OWLFrameSection) {
            JLabel label = (JLabel) separatorRenderer.getListCellRendererComponent(list,
                                                                                   " ",
                                                                                   index,
                                                                                   isSelected,
                                                                                   cellHasFocus);
            label.setVerticalAlignment(JLabel.TOP);
            return label;
        }
        else {
            Object valueToRender = getValueToRender(list, (ProofFrameSectionRow) value, index, isSelected, cellHasFocus);
            
            owlCellRenderer.setCommentedOut(false);
            owlCellRenderer.setOntology(((ProofFrameSectionRow) value).getOntology());
            owlCellRenderer.setInferred(((ProofFrameSectionRow) value).isInferred());
            owlCellRenderer.setHighlightKeywords(highlightKeywords);
            owlCellRenderer.setHighlightUnsatisfiableClasses(highlightUnsatisfiableClasses);
            owlCellRenderer.setCrossedOutEntities(crossedOutEntities);
            
            return owlCellRenderer.getListCellRendererComponent(list,
                                                                valueToRender,
                                                                index,
                                                                isSelected,
                                                                cellHasFocus);
        }
    }

    public void setWrap(boolean b) {
        owlCellRenderer.setWrap(b);
    }

    protected Object getValueToRender(JList<?> list, ProofFrameSectionRow value, int index, boolean isSelected, boolean cellHasFocus) {
        return value.getRendering();
    }
}
