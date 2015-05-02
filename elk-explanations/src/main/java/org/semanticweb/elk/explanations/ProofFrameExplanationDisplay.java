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

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.elk.explanations.list.ProofFrame;
import org.semanticweb.elk.explanations.list.ProofFrameList;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapitools.proofs.util.CycleFreeProofRoot;

/**
 * 
 * @author Pavel Klinov
 *
 */
@SuppressWarnings("serial")
public class ProofFrameExplanationDisplay extends JPanel {
    
	static final String SUBSUMPTION_TITLE = "Proof tree for class subsumption";
	
	static final String INCONSISTENCY_TITLE = "Proof tree for ontology inconsistency";
	
    private ProofFrame frame;

    private final ProofFrameList frameList;

    public ProofFrameExplanationDisplay(
    		final OWLEditorKit editorKit, 
    		CycleFreeProofRoot root,
    		String title, 
    		ProofWorkbenchPanel proofWorkbenchPanel) {
        frame = new ProofFrame(root, new OWLRenderer() {
			
			@Override
			public String render(OWLObject obj) {
				return editorKit.getOWLModelManager().getRendering(obj);
			}
		}, editorKit.getOWLModelManager().getActiveOntology(),
		title);
        
        setLayout(new BorderLayout());
        
        frameList =  new ProofFrameList(editorKit, frame);
        add(frameList, BorderLayout.NORTH);       
        frameList.setBorder(BorderFactory.createEmptyBorder(7, 10, 7, 10));
    }
    
    public void update(CycleFreeProofRoot newRoot) {
    	frame.setRootObject(newRoot);
    	frameList.refreshComponent();
    }

    public void dispose() {
        frame.dispose();
    }
	
    public void setReasonerSynchronized(boolean v) {
    	frame.setReasonerSynchronized(v);
    }
}
