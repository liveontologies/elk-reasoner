package org.semanticweb.elk.reasoner.saturation.rules.subsumers;

/**
 * A visitor pattern for {@link SubsumerRule}s
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public interface SubsumerRuleVisitor extends LinkedSubsumerRuleVisitor,
		SubsumerDecompositionRuleVisitor {

}
