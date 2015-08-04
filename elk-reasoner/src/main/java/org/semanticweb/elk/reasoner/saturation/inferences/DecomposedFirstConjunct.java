package org.semanticweb.elk.reasoner.saturation.inferences;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.DecomposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Subsumer;
import org.semanticweb.elk.reasoner.saturation.inferences.visitors.SubsumerInferenceVisitor;

/**
 * 
 * A {@link DecomposedSubsumer} obtained from a the first conjunct of an
 * {@link IndexedObjectIntersectionOf} {@link Subsumer}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @see IndexedObjectIntersectionOf#getFirstConjunct()
 */
public class DecomposedFirstConjunct extends AbstractDecomposedConjunct {

	public DecomposedFirstConjunct(IndexedContextRoot root,
			IndexedObjectIntersectionOf subsumer) {
		super(root, subsumer, subsumer.getFirstConjunct());
	}

	@Override
	public <I, O> O accept(SubsumerInferenceVisitor<I, O> visitor, I parameter) {
		return visitor.visit(this, parameter);
	}

}
