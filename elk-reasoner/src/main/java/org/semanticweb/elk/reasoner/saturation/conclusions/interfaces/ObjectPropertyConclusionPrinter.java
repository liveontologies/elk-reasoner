package org.semanticweb.elk.reasoner.saturation.conclusions.interfaces;

import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ObjectPropertyConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.SubPropertyChain;

public class ObjectPropertyConclusionPrinter implements
		ObjectPropertyConclusionVisitor<Void, String> {

	private static ObjectPropertyConclusionPrinter INSTANCE_ = new ObjectPropertyConclusionPrinter();

	private ObjectPropertyConclusionPrinter() {

	}

	public static String toString(ObjectPropertyConclusion conclusion) {
		return conclusion.accept(INSTANCE_, null);
	}

	@Override
	public String visit(SubPropertyChain conclusion, Void input) {
		return "SubPropertyChain(" + conclusion.getSubChain() + " "
				+ conclusion.getSuperChain() + ")";
	}

}
