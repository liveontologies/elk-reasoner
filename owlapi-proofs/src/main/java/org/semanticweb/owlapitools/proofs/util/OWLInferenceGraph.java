/**
 * 
 */
package org.semanticweb.owlapitools.proofs.util;
/*
 * #%L
 * ELK Proofs Package
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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapitools.proofs.OWLInference;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;

/**
 * A simple unoptimized implementation of the Inference Graph where each
 * expression (node) is mapped to a list of inferences which use it as a
 * premise.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class OWLInferenceGraph {

	private final Set<OWLExpression> roots_;
	
	private boolean rootsUpdated = false;
	
	private final Map<OWLExpression, List<OWLInference>> nodes_;
	
	public OWLInferenceGraph() {
		roots_ = new HashSet<OWLExpression>();
		nodes_ = new HashMap<OWLExpression, List<OWLInference>>();
	}
	
	public Collection<OWLExpression> getRootExpressions() {
		if (!rootsUpdated) {
			updateRoots();
		}
		
		return Collections.unmodifiableCollection(roots_);
	}
	
	private void updateRoots() {
		for (OWLExpression expr : nodes_.keySet()) {
			if (OWLProofUtils.isAsserted(expr)) {
				roots_.add(expr);
			}
		}
		
		rootsUpdated = true;
	}

	public Collection<OWLInference> getInferencesForPremise(OWLExpression expression) {
		return nodes_.containsKey(expression) ? nodes_.get(expression) : Collections.<OWLInference>emptyList();
	}
	
	public Collection<OWLExpression> getExpressions() {
		return nodes_.keySet();
	}
	
	boolean addExpression(OWLExpression expr) {
		if (nodes_.containsKey(expr)) {
			return false;
		}
		
		nodes_.put(expr, new LinkedList<OWLInference>());
		
		return true;
	}
	
	void addInference(OWLInference inf) {
		rootsUpdated = false;
		
		for (OWLExpression premise : inf.getPremises()) {
			addExpression(premise);
			
			nodes_.get(premise).add(inf);
		}
		
		addExpression(inf.getConclusion());
 
		if (inf.getPremises().isEmpty()) {
			// some expressions are roots because they're produced by initialization inferences (or inferences whose premises are all tautologies, e.g. (A<=A, A<=A) |- A <= A and A)
			roots_.add(inf.getConclusion());
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("Roots: " + roots_ + "\n");
		
		for (OWLExpression expr : nodes_.keySet()) {
			builder.append(expr + " => " + nodes_.get(expr) + "\n");
		}
		
		return builder.toString();
	}
	
}
