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
import java.util.Collections;
import java.util.List;

import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameListener;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.semanticweb.elk.explanations.OWLRenderer;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapitools.proofs.exception.ProofGenerationException;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;
import org.semanticweb.owlapitools.proofs.util.CycleFreeProofRoot;
import org.semanticweb.owlapitools.proofs.util.OWLProofUtils;

/**
 * 
 * @author	Pavel Klinov
 * 			pavel.klinov@uni-ulm.de
 *
 */
public class ProofFrame implements OWLFrame<CycleFreeProofRoot> {
	
	static final String MAY_NOT_BE_ENTAILED = "%s may no longer be entailed by the ontology due to the performed changes. Please synchronize the reasoner.";
	
	static final String NOT_ENTAILED = "%s is no longer entailed by the ontology due to the performed changes.";
	
	private ProofFrameSection rootSection_;
	
    private final List<OWLFrameListener> listeners_ = new ArrayList<OWLFrameListener>(2);
    
    private CycleFreeProofRoot rootExpression_;

    private final OWLRenderer renderer_;
    
    private final OWLOntology activeOntology_;
    
    private final String originalTitle_;
    
    private boolean reasonerInSync_ = true;
    
    private boolean fullyExpanded_ = false;
	
    public ProofFrame(CycleFreeProofRoot proofRoot, OWLRenderer renderer, OWLOntology active, String title) {
    	renderer_ = renderer;
    	activeOntology_ = active;
    	rootExpression_ = proofRoot;
    	rootSection_ = new ProofFrameSection(this, Collections.singletonList(proofRoot), title, 0, renderer);
    	originalTitle_ = title;
    	rootSection_.refill();
    	
    	//FIXME
		/*try {
			System.err.println(OWLProofUtils.printProofTree(proofRoot));
		} catch (ProofGenerationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
    }

    OWLOntology getActiveOntology() {
    	return activeOntology_;
    }
    
    void setFullyExpanded(boolean expanded) {
    	fullyExpanded_ = expanded;
    }
    
    boolean isFullyExpanded() {
    	return fullyExpanded_;
    }
    
    public void setReasonerSynchronized(boolean v) {
    	reasonerInSync_ = v;
    }
    
	public void blockInferencesForPremise(OWLExpression premise) {
		CycleFreeProofRoot root = getRootObject();
		CycleFreeProofRoot updatedRoot = root.blockExpression(premise);
		// this will update the hierarchical model (sections and rows)		
		setRootObject(updatedRoot);
	}

	public ProofFrameSection getRootSection() {
		return rootSection_;
	}
	
	@Override
	public void dispose() {
		rootSection_.dispose();
	}

	@Override
	public void setRootObject(CycleFreeProofRoot expr) {
		rootExpression_ = expr;
		refresh();
	}
	
	// fully expands the proof
	void fullyExpand() {
		rootSection_.expand();
		setFullyExpanded(true);
	}
	
	// called after the root has been updated
	void refresh() {
		try {
			if (!rootExpression_.getInferences().iterator().hasNext()) {
				// the root expression is no longer entailed
				String rendering = renderer_.render(OWLProofUtils.getAxiom(rootExpression_));
				String msg = reasonerInSync_ ? NOT_ENTAILED : MAY_NOT_BE_ENTAILED;
				
				rootSection_.dispose();
				rootSection_ = new ProofFrameSection(this, Collections.<OWLExpression>emptyList(), String.format(msg, rendering), 0, renderer_);
				setFullyExpanded(true);
			}
			else {
				// run down the model and refresh it
				rootSection_.setLabel(originalTitle_);
				rootSection_.update(Collections.singletonList(rootExpression_));
			}
		} catch (ProofGenerationException e) {
			ProtegeApplication.getErrorLog().logError(e);
		}
	}

	@Override
	public CycleFreeProofRoot getRootObject() {
		return rootExpression_;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<OWLFrameSection> getFrameSections() {
		return Collections.<OWLFrameSection>singletonList(rootSection_);
	}

    @Override
	public void addFrameListener(OWLFrameListener listener) {
        listeners_.add(listener);
    }


    @Override
	public void removeFrameListener(OWLFrameListener listener) {
        listeners_.remove(listener);
    }

    @Override
	public void fireContentChanged() {
        for (OWLFrameListener listener : listeners_) {
            try {
                listener.frameContentChanged();
            }
            catch (Exception e) {
            	ProtegeApplication.getErrorLog().logError(e);
            }
        }
    }
	
}
