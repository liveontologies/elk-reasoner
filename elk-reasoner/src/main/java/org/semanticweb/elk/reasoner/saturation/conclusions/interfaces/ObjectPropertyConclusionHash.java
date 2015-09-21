package org.semanticweb.elk.reasoner.saturation.conclusions.interfaces;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObject;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ObjectPropertyConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.SubPropertyChain;
import org.semanticweb.elk.util.hashing.HashGenerator;
import org.semanticweb.elk.util.hashing.Hasher;

public class ObjectPropertyConclusionHash implements
		ObjectPropertyConclusionVisitor<Void, Integer>,
		Hasher<ObjectPropertyConclusion> {

	private static final ObjectPropertyConclusionVisitor<Void, Integer> INSTANCE_ = new ObjectPropertyConclusionHash();

	private static int combinedHashCode(int... hashes) {
		return HashGenerator.combineListHash(hashes);
	}

	public static int hashCode(ObjectPropertyConclusion conclusion) {
		return conclusion == null ? 0 : conclusion.accept(INSTANCE_, null);
	}

	private static int hashCode(Class<?> c) {
		return c.hashCode();
	}

	private static int hashCode(IndexedObject o) {
		return o.hashCode();
	}

	@Override
	public int hash(ObjectPropertyConclusion object) {
		return hashCode(object);
	}

	@Override
	public Integer visit(SubPropertyChain conclusion, Void input) {
		return combinedHashCode(hashCode(SubPropertyChain.class),
				hashCode(conclusion.getSubChain()),
				hashCode(conclusion.getSuperChain()));
	}

}
