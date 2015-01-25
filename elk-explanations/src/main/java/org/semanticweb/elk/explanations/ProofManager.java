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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.protege.editor.core.Disposable;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.inference.OWLReasonerManager;
import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owl.explanation.api.ExplanationException;
import org.semanticweb.owl.explanation.api.ExplanationGenerator;
import org.semanticweb.owl.explanation.impl.blackbox.checker.InconsistentOntologyExplanationGeneratorFactory;
import org.semanticweb.owl.explanation.impl.laconic.LaconicExplanationGeneratorFactory;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapitools.proofs.ExplainingOWLReasoner;
import org.semanticweb.owlapitools.proofs.OWLInference;
import org.semanticweb.owlapitools.proofs.exception.ProofGenerationException;
import org.semanticweb.owlapitools.proofs.expressions.OWLAxiomExpression;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpressionVisitor;
import org.semanticweb.owlapitools.proofs.expressions.OWLLemmaExpression;
import org.semanticweb.owlapitools.proofs.util.CycleBlockingExpression;
import org.semanticweb.owlapitools.proofs.util.OWLProofUtils;



/**
 * 
 * @author pavel
 *
 */
public class ProofManager implements Disposable, OWLReasonerProvider {

    private final OWLOntologyChangeListener ontologyChangeListener;

    public static final String KEY = "org.semanticweb.owl.proofs.explanation";

    private OWLModelManager modelManager;

    private CachingRootDerivedGenerator rootDerivedGenerator;

    private List<ExplanationManagerListener> listeners;

    private int explanationLimit;

    private boolean findAllExplanations;

    private JustificationCacheManager justificationCacheManager = new JustificationCacheManager();

    private ProofManager(OWLModelManager modelManager) {
        this.modelManager = modelManager;
        rootDerivedGenerator = new CachingRootDerivedGenerator(modelManager);
        listeners = new ArrayList<ExplanationManagerListener>();
        explanationLimit = 2;
        findAllExplanations = true;
        ontologyChangeListener = new OWLOntologyChangeListener() {
            @Override
			public void ontologiesChanged(List<? extends OWLOntologyChange> changes) throws OWLException {
                justificationCacheManager.clear();
            }
        };
        modelManager.addOntologyChangeListener(ontologyChangeListener);
    }


    public OWLReasonerProvider getReasonerProvider() {
        return this;
    }

    public OWLReasonerFactory getReasonerFactory() {
        return new ProtegeOWLReasonerFactoryWrapper(modelManager.getOWLReasonerManager().getCurrentReasonerFactory());
    }

    public int getExplanationLimit() {
        return explanationLimit;
    }

    public void setExplanationLimit(int explanationLimit) {
        this.explanationLimit = explanationLimit;
        fireExplanationLimitChanged();
    }


    public boolean isFindAllExplanations() {
        return findAllExplanations;
    }


    public void setFindAllExplanations(boolean findAllExplanations) {
        this.findAllExplanations = findAllExplanations;
        fireExplanationLimitChanged();
    }

    @Override
	public OWLReasoner getReasoner() {
        return modelManager.getReasoner();
    }


    /**
     * Gets the number of explanations that have actually been computed for an entailment
     * @param entailment The entailment
     * @return The number of computed explanations.  If no explanations have been computed this value
     *         will be -1.
     */
    public int getComputedExplanationCount(OWLAxiom entailment, JustificationType type) {
        JustificationCache cache = justificationCacheManager.getJustificationCache(type);
        if(cache.contains(entailment)) {
            return cache.get(entailment).size();
        }
        else {
            return -1;
        }
    }
    
    public CycleBlockingExpression getProofRoot(OWLAxiom entailment)  throws ExplanationException {
    	// TODO caching
        OWLReasonerManager reasonerManager = modelManager.getOWLReasonerManager();
        OWLReasoner reasoner = reasonerManager.getCurrentReasoner();
        
        if (reasoner instanceof ExplainingOWLReasoner) {
        	ExplainingOWLReasoner explainingReasoner = (ExplainingOWLReasoner) reasoner;
        	
        	try {
        		OWLExpression root = explainingReasoner.getDerivedExpression(entailment);
        		// first eliminate possible lemmas since we can't render them in Protege
        		//root = new TransformedOWLExpression<GenericLemmaElimination>(root, new GenericLemmaElimination());
        		// second block loopy proofs
        		return new CycleBlockingExpression(root, OWLProofUtils.computeInferenceGraph(root));
			} catch (Exception e) {
				throw new ExplanationException(e);
			}
        }
        else {
        	throw new ExplanationException("The current reasoner (" + reasoner.getClass().getName() + ") does not support proof-based explanations");
        }    	
    }

    public Set<Explanation<OWLAxiom>> getJustifications(OWLAxiom entailment, JustificationType type) throws ExplanationException {
        JustificationCache cache = justificationCacheManager.getJustificationCache(type);
        
        if (!cache.contains(entailment)) {
            Set<Explanation<OWLAxiom>> expls = computeExplanations(entailment);
            
            cache.put(expls);
        }
        return cache.get(entailment);
    }

    public Explanation<OWLAxiom> getLaconicJustification(Explanation<OWLAxiom> explanation) {
        Set<Explanation<OWLAxiom>> explanations = getLaconicExplanations(explanation, 1);
        if(explanations.isEmpty()) {
            return Explanation.getEmptyExplanation(explanation.getEntailment());
        }
        else {
            return explanations.iterator().next();
        }
    }


    private Set<Explanation<OWLAxiom>> computeExplanations(OWLAxiom entailment) throws ExplanationException {
        // let's first get the reasoner
        OWLReasonerManager reasonerManager = modelManager.getOWLReasonerManager();
        OWLReasoner reasoner = reasonerManager.getCurrentReasoner();
        
        if (reasoner instanceof ExplainingOWLReasoner) {
        	ExplainingOWLReasoner explainingReasoner = (ExplainingOWLReasoner) reasoner;
        	//for now just get all axioms that are used in the proofs and return them as a single explanation.
        	Set<OWLAxiom> allUsedAxioms = Collections.emptySet();
        	
			try {
				allUsedAxioms = getUsedAxioms(explainingReasoner, entailment);
			} catch (ProofGenerationException e) {
				throw new ExplanationException(e);
			}
        	
        	return Collections.singleton(new Explanation<OWLAxiom>(entailment, allUsedAxioms));
        }
        else {
        	throw new ExplanationException("The current reasoner (" + reasoner.getClass().getName() + ") does not support proof-based explanations");
        }
    }
    
    // TODO add a generic unwinding method which returns an iterator over axioms
	private Set<OWLAxiom> getUsedAxioms(ExplainingOWLReasoner reasoner, OWLAxiom entailment) throws ProofGenerationException {
		final Set<OWLAxiom> allUsedAxioms = new HashSet<OWLAxiom>();
		OWLExpression expression = reasoner.getDerivedExpression(entailment);
		// Start recursive unwinding
		LinkedList<OWLExpression> toDo = new LinkedList<OWLExpression>();
		Set<OWLExpression> done = new HashSet<OWLExpression>();
		OWLExpressionVisitor<?> adder = new OWLExpressionVisitor<Void>() {

			@Override
			public Void visit(OWLAxiomExpression expression) {
				if (expression.isAsserted()) {
					allUsedAxioms.add(expression.getAxiom());
				}
				return null;
			}

			@Override
			public Void visit(OWLLemmaExpression expression) {
				return null;
			}
			
		};
		
		toDo.add(expression);
		done.add(expression);
		
		for (;;) {
			OWLExpression next = toDo.poll();
			
			if (next == null) {
				break;
			}
			
			for (OWLInference inf : next.getInferences()) {
				// Recursively unwind premise inferences
				for (OWLExpression premise : inf.getPremises()) {
					premise.accept(adder);
					
					if (done.add(premise)) {
						toDo.addFirst(premise);
					}
				}
				
				// Uncomment if only interested in one inference per derived expression (that is sufficient to reconstruct one proof)
				// break;
			}
		}
		
		return allUsedAxioms;
	}

    public OWLOntologyManager getExplanationOntologyManager() {
        return modelManager.getOWLOntologyManager();
    }


    public Set<Explanation<OWLAxiom>> getLaconicExplanations(Explanation<OWLAxiom> explanation, int limit) throws ExplanationException {
        return computeLaconicExplanations(explanation, limit);
    }


    private Set<Explanation<OWLAxiom>> computeLaconicExplanations(Explanation<OWLAxiom> explanation, int limit) throws ExplanationException {
        try {
            if(modelManager.getReasoner().isConsistent()) {
                OWLReasonerFactory rf = getReasonerFactory();
                ExplanationGenerator<OWLAxiom> g = org.semanticweb.owl.explanation.api.ExplanationManager.createLaconicExplanationGeneratorFactory(rf).createExplanationGenerator(explanation.getAxioms());
                return g.getExplanations(explanation.getEntailment(), limit);
            }
            else {
                OWLReasonerFactory rf = getReasonerFactory();
                InconsistentOntologyExplanationGeneratorFactory fac = new InconsistentOntologyExplanationGeneratorFactory(rf, Long.MAX_VALUE);
                LaconicExplanationGeneratorFactory<OWLAxiom> lacFac = new LaconicExplanationGeneratorFactory<OWLAxiom>(fac);
                ExplanationGenerator<OWLAxiom> g = lacFac.createExplanationGenerator(explanation.getAxioms());
                return g.getExplanations(explanation.getEntailment(), limit);
            }
        }
        catch (ExplanationException e) {
            throw new ExplanationException(e);
        }
    }


    @Override
	public void dispose() {
        rootDerivedGenerator.dispose();
        modelManager.removeOntologyChangeListener(ontologyChangeListener);
    }


    public void addListener(ExplanationManagerListener lsnr) {
        listeners.add(lsnr);
    }

    public void removeListener(ExplanationManagerListener lsnr) {
        listeners.remove(lsnr);
    }

    protected void fireExplanationLimitChanged() {
        for (ExplanationManagerListener lsnr : new ArrayList<ExplanationManagerListener>(listeners)) {
            lsnr.explanationLimitChanged(this);
        }
    }

    protected void fireExplanationsComputed(OWLAxiom entailment) {
        for (ExplanationManagerListener lsnr : new ArrayList<ExplanationManagerListener>(listeners)) {
            lsnr.explanationsComputed(entailment);
        }
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
