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

import org.protege.editor.owl.ui.explanation.ExplanationResult;
import org.protege.editor.owl.ui.explanation.ExplanationService;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;


/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 18/03/2012
 */
public class ProofBasedExplanationService extends ExplanationService {

	private WorkbenchPanelExplanationResult explanationResult_ = null;
	
    @Override
    public void initialise() throws Exception {
    	//no-op    	
    }

    @Override
    public boolean hasExplanation(OWLAxiom axiom) {
		return axiom instanceof OWLSubClassOfAxiom
				|| axiom instanceof OWLEquivalentClassesAxiom;
    }

    @Override
    public ExplanationResult explain(OWLAxiom entailment) {
        ProofWorkbenchPanel workbenchPanel = new ProofWorkbenchPanel(getOWLEditorKit(), entailment);
        
        return explanationResult_ = new WorkbenchPanelExplanationResult(workbenchPanel);
    }

    @Override
	public void dispose() throws Exception {
    	if (explanationResult_ != null) {
    		explanationResult_.dispose();
    	}
    }
}
