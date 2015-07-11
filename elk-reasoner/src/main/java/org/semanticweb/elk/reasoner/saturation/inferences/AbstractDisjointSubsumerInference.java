package org.semanticweb.elk.reasoner.saturation.inferences;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointClassesAxiom;
import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.DisjointSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.inferences.visitors.ClassInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.inferences.visitors.DisjointSubsumerInferenceVisitor;

public abstract class AbstractDisjointSubsumerInference extends
		DisjointSubsumerImpl implements DisjointSubsumerInference {

	public AbstractDisjointSubsumerInference(IndexedContextRoot root,
			IndexedClassExpression member, IndexedDisjointClassesAxiom axiom,
			ElkAxiom reason) {
		super(root, member, axiom, reason);
	}

	@Override
	public <I, O> O accept(ClassInferenceVisitor<I, O> visitor, I parameter) {
		return accept((DisjointSubsumerInferenceVisitor<I, O>) visitor,
				parameter);
	}

}
