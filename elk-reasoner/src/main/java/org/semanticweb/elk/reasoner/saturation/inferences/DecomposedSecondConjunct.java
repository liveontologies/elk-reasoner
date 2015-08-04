package org.semanticweb.elk.reasoner.saturation.inferences;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.DecomposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Subsumer;
import org.semanticweb.elk.reasoner.saturation.inferences.visitors.SubsumerInferenceVisitor;

/**
 * 
 * A {@link DecomposedSubsumer} obtained from a the second conjunct of an
 * {@link IndexedObjectIntersectionOf} {@link Subsumer}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @see IndexedObjectIntersectionOf#getSecondConjunct()
 */
public class DecomposedSecondConjunct extends AbstractDecomposedConjunct {

	public DecomposedSecondConjunct(IndexedContextRoot root,
			IndexedObjectIntersectionOf subsumer) {
		super(root, subsumer, subsumer.getSecondConjunct());
	}

	@Override
	public <I, O> O accept(SubsumerInferenceVisitor<I, O> visitor, I parameter) {
		return visitor.visit(this, parameter);
	}

}
