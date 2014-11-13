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

import java.util.Comparator;
import java.util.List;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 19/03/2012
 */
public class JustificationFrameSection extends AbstractOWLFrameSection<Explanation<OWLAxiom>, OWLAxiom, OWLAxiom> {

    private static String LABEL = "Explanation for: ";

    private boolean filled = false;
    
    public JustificationFrameSection(OWLEditorKit editorKit, OWLFrame<? extends Explanation<OWLAxiom>> owlFrame) {
        super(editorKit, LABEL, owlFrame);
    }



    @Override
    public String getLabel() {
        Explanation<OWLAxiom> explanation = getRootObject();
        if(explanation == null) {
            return "";
        }
        OWLModelManager modelManager = getOWLEditorKit().getOWLModelManager();
        String rendering = modelManager.getRendering(explanation.getEntailment()).replaceAll("\\s+", " ");
        return LABEL + rendering;
    }

    @Override
    protected OWLAxiom createAxiom(OWLAxiom object) {
        return object;
    }

    @Override
    public OWLObjectEditor<OWLAxiom> getObjectEditor() {
        return null;
    }

    @Override
    protected void refill(OWLOntology ontology) {
        if(filled) {
            return;
        }
        
        filled = true;
        
        JustificationFormattingManager formattingManager = JustificationFormattingManager.getManager();
        Explanation<OWLAxiom> expl = getRootObject();
        List<OWLAxiom> formatting = formattingManager.getOrdering(expl);
        
        for(OWLAxiom ax : formatting) {
            int depth = formattingManager.getIndentation(expl, ax);
            JustificationFrameSectionRow row = new JustificationFrameSectionRow(getOWLEditorKit(), this, expl, ax, depth);
            addRow(row);
        }
        
        // add a frame section with a tree-like expandable proof representation
    }

    @Override
    protected void clear() {
        filled = false;
    }

    public Comparator<OWLFrameSectionRow<Explanation<OWLAxiom>, OWLAxiom, OWLAxiom>> getRowComparator() {
        return null;
    }

    @Override
    public boolean canAdd() {
        return false;
    }

    @Override
    public boolean canAcceptDrop(List<OWLObject> objects) {
        return false;
    }
}
