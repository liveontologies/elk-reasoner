/**
 * 
 */
package org.semanticweb.elk.proofs;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.elk.proofs.expressions.derived.DerivedExpression;
import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.elk.proofs.utils.ProofUtils;

/**
 * A simple unoptimized implementation of {@link InferenceGraph}.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class InferenceGraphImpl implements InferenceGraph {

	private final Set<DerivedExpression> roots_;
	
	private final Map<DerivedExpression, List<Inference>> nodes_;
	
	public InferenceGraphImpl() {
		roots_ = new HashSet<DerivedExpression>();
		nodes_ = new HashMap<DerivedExpression, List<Inference>>();
	}
	
	@Override
	public Collection<DerivedExpression> getRootExpressions() {
		return Collections.unmodifiableCollection(roots_);
	}

	@Override
	public Collection<Inference> getInferencesForPremise(DerivedExpression expression) {
		return nodes_.containsKey(expression) ? nodes_.get(expression) : Collections.<Inference>emptyList();
	}
	
	@Override
	public Collection<DerivedExpression> getExpressions() {
		return nodes_.keySet();
	}
	
	public boolean addExpression(DerivedExpression expr) {
		// asserted expressions are roots
		if (ProofUtils.isAsserted(expr)) {
			roots_.add(expr);
		}
		
		if (nodes_.containsKey(expr)) {
			return false;
		}
		
		nodes_.put(expr, new LinkedList<Inference>());
		
		return true;
	}
	
	public void addInference(Inference inf) {
		for (DerivedExpression premise : inf.getPremises()) {
			addExpression(premise);
			
			nodes_.get(premise).add(inf);
		}
		
		addExpression(inf.getConclusion());
		// could get inferences whose premises were all filtered out for some reason (i.e. they are all tautologies), e.g. (A <= A, A <= A) |- A <= A and A 
		if (inf.getPremises().isEmpty() || ProofUtils.isAsserted(inf.getConclusion())) {
			roots_.add(inf.getConclusion());
		}
		else {
			roots_.remove(inf.getConclusion());			
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("Roots: " + roots_ + "\n");
		
		for (DerivedExpression expr : nodes_.keySet()) {
			builder.append(expr + " => " + nodes_.get(expr) + "\n");
		}
		
		return builder.toString();
	}
	
	
}
