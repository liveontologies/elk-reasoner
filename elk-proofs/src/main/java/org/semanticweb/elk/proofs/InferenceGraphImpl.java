/**
 * 
 */
package org.semanticweb.elk.proofs;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.elk.proofs.expressions.Expression;
import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.elk.proofs.utils.ProofUtils;
import org.semanticweb.elk.util.collections.ArrayHashSet;

/**
 * A simple unoptimized implementation of the Inference Graph where each
 * expression (node) is mapped to a list of inferences which use it as a
 * premise.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class InferenceGraphImpl implements InferenceGraph {

	private final Set<Expression> roots_ = new ArrayHashSet<Expression>();
	
	private boolean rootsUpdated = false;
	
	private final Map<Expression, List<Inference>> nodes_;
	
	public InferenceGraphImpl() {
		nodes_ = new HashMap<Expression, List<Inference>>();
	}
	
	@Override
	public Collection<Expression> getRootExpressions() {
		if (!rootsUpdated) {
			updateRoots();
			rootsUpdated = true;
		}
		
		return Collections.unmodifiableCollection(roots_);
	}

	@Override
	public Collection<Inference> getInferencesForPremise(Expression expression) {
		return nodes_.containsKey(expression) ? nodes_.get(expression) : Collections.<Inference>emptyList();
	}
	
	@Override
	public Collection<Expression> getExpressions() {
		return nodes_.keySet();
	}
	
	public boolean addExpression(Expression expr) {
		if (nodes_.containsKey(expr)) {
			return false;
		}
		
		nodes_.put(expr, new LinkedList<Inference>());
		
		return true;
	}
	
	public void addInference(Inference inf) {
		rootsUpdated = false;
		
		for (Expression premise : inf.getPremises()) {
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
		
		builder.append("Roots: " + getRootExpressions() + "\n");
		
		for (Expression expr : nodes_.keySet()) {
			builder.append(expr + " => " + nodes_.get(expr) + "\n");
		}
		
		return builder.toString();
	}

	private void updateRoots() {
		for (Expression expr : nodes_.keySet()) {
			if (ProofUtils.isAsserted(expr)) {
				roots_.add(expr);
			}
		}
		
		rootsUpdated = true;
	}
	
}
