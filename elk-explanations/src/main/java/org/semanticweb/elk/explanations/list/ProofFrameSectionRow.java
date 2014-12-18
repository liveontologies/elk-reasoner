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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.ui.list.MListButton;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapitools.proofs.OWLInference;
import org.semanticweb.owlapitools.proofs.exception.ProofGenerationException;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;
import org.semanticweb.owlapitools.proofs.util.OWLProofUtils;

/**
 * Represents an item in the list which holds an {@link OWLExpression} represented by an {@link OWLAxiom}.
 * 
 * @author	Pavel Klinov
 * 			pavel.klinov@uni-ulm.de
 *
 */
public class ProofFrameSectionRow extends AbstractOWLFrameSectionRow<OWLExpression, OWLAxiom, OWLAxiom>{

    private final int depth_;
    
    private boolean expanded_ = false;
    
    private List<ProofFrameSection> inferenceSections_ = new ArrayList<ProofFrameSection>(2);

    public ProofFrameSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection<OWLExpression, OWLAxiom, OWLAxiom> section, OWLExpression rootObject, OWLAxiom axiom, int depth) {
        super(owlEditorKit, section, /*owlEditorKit.getOWLModelManager().getActiveOntology()*/null, rootObject, axiom);
        
        this.depth_ = depth;
    }

    public int getDepth() {
        return depth_;
    }
    
    boolean isExpanded() {
    	return expanded_;
    }
    
    void setExpanded(boolean v) {
    	expanded_ = v;
    }
    
    boolean isFilled() {
    	return !isInferred() || !inferenceSections_.isEmpty();
    }
    
    List<ProofFrameSection> getInferenceSections() {
    	return inferenceSections_;
    }
    
    void refillInferenceSections() {
    	inferenceSections_.clear();
    	
    	try {
			for (OWLInference inf : getRootObject().getInferences()) {
				ProofFrameSection inferenceSection = new ProofFrameSection(getOWLEditorKit(), getFrameSection().getFrame(), inf.getPremises(), inf.getName(), depth_ + 1);
				// filling up rows with premises
				inferenceSection.refill(getOntology());
				inferenceSections_.add(inferenceSection);
			}
		} catch (ProofGenerationException e) {
			ProtegeApplication.getErrorLog().logError(e);
		}
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
        return !isInferred();
    }

    @Override
    public boolean isDeleteable() {
        return isEditable();
    }

    @Override
    public boolean isInferred() {
        return !OWLProofUtils.isAsserted(getRoot());
    }

	public void disposeOfRow() {
		for (ProofFrameSection section : inferenceSections_) {
			section.dispose();
		}
	}

    
}
