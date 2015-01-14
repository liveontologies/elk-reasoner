/**
 * 
 */
package org.semanticweb.owlapitools.proofs.util;
/*
 * #%L
 * OWL API Proofs Model
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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
 * The transformation which replaces inferences having {@link OWLLemmaExpression}s as premises by collections of inferences, one per the inference which derives each lemma premise.
 * The process is done recursively until there are no lemma premises.
 * 
 * There is a possibility of combinatorial explosion here if there are multiple lemma premises and they are derived by multiple inferences.
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
									toDo.add(new MacroOWLInference("Macro inference", candidate.getConclusion(), premises));
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
