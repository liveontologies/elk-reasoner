/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.AbstractClassInferenceVisitor;

/**
 * The visitor which uses {@link SideConditionLookup} to expose {@link ElkAxiom}
 * s used as side conditions of the rules.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class SideConditionVisitor extends
		AbstractClassInferenceVisitor<IndexedClassExpression, Void> {

	private final SideConditionLookup lookup_;

	private final ElkAxiomVisitor<?> axiomVisitor_;

	SideConditionVisitor(ElkAxiomVisitor<?> visitor) {
		lookup_ = new SideConditionLookup();
		axiomVisitor_ = visitor;
	}

	@Override
	protected Void defaultTracedVisit(ClassInference inference,
			IndexedClassExpression root) {

		ElkAxiom sideCondition = lookup_.lookup(inference);

		if (sideCondition != null) {
			sideCondition.accept(axiomVisitor_);
		}

		return null;
	}

}
