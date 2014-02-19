/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.conclusions;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionEqualityChecker;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionHashGenerator;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;

/**
 * A wrapper around the underlying {@link Conclusion} which implements
 * {@code equals} and {@code hashCode}. Useful for storing and looking up
 * conclusions in collections.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ConclusionEntry implements Conclusion {

	private static final ConclusionEqualityChecker equalityChecker = new ConclusionEqualityChecker();
	private static final ConclusionHashGenerator hashGen = new ConclusionHashGenerator();
	
	private final Conclusion conclusion_;
	
	public ConclusionEntry(Conclusion c) {
		conclusion_ = c;
	}
	
	@Override
	public <I, O> O accept(ConclusionVisitor<I, O> visitor, I parameter) {
		return conclusion_.accept(visitor, parameter);
	}

	@Override
	public int hashCode() {
		return conclusion_.accept(hashGen, null);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Conclusion) {
			return conclusion_.accept(equalityChecker, (Conclusion) obj);
		}
		
		return false;
	}

	@Override
	public String toString() {
		return conclusion_.toString();
	}

	@Override
	public IndexedClassExpression getSourceRoot(IndexedClassExpression rootWhereStored) {
		return conclusion_.getSourceRoot(rootWhereStored);
	}

	@Override
	public void applyNonRedundantRules(RuleVisitor ruleAppVisitor,
			ContextPremises premises, ConclusionProducer producer) {
		conclusion_.applyNonRedundantRules(ruleAppVisitor, premises, producer);
	}

	@Override
	public void applyRedundantRules(RuleVisitor ruleAppVisitor,
			ContextPremises premises, ConclusionProducer producer) {
		conclusion_.applyRedundantRules(ruleAppVisitor, premises, producer);
	}

	@Override
	public void applyNonRedundantLocalRules(RuleVisitor ruleAppVisitor,
			ContextPremises premises, ConclusionProducer producer) {
		conclusion_.applyRedundantLocalRules(ruleAppVisitor, premises, producer);
	}

	@Override
	public void applyRedundantLocalRules(RuleVisitor ruleAppVisitor,
			ContextPremises premises, ConclusionProducer producer) {
		conclusion_.applyRedundantLocalRules(ruleAppVisitor, premises, producer);
	}
	
}
