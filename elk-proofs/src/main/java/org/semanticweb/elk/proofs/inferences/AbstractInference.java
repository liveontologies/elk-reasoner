/**
 * 
 */
package org.semanticweb.elk.proofs.inferences;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.semanticweb.elk.proofs.expressions.derived.DerivedExpression;
import org.semanticweb.elk.proofs.utils.TautologyChecker;
import org.semanticweb.elk.util.collections.Condition;
import org.semanticweb.elk.util.collections.Operations;

/**
 * The base abstract class of all inferences.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public abstract class AbstractInference implements Inference {

	private List<DerivedExpression> premises_; 
	
	@Override
	public final Collection<? extends DerivedExpression> getPremises() {
		if (premises_ != null) {
			return premises_;
		}
		
		premises_ = new LinkedList<DerivedExpression>();
		// filtering out tautologies
		final TautologyChecker checker = new TautologyChecker();
		
		for (DerivedExpression expr : Operations.filter(getRawPremises(), new Condition<DerivedExpression>() {

			@Override
			public boolean holds(DerivedExpression premise) {
				return !premise.accept(checker, null);
			}
			
		})) {
			premises_.add(expr);
		}
		
		return premises_;
	}

	protected abstract Iterable<DerivedExpression> getRawPremises();
}
