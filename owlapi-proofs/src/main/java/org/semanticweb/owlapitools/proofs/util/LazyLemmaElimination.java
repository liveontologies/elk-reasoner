/**
 * 
 */
package org.semanticweb.owlapitools.proofs.util;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;

import org.semanticweb.owlapitools.proofs.OWLInference;
import org.semanticweb.owlapitools.proofs.exception.ProofGenerationException;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;
import org.semanticweb.owlapitools.proofs.expressions.OWLLemmaExpression;
import org.semanticweb.owlapitools.proofs.util.Operations.Transformation;

/**
 * TODO
 * 
 * @author	Pavel Klinov
 * 			pavel.klinov@uni-ulm.de
 *
 */
public class LazyLemmaElimination implements Transformation<OWLInference, Iterable<OWLInference>> {

	@Override
	public Iterable<OWLInference> transform(final OWLInference inf) {
		
		return new Iterable<OWLInference>() {

			@Override
			public Iterator<OWLInference> iterator() {
				return lazyElimination(inf);
			}
			
		};
	}
	
	Iterator<OWLInference> lazyElimination(final OWLInference inf) {
		if (!lemmasPresent(inf)) {
			// a simple shortcut in case there are no lemmas, that should happen most of the time
			return Collections.singletonList(inf).iterator();
		}
		
		final Queue<OWLInference> toDo = new ArrayDeque<OWLInference>();
		
		toDo.add(inf);
		// returning the lazy iterator which will recursively replace inferences
		// with lemmas by inferences without a single lemma premise 
		return new Iterator<OWLInference>() {
			
			OWLInference next = null;

			@Override
			public boolean hasNext() {
				for (;;) {
					if (next != null) {
						return true;
					}
					
					OWLInference candidate = toDo.poll();
					
					if (candidate == null) {
						break;
					}
					
					boolean hasLemma = false;
					
					for (OWLExpression premise : candidate.getPremises()) {
						if (premise instanceof OWLLemmaExpression) {
							hasLemma = true;
							// replace the inference with a set of macro
							// inferences in which this lemma is sub'd by one of
							// its inference's premises
							try {
								List<OWLExpression> otherPremises = new ArrayList<OWLExpression>(candidate.getPremises());
								
								otherPremises.remove(premise);
								
								for (OWLInference premiseInf : premise.getInferences()) {
									List<OWLExpression> premises = new ArrayList<OWLExpression>(otherPremises);
									
									premises.addAll(premiseInf.getPremises());
									toDo.add(new MacroOWLInference("macro inference", candidate.getConclusion(), premises));
								}
							} catch (ProofGenerationException e) {
								// TODO log the failed transformation
							}
							// breaking at the first lemma to avoid duplicate
							// inferences if there are multiple lemma premises
							break;
						}
					}
					
					if (!hasLemma) {
						next = candidate;
						return true;
					}
				}
				
				return false;
			}

			@Override
			public OWLInference next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				
				OWLInference result = next;

				next = null;
				
				return result;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
			
		};
	}

	private boolean lemmasPresent(OWLInference inf) {
		for (OWLExpression premise : inf.getPremises()) {
			if (premise instanceof OWLLemmaExpression) {
				return true;
			}
		}
		
		return false;
	}

}
