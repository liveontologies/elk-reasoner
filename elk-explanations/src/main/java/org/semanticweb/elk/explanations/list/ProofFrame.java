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
	
	private ProofFrameSection rootSection_;
	
    private final List<OWLFrameListener> listeners_ = new ArrayList<OWLFrameListener>(2);
    
    private CycleFreeProofRoot rootExpression_;

    private final OWLRenderer renderer_;
	
    public ProofFrame(CycleFreeProofRoot proofRoot, OWLRenderer renderer, OWLOntology active, String title) {
    	renderer_ = renderer;
    	rootExpression_ = proofRoot;
    	rootSection_ = new ProofFrameSection(this, Collections.singletonList(proofRoot), title, 0, renderer);
    	rootSection_.refill(active);
    }

	public void blockInferencesForPremise(OWLExpression premise) {
		CycleFreeProofRoot root = getRootObject();
		CycleFreeProofRoot updatedRoot = root.blockExpression(premise);
		// this will update the hierarchical model (sections and rows)
		
		//FIXME
		System.err.println("Blocked " + premise + ", root replaced");
		
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
	
	// called after the root has been updated
	void refresh() {
		try {
			if (!rootExpression_.getInferences().iterator().hasNext()) {
				// the root expression is no longer entailed
				String rendering = renderer_.render(OWLProofUtils.getAxiom(rootExpression_));
				
				rootSection_.dispose();
				rootSection_ = new ProofFrameSection(this, Collections.<OWLExpression>emptyList(), String.format("%s is no longer entailed by the ontology", rendering), 0, renderer_);
			}
			else {
				// run down the model and refresh it
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
	

    /*private void showAxiomEditor(final OWLAxiom axiom) {
    	final AxiomExpressionEditor editor = new AxiomExpressionEditor(kit_);
        final JComponent editorComponent = editor.getEditorComponent();
        @SuppressWarnings("serial")
		final VerifyingOptionPane optionPane = new VerifyingOptionPane(editorComponent) {

            public void selectInitialValue() {
                // This is overriden so that the option pane dialog default
                // button doesn't get the focus.
            }
        };
        final InputVerificationStatusChangedListener verificationListener = new InputVerificationStatusChangedListener() {
            public void verifiedStatusChanged(boolean verified) {
                optionPane.setOKEnabled(verified);
            }
        };
        // Protege's syntax checkers only cover the class axiom's syntax
        editor.setEditedObject((OWLClassAxiom) axiom);
        // prevent the OK button from being available until the expression is syntactically valid
        editor.addStatusChangedListener(verificationListener);
        
        JDialog dlg = optionPane.createDialog(this, null);

        dlg.setModal(false);
        dlg.setResizable(true);
        dlg.pack();
        dlg.setLocationRelativeTo(this);
        dlg.addComponentListener(new ComponentAdapter() {

            public void componentHidden(ComponentEvent e) {
                Object retVal = optionPane.getValue();
                
                editorComponent.setPreferredSize(editorComponent.getSize());
                
                if (retVal != null && retVal.equals(JOptionPane.OK_OPTION)) {
                    handleEditFinished(axiom, editor.getEditedObject());
                }
                
                //setSelectedValue(frameObject, true);
                
                editor.removeStatusChangedListener(verificationListener);
                editor.dispose();
            }
        });

        dlg.setTitle("Class axiom expression editor");
        dlg.setVisible(true);
    }
    
	private void handleEditFinished(OWLAxiom oldAxiom, OWLAxiom newAxiom) {
		List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
		OWLOntology ontology = kit_.getOWLModelManager().getActiveOntology();
		// remove the old axiom
		changes.add(new RemoveAxiom(ontology, oldAxiom));
		changes.add(new AddAxiom(ontology, newAxiom));

		kit_.getOWLModelManager().applyChanges(changes);
	}*/
    
}
