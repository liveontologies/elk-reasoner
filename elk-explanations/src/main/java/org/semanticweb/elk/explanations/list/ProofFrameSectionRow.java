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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.protege.editor.core.ui.list.MListButton;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;
import org.semanticweb.owlapitools.proofs.util.OWLProofUtils;

/**
 * 
 * @author	Pavel Klinov
 * 			pavel.klinov@uni-ulm.de
 *
 */
public class ProofFrameSectionRow extends AbstractOWLFrameSectionRow<OWLExpression, OWLAxiom, OWLAxiom>{

    private final int depth;

    public ProofFrameSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection<OWLExpression, OWLAxiom, OWLAxiom> section, OWLExpression rootObject, OWLAxiom axiom, int depth) {
        super(owlEditorKit, section, owlEditorKit.getOWLModelManager().getActiveOntology(), rootObject, axiom);
        
        this.depth = depth;
    }

    public int getDepth() {
        return depth;
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
        return !OWLProofUtils.isAsserted(getRoot());
    }


}
