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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.protege.editor.core.ui.list.MListButton;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
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
public class JustificationFrameSectionRow extends AbstractOWLFrameSectionRow<Explanation<OWLAxiom>, OWLAxiom, OWLAxiom>{

    private int depth;
    

    public JustificationFrameSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection<Explanation<OWLAxiom>, OWLAxiom, OWLAxiom> section, Explanation<OWLAxiom> rootObject, OWLAxiom axiom, int depth) {
        super(owlEditorKit, section, getOntologyForAxiom(owlEditorKit, axiom), rootObject, axiom);
        this.depth = depth;
    }

    public int getDepth() {
        return depth;
    }

    private static OWLOntology getOntologyForAxiom(OWLEditorKit editorKit, OWLAxiom axiom) {
//        for(OWLOntology ont : editorKit.getOWLModelManager().getActiveOntologies()) {
//            if(ont.containsAxiom(axiom)) {
//                return ont;
//            }
//        }
        return null;
    }

    @Override
    public String getRendering() {
        String rendering =  super.getRendering().replaceAll("\\s", " ");
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < depth; i++) {
            sb.append("        ");
        }
        sb.append(rendering);
        return sb.toString();
    }

    @Override
    public List<MListButton> getAdditionalButtons() {
        return Collections.emptyList();
    }

    @Override
    protected OWLObjectEditor<OWLAxiom> getObjectEditor() {
        return null;
    }

    @Override
    protected OWLAxiom createAxiom(OWLAxiom editedObject) {
        return null;
    }

    public List<? extends OWLObject> getManipulatableObjects() {
        return Arrays.asList(getAxiom());
    }

    @Override
    public boolean isEditable() {
        return true;
    }

    @Override
    public boolean isDeleteable() {
        return true;
    }

    @Override
    public boolean isInferred() {
        return false;
    }


}
