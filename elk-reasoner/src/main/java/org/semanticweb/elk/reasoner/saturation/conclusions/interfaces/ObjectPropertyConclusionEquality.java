package org.semanticweb.elk.reasoner.saturation.conclusions.interfaces;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObject;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ObjectPropertyConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.SubPropertyChain;

public class ObjectPropertyConclusionEquality implements
		ObjectPropertyConclusionVisitor<Void, ObjectPropertyConclusion> {

	private final Object object_;

	private ObjectPropertyConclusionEquality(Object object) {
		this.object_ = object;
	}

	public static boolean equals(ObjectPropertyConclusion first, Object second) {
		return first == null ? second == null : first.accept(
				new ObjectPropertyConclusionEquality(second), null) == second;
	}

	private static boolean equals(IndexedObject first, IndexedObject second) {
		return first == second;
	}

	@Override
	public SubPropertyChain visit(SubPropertyChain conclusion, Void input) {
		if (object_ == conclusion)
			return conclusion;
		if (object_ instanceof SubPropertyChain) {
			SubPropertyChain result = (SubPropertyChain) object_;
			if (equals(result.getSubChain(), conclusion.getSubChain())
					&& equals(result.getSuperChain(),
							conclusion.getSuperChain()))
				return result;
		}
		return null;
	}

}
