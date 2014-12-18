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

import java.util.Comparator;
import java.util.List;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;
import org.semanticweb.owlapitools.proofs.util.OWLProofUtils;

/**
 * Parameters: root object, axiom, editable object
 */
public class ProofFrameSection extends AbstractOWLFrameSection<OWLExpression, OWLAxiom, OWLAxiom> {

    private boolean filled = false;
    
    private final Iterable<? extends OWLExpression> expressions_;
    
    private final int depth_;
    
    public ProofFrameSection(OWLEditorKit editorKit, OWLFrame<OWLExpression> owlFrame, Iterable<? extends OWLExpression> exprs, String label, int depth) {
        super(editorKit, label, owlFrame);
        expressions_ = exprs;
        depth_ = depth;
    }

    int getDepth() {
    	return depth_;
    }
    
    @Override
	public String getName() {
    	String rendering = super.getRendering();
    	
		for (int i = 0; i < depth_; i++) {
			rendering = "       " + rendering;
		}
		
		return rendering;
	}

	@Override
    protected OWLAxiom createAxiom(OWLAxiom object) {
        return null;
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

        for(OWLExpression premise : expressions_) {
            ProofFrameSectionRow row = new ProofFrameSectionRow(getOWLEditorKit(), this, premise, OWLProofUtils.getAxiom(premise), depth_);
            
            addRow(row);
        }
    }

    @Override
    protected void clear() {
        filled = false;
    }

    public Comparator<OWLFrameSectionRow<OWLExpression, OWLAxiom, OWLAxiom>> getRowComparator() {
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
