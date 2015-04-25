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

import org.protege.editor.core.Disposable;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.inference.OWLReasonerManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapitools.proofs.ExplainingOWLReasoner;
import org.semanticweb.owlapitools.proofs.exception.ProofGenerationException;
import org.semanticweb.owlapitools.proofs.expressions.OWLAxiomExpression;
import org.semanticweb.owlapitools.proofs.util.CycleFreeProofRoot;
import org.semanticweb.owlapitools.proofs.util.OWLProofUtils;



/**
 * 
 * @author pavel
 *
 */
public class ProofManager implements Disposable {

    public static final String KEY = "org.semanticweb.owl.proofs.explanation";

    private OWLModelManager modelManager;

    private ProofManager(OWLModelManager modelManager) {
        this.modelManager = modelManager;
    }


    public OWLReasonerFactory getReasonerFactory() {
        return new ProtegeOWLReasonerFactoryWrapper(modelManager.getOWLReasonerManager().getCurrentReasonerFactory());
    }

    private ExplainingOWLReasoner getExplainingReasoner() {
    	OWLReasonerManager reasonerManager = modelManager.getOWLReasonerManager();
        OWLReasoner reasoner = reasonerManager.getCurrentReasoner();
        
        if (reasoner instanceof ExplainingOWLReasoner) {
        	return (ExplainingOWLReasoner) reasoner;
        }
        else {
        	throw new ExplanationException("The current reasoner (" + reasoner.getClass().getName() + ") does not support proof-based explanations");
        } 
    }
    
    private CycleFreeProofRoot blockCycles(OWLAxiomExpression root) throws ProofGenerationException {
    	return new CycleFreeProofRoot(root, OWLProofUtils.computeInferenceGraph(root));
    }
    
    public CycleFreeProofRoot getProofRootForInconsistency()  throws ExplanationException {
    	try {
			ExplainingOWLReasoner reasoner = getExplainingReasoner();
			OWLAxiomExpression root = reasoner.getDerivedExpressionForInconsistency();
			
			return blockCycles(root);
		} catch (ProofGenerationException e) {
			throw new ExplanationException(e);
		}
    }
    
    public CycleFreeProofRoot getProofRoot(OWLAxiom entailment)  throws ExplanationException {
    	// TODO caching
    	try {
			ExplainingOWLReasoner reasoner = getExplainingReasoner();
			OWLAxiomExpression root = reasoner.getDerivedExpression(entailment);
			
			return blockCycles(root);
		} catch (ProofGenerationException e) {
			throw new ExplanationException(e);
		}
    }

    public OWLOntologyManager getExplanationOntologyManager() {
        return modelManager.getOWLOntologyManager();
    }

    @Override
	public void dispose() {
    }

    public static synchronized ProofManager getExplanationManager(OWLModelManager modelManager) {
        ProofManager m = modelManager.get(KEY);
        
        if (m == null) {
            m = new ProofManager(modelManager);
            modelManager.put(KEY, m);
        }
        return m;
    }
}
